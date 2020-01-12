package schulscheduler.i18n;

/**
 * Dieser Wrapper für messages.properties sorgt dafür, dass die Texte in UTF-8 geladen werden.
 */
public class Messages extends UTF8ResourceBundle {

    /**
     * Singleton-Pattern, enthält eine Instanz. Hinweis: Es wird nicht verhindert, dass weitere Instanzen erstellt
     * werden, weil JavaFX für die FXML-Anbindung eigene Instanzen erstellen muss.
     */
    private static final Messages INSTANCE = new Messages();

    /**
     * @return Ein Message-Bundle für die aktive Locale.
     */
    public static Messages getInstance() {
        return INSTANCE;
    }

    /**
     * Default-Konstruktor, über den das UTF8_CONTROL angebunden wird.
     */
    public Messages() {
        super("messages");
    }

}
