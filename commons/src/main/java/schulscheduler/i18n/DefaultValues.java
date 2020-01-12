package schulscheduler.i18n;

/**
 * Wrapper um default.properties, damit die Werte mit UTF-8 geladen werden.
 */
public class DefaultValues extends UTF8ResourceBundle {

    /**
     * Singleton-Pattern, enthält eine Instanz.
     */
    private static final DefaultValues INSTANCE = new DefaultValues();

    /**
     * @return Ein Resource-Bundle für die aktive Locale.
     */
    public static DefaultValues getInstance() {
        return INSTANCE;
    }

    /**
     * Default-Konstruktor, über den das UTF8_CONTROL angebunden wird.
     */
    public DefaultValues() {
        super("default");
    }

}
