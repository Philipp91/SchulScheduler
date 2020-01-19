package schulscheduler.solver.binary;

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
import java.util.*;
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

    public BinaryLP(@Nonnull Eingabedaten eingabe) {
        this.eingabe = Serialization.xmlClone(Objects.requireNonNull(eingabe));
        subtractKopplungen();
        fillMaps();
        createMainVariablesAndConstraints();
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
     * Füllt {@link #klassenUnterricht} und {@link #lehrerUnterricht}.
     */
    private void fillMaps() {
        eingabe.getKlassen().forEach(klasse -> klassenUnterricht.put(klasse, new IDElementSet<>()));
        eingabe.getLehrer().forEach(lehrer -> lehrerUnterricht.put(lehrer, new IDElementSet<>()));
        Stream.concat(eingabe.getZuweisungen().stream(), eingabe.getKopplungen().stream()).forEach(einheit -> {
            einheit.getAllKlassen().forEach(klasse -> klassenUnterricht.get(klasse).add(einheit));
            einheit.getAllLehrer().forEach(lehrer -> lehrerUnterricht.get(lehrer).add(einheit));
        });
    }

    /**
     * Füllt {@link #mainVariables} mit einer binären Variable pro Zuweisung/Kopplung und Zeitslot.
     */
    private void createMainVariablesAndConstraints() {
        Stream.concat(eingabe.getZuweisungen().stream(), eingabe.getKopplungen().stream()).forEach(einheit -> {
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
