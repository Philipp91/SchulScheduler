package schulscheduler.i18n;

/**
 * Wrapper um enums.properties, damit die Werte mit UTF-8 geladen werden.
 */
public class EnumMessages extends UTF8ResourceBundle {

    /**
     * Singleton-Pattern, enthält eine Instanz.
     */
    private static final EnumMessages INSTANCE = new EnumMessages();

    /**
     * @return Ein Message-Bundle für die aktive Locale.
     */
    public static EnumMessages getInstance() {
        return INSTANCE;
    }

    /**
     * Default-Konstruktor, über den das UTF8_CONTROL angebunden wird.
     */
    public EnumMessages() {
        super("enums");
    }

}
