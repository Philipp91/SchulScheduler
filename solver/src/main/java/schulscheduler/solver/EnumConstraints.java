package schulscheduler.solver;

import schulscheduler.model.base.BaseElement;

/**
 * Ein Constraint ist eine Bedingung, die für jeden gültigen Stundenplan (d.h. für jede Lösung) gelten muss. Wenn eine
 * Probleminstanz unlösbar ist, gibt es eine Menge von Bedingungen, die im Konflikt stehen. Dieser Enum dient dazu,
 * diese auflisten und dem Nutzer präsentieren zu können.
 * WICHTIG: Für jeden Constraint, der hier eingetragen wird, sollten im Kommentar die dazugehörigen Parameter in der
 * richtigen Reihenfolge sowie die Bedeutung des Constraints dokumentiert werden.
 */
public enum EnumConstraints {

    /**
     * Parameter: Unterrichtseinheit u, Stundenzahl stunden
     * Bedeutung: `u` muss `stunden` Mal pro Woche unterrichtet werden.
     */
    WOCHENSTUNDEN,

    /**
     * Parameter: Lehrer/Klasse l/k, Zeitslot z
     * Bedeutung: `l/k` kann zum Zeitslot `z` höchstens einen Unterricht halten.
     */
    KONFLIKTFREIHEIT,

    /**
     * Parameter: Unterrichtseinheit u, Zeitslot z
     * Bedeutung: `u` muss am Zeitslot `z` unterrichtet werden, weil der Nutzer `z` als fixe Stunde angegeben hat.
     */
    FIXE_STUNDE,

    /**
     * Parameter: Unterrichtseinheit u, Zeitslot z
     * Bedeutung: `u` kann am Zeitslot `z` nicht unterrichtet werden, weil diese Stunde gesperrt ist.
     */
    GESPERRTE_STUNDE,

    /**
     * Parameter: Unterrichtseinheit u, Zeitslots z1, z2
     * Bedeutung: `u` findet zu den Zeitslots `z1` und `z2` als Doppelstunde statt, oder gar nicht.
     */
    DOPPELSTUNDE,

    /**
     * Parameter: Unterrichtseinheit u, Zeitslots z1, z2
     * Bedeutung: Wenn `u` nur zu `z1` stattfindet und nicht zu `z2`, muss die Einzelstunden-Variable für `z2` gesetzt
     * werden.
     */
    EINZELSTUNDE,

    /**
     * Parameter: Unterrichtsinheit u
     * Bedeutung: Für `u` ist höchstens eine Einzelstunde erlaubt.
     */
    MAX_EINZELSTUNDEN,

    /**
     * Parameter: Klasse k, Fach f, Zeitslots z1, z2
     * Bedeutung: `k` kann in `f` höchstens zu einem der Zeitslots `z1` oder `z2` unterrichtet werden, weil das Fach
     * sonst zu häufig oder mit einer Lücke am selben Tag geplant wäre.
     */
    FACH_PRO_TAG,

    /**
     * Parameter: Lehrer l, Unterrichtseinheit u, Zeitslot z
     * Bedeutung: `l` hat zum Zeitpunkt `z` keine Zeit, also kann `u` dann nicht stattfinden.
     */
    LEHRER_NICHT_VERFUEGBAR,

    /**
     * Parameter: Klasse k, Zeitslot z
     * Bedeutung: `k` muss zum Zeitpunkt `z` Unterricht haben, also keine unbetreute Freistunde.
     */
    KERNSTUNDE,

    ; // Enum-Ende

    /**
     * @param parameters Die tatsächlichen Parameter für den Konflikt-String.
     * @return Ein Konflikt-String basierend auf dem Constraint.
     */
    public String get(Object... parameters) {
        StringBuilder result = new StringBuilder();
        result.append(name());
        for (Object parameter : parameters) {
            result.append("-");
            if (parameter instanceof BaseElement) {
                result.append(((BaseElement) parameter).toShortString());
            } else {
                result.append(parameter);
            }
        }
        return result.toString();
    }

}
