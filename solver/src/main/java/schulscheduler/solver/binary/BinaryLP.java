package schulscheduler.solver.binary;

import javafx.util.Pair;
import schulscheduler.collections.IDElementMap;
import schulscheduler.collections.IDElementSet;
import schulscheduler.model.eingabe.Eingabedaten;
import schulscheduler.model.ergebnis.Ergebnisdaten;
import schulscheduler.model.ergebnis.Klassenstundenplan;
import schulscheduler.model.ergebnis.Lehrerstundenplan;
import schulscheduler.model.ergebnis.Unterricht;
import schulscheduler.model.schule.Lehrer;
import schulscheduler.model.schule.Zeitslot;
import schulscheduler.model.unterricht.Klasse;
import schulscheduler.model.unterricht.Kopplung;
import schulscheduler.model.unterricht.Unterrichtseinheit;
import schulscheduler.model.unterricht.Zuweisung;
import schulscheduler.xml.Serialization;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Formuliert das Stundenplanproblem mittels Integer Linear Programming, wobei die Hauptvariablen binär sind (eine
 * Variable pro Unterrichtseinheit und Zeitslot, die bestimmt, ob die Unterrichtseinheit zu dem Zeitpunkt stattfindet).
 * <p>
 * Die Problemformulierung als binäres ILP kann dann an verschiedene ILP-Solver weitergegeben werden.
 */
public class BinaryLP {

    private final Eingabedaten eingabe;

    /**
     * Alle Variablen, die für diese Probleminstanz erzeugt wurden.
     */
    private final List<BinaryVariable> allVariables = new ArrayList<>();

    /**
     * Alle harten Bedingungen, die zu dieser Probleminstanz hinzugefügt wurden.
     */
    private final List<Constraint> constraints = new ArrayList<>();

    /**
     * Enthält die binären Hauptvariablen für das Stundenplanproblem.
     * Pro planbarer Unterrichtseinheit (normale Lehrer-Klasse-Zuweisungen nach Abzug von Kopplungen, sowie die
     * komplexeren Kopplungen) und pro Zeitslot gibt es eine binäre Variable die aussagt, ob die Unterrichtseinheit in
     * dem Zeitslot stattfindet, oder nicht.
     */
    private final Map<Unterrichtseinheit, Map<Zeitslot, BinaryVariable>> mainVariables = new IDElementMap<>();

    /**
     * Pro Klasse die Unterrichtseinheiten, an denen die Klasse beteiligt ist.
     */
    private final Map<Klasse, Set<Unterrichtseinheit>> klassenUnterricht = new IDElementMap<>();

    /**
     * Pro Lehrer die Unterrichtseinheiten, an denen der Lehrer beteiligt ist.
     */
    private final Map<Lehrer, Set<Unterrichtseinheit>> lehrerUnterricht = new IDElementMap<>();

    /**
     * Konstruktor, der das SchulScheduler-Problem in ein binäres ILP übersetzt, dass dann (direkt nach dem
     * Konstruktor-Aufruf) über die öffentlichen Getter dieser Klasse abgeholt werden kann.
     *
     * @param eingabe Die Problemformulierung im SchulScheduler-Datenmodell.
     */
    public BinaryLP(@Nonnull Eingabedaten eingabe) {
        this.eingabe = Serialization.xmlClone(Objects.requireNonNull(eingabe));
        subtractKopplungen();
        fillMaps();
        createMainVariablesAndConstraints();

        createGesperrteAndFixeStundenConstraints();
        createDoppelstundenConstraints();
        if (allVariables.isEmpty() || mainVariables.isEmpty()) {
            throw new IllegalArgumentException("Probleminstanz ist leer");
        }
    }

    private BinaryVariable addVariable(@Nonnull String name) {
        BinaryVariable variable = new BinaryVariable(name);
        allVariables.add(variable);
        return variable;
    }

    private void addConstraint(@Nonnull Constraint constraint) {
        constraints.add(constraint);
    }

    public List<BinaryVariable> getVariables() {
        return allVariables;
    }

    public List<Constraint> getConstraints() {
        return constraints;
    }

    /**
     * Zeit die gekoppelten Stunden von den ursprünglichen Lehrer-Klasse-Zuweisungen ab, sodass gekoppelte
     * Unterrichtsstunden nicht doppelt verplant werden. Entfernt die ursprünglichen Zuweisungen, die dabei leer werden.
     */
    private void subtractKopplungen() {
        for (Kopplung kopplung : eingabe.getKopplungen()) {
            for (Zuweisung zuweisung : kopplung.getZuweisungen()) {
                zuweisung.setWochenstunden(zuweisung.getWochenstunden() - kopplung.getWochenstunden());
                if (zuweisung.getWochenstunden() < 0) {
                    throw new IllegalArgumentException(zuweisung.toLongString() + " hat zu viele Kopplungen");
                }
                if (zuweisung.getWochenstunden() < zuweisung.getFixeStunden().size()) {
                    throw new IllegalArgumentException(zuweisung.toLongString() + " hat zu viele fixe Stunden nach Kopplungen");
                }
                if (zuweisung.getWochenstunden() == 0) {
                    eingabe.getZuweisungen().remove(zuweisung);
                }
            }
        }
        for (Zuweisung zuweisung : eingabe.getZuweisungen()) {
            if (zuweisung.getWochenstunden() != 0 && zuweisung.getLehrer() == null) {
                throw new IllegalArgumentException(zuweisung.toLongString() + " braucht einen Lehrer");
            }
        }
    }

    /**
     * @return Alle Zuweisungen und Kopplungen, d.h. alles was verplant werden muss.
     */
    private Stream<Unterrichtseinheit> getUnterrichtseinheiten() {
        return Stream.concat(eingabe.getZuweisungen().stream(), eingabe.getKopplungen().stream());
    }

    /**
     * Füllt {@link #klassenUnterricht} und {@link #lehrerUnterricht}.
     */
    private void fillMaps() {
        eingabe.getKlassen().forEach(klasse -> klassenUnterricht.put(klasse, new IDElementSet<>()));
        eingabe.getLehrer().forEach(lehrer -> lehrerUnterricht.put(lehrer, new IDElementSet<>()));
        getUnterrichtseinheiten().forEach(einheit -> {
            einheit.getAllKlassen().forEach(klasse -> klassenUnterricht.get(klasse).add(einheit));
            einheit.getAllLehrer().forEach(lehrer -> lehrerUnterricht.get(lehrer).add(einheit));
        });
    }

    /**
     * Füllt {@link #mainVariables} mit einer binären Variable pro Zuweisung/Kopplung und Zeitslot.
     */
    private void createMainVariablesAndConstraints() {
        getUnterrichtseinheiten().forEach(einheit -> {
            if (mainVariables.containsKey(einheit)) throw new AssertionError();
            if (einheit.getWochenstunden() == 0) throw new AssertionError();
            Map<Zeitslot, BinaryVariable> einheitVars = new IDElementMap<>();
            mainVariables.put(einheit, einheitVars);
            for (Zeitslot zeitslot : eingabe.getZeitslots()) {
                einheitVars.put(zeitslot, addVariable(einheit.toShortString() + "-" + zeitslot.toShortString()));
            }
            // Diese Unterrichtseinheit muss genau #Wochenstunden Mal stattfinden.
            addConstraint(new SumEq("Wochenstunden-" + einheit.toShortString(),
                    new ArrayList<>(einheitVars.values()), einheit.getWochenstunden()));
        });

        for (var zeitslot : eingabe.getZeitslots()) {
            Stream.concat(klassenUnterricht.entrySet().stream(), lehrerUnterricht.entrySet().stream()).forEach(entry -> {
                // Der Lehrer bzw. die Klasse kann am gegebenen Zeitslot an maximal einem Unterricht teilnehmen.
                addConstraint(new SumLeq(entry.getKey().toShortString() + "-Konflikt",
                        entry.getValue().stream().map(einheit -> mainVariables.get(einheit).get(zeitslot)).collect(Collectors.toList()), 1));
            });
        }
    }

    /**
     * Harte Bedingung: An gesperrten Zeitslots darf kein Unterricht stattfinden.
     * Harte Bedingung: Fixe Stunden müssen genau zum fixierten Zeitpunkt stattfinden.
     */
    private void createGesperrteAndFixeStundenConstraints() {
        for (var einheitVars : mainVariables.entrySet()) {
            Unterrichtseinheit einheit = einheitVars.getKey();
            for (var zeitslotVar : einheitVars.getValue().entrySet()) {
                Zeitslot zeitslot = zeitslotVar.getKey();
                Boolean forcedValue = null;
                if (einheit.getFixeStunden().contains(zeitslot)) {
                    if (zeitslot.isGesperrt()) {
                        throw new IllegalArgumentException("Fixe Stunde auf gesperrtem Zeitslot");
                    }
                    forcedValue = true;
                } else if (zeitslot.isGesperrt()) {
                    forcedValue = false;
                }
                if (forcedValue != null) {
                    addConstraint(new ForceValue(
                            "Gesperrt-" + einheit.toShortString() + "-" + zeitslot.toShortString(),
                            zeitslotVar.getValue(), forcedValue));
                }
            }
        }
    }

    /**
     * Harte Bedingung: In Stunden-Paaren, die als Doppelstunde markiert sind, muss derselbe Unterricht in beiden
     * Stunden (oder in keiner) stattfinden. Ausnahme: Unterrichte mit insgesamt ungerader Stundenzahl dürfen diese
     * Regel ein Mal (in der ganzen Woche) verletzen.
     */
    private void createDoppelstundenConstraints() {
        Map<Zeitslot, Zeitslot> doppelstunden = eingabe.getZeitslots().stream()
                .map(z1 -> new Pair<>(z1, eingabe.getZeitslots().stream().filter(z1::isDoppelstundeWith).findAny()))
                .filter(p -> p.getValue().isPresent())
                .collect(Collectors.toUnmodifiableMap(Pair::getKey, p -> p.getValue().get()));
        getUnterrichtseinheiten().forEach(einheit -> createDoppelstundenConstraints(doppelstunden, einheit));
    }

    private void createDoppelstundenConstraints(@Nonnull Map<Zeitslot, Zeitslot> doppelstunden,
                                                @Nonnull Unterrichtseinheit einheit) {
        final Map<Zeitslot, BinaryVariable> mainVars = mainVariables.get(einheit);

        // Wenn die Stundenzahl insgesamt ungerade ist, muss eine Einzelstunde toleriert werden.
        int erlaubteEinzelstunden = einheit.getWochenstunden() % 2 == 0 ? 0 : 1;

        // Jede fixe Stunde, die eine Doppelstunde sein müsste, bei der aber die andere Stunde nicht auch fixiert ist,
        // muss durch eine normal planbare Stunde ausgeglichen werden. Wenn es zu wenige normal planbare Stunden gibt,
        // müssen ggf. mehr Einzelstunden toleriert werden. Im Extremfall sind alle fixierten Stunden einzeln.
        int fixeEinzelstunden = (int) einheit.getFixeStunden().stream()
                .filter(doppelstunden::containsKey) // Müsste eine Doppelstunde sein
                .filter(z1 -> !einheit.getFixeStunden().contains(doppelstunden.get(z1))) // Andere ist nicht fixiert
                .count();
        int nichtfixierteStunden = einheit.getWochenstunden() - einheit.getFixeStunden().size();
        erlaubteEinzelstunden += Math.max(0, fixeEinzelstunden - nichtfixierteStunden);

        if (erlaubteEinzelstunden == 0) {
            // Im einfachen Fall können die Doppelstunden einfach erzwungen werden.
            for (Map.Entry<Zeitslot, Zeitslot> doppelstunde : doppelstunden.entrySet()) {
                Zeitslot z1 = doppelstunde.getKey();
                Zeitslot z2 = doppelstunde.getValue();
                if (z1.compareTo(z2) < 0) { // Doppelte Constraints verhindern, weil alle Paare doppelt vorhanden sind.
                    addConstraint(new VarEq(
                            "Doppelstunde-" + einheit.toShortString() + "-" + z1.toShortString(),
                            mainVars.get(z1), mainVars.get(z2)
                    ));
                }
            }
        } else {
            // Wenn Einzelstunden erlaubt sind, müssen sie gezählt werden.
            // Pro Zeitslot, der eine Einzelstunde sein kann, gibt es eine binäre Hilfsvariable, die auf wahr
            // gesetzt wird, wenn es sich um eine Einzelstunde handelt. Wenn der Unterricht in z1 stattfindet,
            // dann muss es eine Einzelstunde sein, oder der Unterricht muss auch in z2 stattfinden.
            final List<BinaryVariable> einzelstundenVars = new ArrayList<>();
            // Hinweis: Diese Schleife wird pro Zeitslot-Paar zwei Mal durchlaufen, jeder Zeitslot ist mal der Key.
            for (Map.Entry<Zeitslot, Zeitslot> doppelstunde : doppelstunden.entrySet()) {
                Zeitslot z1 = doppelstunde.getKey();
                Zeitslot z2 = doppelstunde.getValue();
                String name = "Einzelstunde-" + einheit.toShortString() + "-" + z1.toShortString();
                BinaryVariable einzelstunde = addVariable(name);
                einzelstundenVars.add(einzelstunde);
                addConstraint(new VarImpliesOr(
                        name + "-Constraint",
                        mainVars.get(z1), // Wenn erster Slot stattfindet, dann
                        Arrays.asList(
                                mainVars.get(z2),  // muss der zweite auch stattfinden
                                einzelstunde // oder es muss sich um eine Einzelstunde handeln.
                        )
                ));
            }
            addConstraint(new SumLeq("MaxEinzelstunden-" + einheit.toShortString(), einzelstundenVars, erlaubteEinzelstunden));
        }
    }

    /**
     * Harte Bedingung: Eine Klasse darf maximal 2 Stunden pro Tag im selben Fach haben. Ausnahme: Fixe Stunden
     * Harte Bedingung: Wenn es 2 Stunden an einem Tag sind, müssen sie aufeinanderfolgend sein. Ausnahme: Gesperrte Zeitslots.
     */
    private void createFachProTagConstraints() {

    }

    /**
     * Kann aufgerufen werden, sobald für alle Variablen, die von {@link #getVariables()} zurückgegeben werden, mittels
     * {@link BinaryVariable#setSolution(boolean)} eine Lösung angegeben wurde.
     *
     * @return Ein neues Ergebnisdaten-Objekt, das die Lösung im SchulScheduler-Datenmodell darstellt.
     */
    @Nonnull
    public Ergebnisdaten createErgebnis() {
        Ergebnisdaten ergebnis = new Ergebnisdaten();
        ergebnis.setEingabedaten(eingabe);
        for (var einheitEntry : mainVariables.entrySet()) {
            Unterrichtseinheit einheit = einheitEntry.getKey();
            for (var zeitslotEntry : einheitEntry.getValue().entrySet()) {
                if (zeitslotEntry.getValue().requireSolution()) { // Der Unterricht soll an diesem Zeitpunkt stattfinden
                    Unterricht unterricht = new Unterricht();
                    unterricht.setZeitslot(zeitslotEntry.getKey());
                    unterricht.getKlassen().setAll(einheit.getAllKlassen().collect(Collectors.toList()));
                    unterricht.getLehrer().setAll(einheit.getAllLehrer().collect(Collectors.toList()));
                    unterricht.getFaecher().setAll(einheit.getAllFaecher().collect(Collectors.toList()));
                    ergebnis.getUnterricht().add(unterricht);
                }
            }
        }
        for (Klasse klasse : eingabe.getKlassen()) {
            var unterrichte = ergebnis.getUnterricht().filtered(u -> u.getKlassen().contains(klasse));
            if (!unterrichte.isEmpty()) {
                ergebnis.getKlassenStundenplaene().add(new Klassenstundenplan(klasse, unterrichte));
            }
        }
        for (Lehrer lehrer : eingabe.getLehrer()) {
            var unterrichte = ergebnis.getUnterricht().filtered(u -> u.getLehrer().contains(lehrer));
            if (!unterrichte.isEmpty()) {
                ergebnis.getLehrerStundenplaene().add(new Lehrerstundenplan(lehrer, unterrichte));
            }
        }
        return ergebnis;
    }

}
