package schulscheduler.model.schule;

import schulscheduler.i18n.EnumMessages;

/**
 * Verfügbarkeiten eines Lehrers zu einem bestimmten Zeitpunkt.
 */
public enum EnumVerfuegbarkeit {

    NORMAL, EINGESCHRAENKT, NICHT;

    private final String toString;

    /**
     * Lädt den toString-Text aus enums.properties.
     */
    private EnumVerfuegbarkeit() {
        this.toString = EnumMessages.getInstance().getString(name());
    }

    @Override
    public String toString() {
        return this.toString;
    }

}
