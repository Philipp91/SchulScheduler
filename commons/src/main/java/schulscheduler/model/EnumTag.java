package schulscheduler.model;

import schulscheduler.i18n.EnumMessages;

/**
 * Die Wochentage
 */
public enum EnumTag {
    MONTAG, DIENSTAG, MITTWOCH, DONNERSTAG, FREITAG;

    private final String shortForm;
    private final String longForm;

    /**
     * Lädt die Kurz- und Lang-Form aus enums.properties.
     */
    private EnumTag() {
        this.shortForm = EnumMessages.getInstance().getString(name() + "_short");
        this.longForm = EnumMessages.getInstance().getString(name() + "_long");
    }

    @Override
    public String toString() {
        return this.shortForm;
    }

    /**
     * @return Die Abkürzung des Tages (z.B. "Mo"). Siehe enums.properties.
     */
    public String getShortForm() {
        return this.shortForm;
    }

    /**
     * @return Der normale Name des Tages (z.B. "Montag"). Siehe enums.properties.
     */
    public String getLongForm() {
        return this.longForm;
    }

}
