package schulscheduler.solver.binary;

import javafx.util.Pair;
import schulscheduler.collections.IDElementMap;
import schulscheduler.collections.IDElementSet;
import schulscheduler.model.eingabe.Eingabedaten;
import schulscheduler.model.eingabe.EnumGewichtung;
import schulscheduler.model.ergebnis.Ergebnisdaten;
import schulscheduler.model.ergebnis.Klassenstundenplan;
import schulscheduler.model.ergebnis.Lehrerstundenplan;
import schulscheduler.model.ergebnis.Unterricht;
import schulscheduler.model.schule.EnumVerfuegbarkeit;
import schulscheduler.model.schule.EnumWochentag;
import schulscheduler.model.schule.Fach;
import schulscheduler.model.schule.Lehrer;
import schulscheduler.model.schule.Zeitslot;
import schulscheduler.model.unterricht.Klasse;
import schulscheduler.model.unterricht.Kopplung;
import schulscheduler.model.unterricht.Unterrichtseinheit;
import schulscheduler.model.unterricht.Zuweisung;
import schulscheduler.solver.EnumConstraints;
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
        this.eingabe.getZeitslots().sort(null);
        subtractKopplungen();
        fillMaps();
        createMainVariablesAndConstraints();

        createGesperrteAndFixeStundenConstraints();
        createDoppelstundenConstraints();
        createFachProTagConstraints();
        createLehrerVerfuegbarkeitConstraints();
        createUnterrichtsPrioritaetConstraints();
        createHarteFaecherFolgenConstraints(eingabe.getParameter().getHarteFaecherFolgen());
        createHarteFaecherNachmittagsConstraints(eingabe.getParameter().getWeicheNachmittagsFaecher());
        // TODO Lehrer-Hohlstunden-Vermeidung
        // TODO Freier Tag für Lehrer

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
     * @param wochentag Ein Wochentag.
     * @return Alle Zeitslots für diesen Wochentag, chronologisch sortiert.
     */
    private List<Zeitslot> getZeitslotsForTag(EnumWochentag wochentag) {
        return eingabe.getZeitslots().stream().filter(z -> z.getWochentag() == wochentag).collect(Collectors.toList());
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
            addConstraint(new SumEq(EnumConstraints.WOCHENSTUNDEN.get(einheit, einheit.getWochenstunden()),
                    new ArrayList<>(einheitVars.values()), einheit.getWochenstunden()));
        });

        for (var zeitslot : eingabe.getZeitslots()) {
            Stream.concat(klassenUnterricht.entrySet().stream(), lehrerUnterricht.entrySet().stream()).forEach(entry -> {
                // Der Lehrer bzw. die Klasse kann am gegebenen Zeitslot an maximal einem Unterricht teilnehmen.
                addConstraint(new SumLeq(EnumConstraints.KONFLIKTFREIHEIT.get(entry.getKey(), zeitslot),
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
                if (einheit.getFixeStunden().contains(zeitslot)) {
                    if (zeitslot.isGesperrt()) {
                        throw new IllegalArgumentException("Fixe Stunde auf gesperrtem Zeitslot");
                    }
                    addConstraint(new ForceValue(
                            EnumConstraints.FIXE_STUNDE.get(einheit, zeitslot),
                            zeitslotVar.getValue(), true));
                } else if (zeitslot.isGesperrt()) {
                    addConstraint(new ForceValue(
                            EnumConstraints.GESPERRTE_STUNDE.get(einheit, zeitslot),
                            zeitslotVar.getValue(), false));
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

    /**
     * Fügt DoppelstundenConstraints (siehe oben) für eine Unterrichtseinheit hinzu.
     *
     * @param doppelstunden Die Doppelstunden, wobei jedes Paar doppelt vorkommt (als A->B und als B->A).
     * @param einheit Die Unterrichtseinheit, für die die Constraints hinzugefügt werden sollen.
     */
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
                            EnumConstraints.DOPPELSTUNDE.get(einheit, z1, z2),
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
                String name = EnumConstraints.EINZELSTUNDE.get(einheit, z1);
                BinaryVariable einzelstunde = addVariable(name + "-Var");
                einzelstundenVars.add(einzelstunde);
                addConstraint(new VarImpliesOr(
                        name,
                        mainVars.get(z1), // Wenn erster Slot stattfindet, dann
                        Arrays.asList(
                                mainVars.get(z2),  // muss der zweite auch stattfinden
                                einzelstunde // oder es muss sich um eine Einzelstunde handeln.
                        )
                ));
            }
            addConstraint(new SumLeq(EnumConstraints.MAX_EINZELSTUNDEN.get(einheit), einzelstundenVars, erlaubteEinzelstunden));
        }
    }

    /**
     * Harte Bedingung: Eine Klasse darf maximal 2 Stunden pro Tag im selben Fach haben.
     * Harte Bedingung: Wenn es 2 Stunden an einem Tag sind, müssen sie aufeinanderfolgend sein.
     * Ausnahme: Fixe Stunden
     * Hinweis: Dieser Constraint ist pro Fach, d.h er trifft auch zu, wenn ein Fach für eine Klasse in mehreren
     * Zuweisungen und/oder Kopplungen auftritt.
     */
    private void createFachProTagConstraints() {
        final int maxWochenstunden = 2 * EnumWochentag.values().length;
        getUnterrichtseinheiten().filter(u -> u.getWochenstunden() > maxWochenstunden).forEach(u -> {
            throw new IllegalArgumentException("Mehr als " + maxWochenstunden + " Wochenstunden werden derzeit nicht unterstützt");
        });
        for (Klasse klasse : eingabe.getKlassen()) {
            for (Fach fach : eingabe.getFaecher()) {
                List<Unterrichtseinheit> unterrichte = getUnterrichtseinheiten()
                        .filter(u -> u.hasKlasse(klasse) && u.hasFach(fach)).collect(Collectors.toList());
                if (unterrichte.isEmpty()) continue;
                for (EnumWochentag wochentag : EnumWochentag.values()) {
                    createFachProTagConstraints(klasse, fach, unterrichte, wochentag);
                }
            }
        }
    }

    /**
     * Erstellt den Fach-pro-Tag Constraint (siehe oben) für einen Wochentag und eine Klasse-Fach-Kombination.
     * Umsetzung: Wenn am gegebenen Wochentag der Unterricht zur Stunde N stattfindet, dann darf er nicht zu den Stunden
     * M mit M>N+1 stattfinden. Mit anderen Worten: Nur der Zeitslot unmittelbar danach ist erlaubt. Damit ist auch
     * sichergestellt, dass es insgesamt höchstens 2 Stunden pro Tag sein können.
     * Wenn es eine fixe Stunde an dem Tag gibt, funktioniert der Constraint normal und stellt einfach sicher, dass eine
     * zweite Stunden an dem Tag wenn überhaupt nur direkt vor oder nach der fixen Stunde stattfindet.
     * Wenn zwei oder mehr Stunden an dem Tag fixiert sind, ist der Constraint überflüssig.
     *
     * @param klasse Die Klasse, zu der die `unterrichte` gehören.
     * @param fach Das Fach, zu dem die `unterrichte` gehören.
     * @param unterrichte Die Unterrichte von einer Klasse in einem bestimmten Fach.
     * @param wochentag Der Wochentag, für den die Constraints hinzugefügt werden.
     */
    private void createFachProTagConstraints(@Nonnull Klasse klasse, @Nonnull Fach fach,
                                             @Nonnull List<Unterrichtseinheit> unterrichte,
                                             @Nonnull EnumWochentag wochentag) {
        final long totalFixeStunden = unterrichte.stream()
                .mapToLong(u -> u.getFixeStunden().stream().filter(z -> z.getWochentag() == wochentag).count()).sum();
        if (totalFixeStunden >= 2) {
            return;
        }

        List<Zeitslot> zeitslots = getZeitslotsForTag(wochentag);
        for (Zeitslot oneslot : zeitslots) {
            zeitslots.stream()
                    .filter(other -> other.getStunde().getNummer() > oneslot.getStunde().getNummer() + 1)  // M>N+1
                    .forEach(laterslot -> {
                        // oneslot -> -laterslot  <==>  -oneslot \/ -laterslot  <==>  (oneslot+laterslot) <= 1
                        addConstraint(new SumLeq(
                                EnumConstraints.FACH_PRO_TAG.get(klasse, fach, oneslot, laterslot),
                                Stream.concat(
                                        unterrichte.stream().map(u -> mainVariables.get(u).get(oneslot)),
                                        unterrichte.stream().map(u -> mainVariables.get(u).get(laterslot))
                                ).collect(Collectors.toList()), 1));
                    });
        }
    }

    /**
     * Harte Bedingung: Zu Zeitslots, zu denen ein Lehrer {@link EnumVerfuegbarkeit#NICHT} verfügbar ist, kann kein
     * Unterricht für ihn stattfinden.
     * Weiche Bedingung: Wenn ein Lehrer {@link EnumVerfuegbarkeit#EINGESCHRAENKT} verfügbar ist, soll der Unterricht
     * wenn möglich nicht dann geplant werden.
     */
    private void createLehrerVerfuegbarkeitConstraints() {
        for (Lehrer lehrer : eingabe.getLehrer()) {
            //noinspection CodeBlock2Expr
            lehrer.getVerfuegbarkeit().stream().filter(v -> v.getVerfuegbarkeit() != EnumVerfuegbarkeit.NORMAL).forEach(verfEntry -> {
                getUnterrichtseinheiten().filter(u -> u.hasLehrer(lehrer)).forEach(unterricht -> {
                    BinaryVariable variable = mainVariables.get(unterricht).get(verfEntry.getZeitslot());
                    if (verfEntry.getVerfuegbarkeit() == EnumVerfuegbarkeit.NICHT) {
                        addConstraint(new ForceValue(
                                EnumConstraints.LEHRER_NICHT_VERFUEGBAR.get(lehrer, verfEntry.getZeitslot()),
                                variable, false));
                    } else if (verfEntry.getVerfuegbarkeit() == EnumVerfuegbarkeit.EINGESCHRAENKT) {
                        variable.addObjectiveFactor(-1.0);
                    } else {
                        throw new AssertionError();
                    }
                });
            });
        }
    }

    /**
     * Harte Bedingung: Bei Stunden/Zeitslots mit Unterrichtspriorität MAXIMAL (Pflicht/Kernstunde) muss pro Klasse
     * mindestens einer der möglichen Unterrichte stattfinden.
     * Weiche Bedingung: Bei anderen Prioritäten werden die Unterrichte entsprechend bevorzugt oder nicht.
     */
    private void createUnterrichtsPrioritaetConstraints() {
        for (Zeitslot zeitslot : eingabe.getZeitslots()) {
            double gewicht;
            switch (zeitslot.getStunde().getUnterrichtsprioritaet()) {
                case MAXIMAL:
                    // Harte Bedingung pro Klasse.
                    for (Klasse klasse : eingabe.getKlassen()) {
                        List<BinaryVariable> klasseZeitslotVars = getUnterrichtseinheiten()
                                .filter(u -> u.hasKlasse(klasse))
                                .map(u -> mainVariables.get(u).get(zeitslot))
                                .collect(Collectors.toList());
                        addConstraint(new SumGeq(EnumConstraints.KERNSTUNDE.get(klasse, zeitslot), klasseZeitslotVars, 1));
                    }
                    continue;
                case NULL:
                case MITTEL:
                    continue; // Keine Beeinflussung.
                case NIEDRIG:
                    gewicht = -1;
                    break;
                case HOCH:
                    gewicht = 1;
                    break;
                case SEHR_HOCH:
                    gewicht = 2;
                    break;
                default:
                    throw new AssertionError();
            }
            // Weiche Bedingung eigentlich auch pro Klasse, aber äquivalent implementiert als pro Unterricht,
            // gewichtet nach Anzahl teilnehmender Klassen.
            getUnterrichtseinheiten().forEach(einheit ->
                    mainVariables.get(einheit).get(zeitslot).addObjectiveFactor(gewicht * einheit.getAllKlassen().count()));
        }
    }

    /**
     * Weiche Bedingung: Vier aufeinanderfolgende harte Fächer vermeiden.
     *
     * @param gewichtung Die Gewichtung dieses Constraints.
     */
    private void createHarteFaecherFolgenConstraints(EnumGewichtung gewichtung) {
        if (gewichtung == null || gewichtung == EnumGewichtung.NULL) return;
        final double factor = -convertGewichtungToFactor(gewichtung);
        // Maximal 3 von vier aufeinanderfolgenden Stunden dürfen ein hartes Fach sein.
        // TOOD Vier Stunden zählen nicht als aufeinanderfolgend wenn sie die Mittagspause überlappen.
        final int windowSize = 4;
        final int maxHarteFaecher = 3;
        for (EnumWochentag wochentag : EnumWochentag.values()) {
            List<Zeitslot> tagZeitslots = getZeitslotsForTag(wochentag);
            for (int beginIndex = 0; beginIndex <= tagZeitslots.size() - windowSize; beginIndex++) {
                List<Zeitslot> zeitslots = tagZeitslots.subList(beginIndex, beginIndex + windowSize);
                for (Klasse klasse : eingabe.getKlassen()) {
                    List<BinaryVariable> windowVars = mainVariables.entrySet().stream()
                            .filter(entry -> entry.getKey().hasKlasse(klasse) && entry.getKey().isHart())
                            .flatMap(entry -> zeitslots.stream().map(z -> entry.getValue().get(z)))
                            .collect(Collectors.toList());
                    if (windowVars.isEmpty()) continue;
                    String name = EnumConstraints.MAX_HARTE_FAECHER.get(klasse, zeitslots.get(0));
                    // Wenn die Toleranz-Variable auf 1 gesetzt wird (was Zielfunktion kostet), dann sind 3+1=4 harte
                    // Fächer in der 4er-Sequenz erlaubt, also wäre der Constraint dann gegen Bezahlung verletzt.
                    BinaryVariable toleranceVars = addVariable(name + "-Tolerance");
                    toleranceVars.addObjectiveFactor(factor);
                    windowVars.add(toleranceVars);
                    addConstraint(new SumLeq(name, windowVars, maxHarteFaecher));
                }
            }
        }
    }

    /**
     * Weiche Bedingung: Harte Fächer am Nachmittag vermeiden.
     * TODO Eine Stunde sollte wissen, ob sie am Vormittag/Nachmittag ist, sodass man hier nicht 6 hartkodieren muss.
     *
     * @param gewichtung Die Gewichtung dieses Constraints.
     */
    private void createHarteFaecherNachmittagsConstraints(EnumGewichtung gewichtung) {
        if (gewichtung == null || gewichtung == EnumGewichtung.NULL) return;
        final double factor = -convertGewichtungToFactor(gewichtung);
        // Harte Fächer am Nachmittag vermeiden.
        final int numVormittagsStunden = 6;
        mainVariables.entrySet().stream()
                .filter(einheitEntry -> einheitEntry.getKey().isHart())
                .forEach(einheitEntry -> einheitEntry.getValue().entrySet().stream()
                        .filter(entry -> entry.getKey().getStunde().getNummer() > numVormittagsStunden)
                        .forEach(zeitslotEntry -> zeitslotEntry.getValue().addObjectiveFactor(factor)));
    }

    private static double convertGewichtungToFactor(EnumGewichtung gewichtung) {
        switch (gewichtung) {
            case NIEDRIG:
                return 1;
            case MITTEL:
                return 2;
            case HOCH:
                return 4;
            default:
                throw new IllegalArgumentException("Expected one of BERECHNUNG_PRIORITAET_VALUES, got " + gewichtung);
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
