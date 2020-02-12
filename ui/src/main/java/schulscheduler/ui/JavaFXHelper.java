package schulscheduler.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.util.Callback;
import schulscheduler.i18n.Messages;
import schulscheduler.ui.eingabe.EingabeWindowController;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Enthält Hilfs-Methoden zur Erstellung von UIs mit JavaFX.
 */
public final class JavaFXHelper {

    // Legt die Fenstergröße der Dialoge fest, die durch showMessage und showConfirmMessage erstellt werden
    private static final int MAX_MSG_WIDTH = 700;
    private static final int MIN_MSG_WIDTH = 400;
    private static final int MIN_MSG_HEIGHT = 100;

    public static final Image WINDOW_ICON = new Image("icons/logo_black.png");

    /**
     * Privater Default-Konstruktor um zu verhindern, dass die Klasse instanziiert wird.
     */
    private JavaFXHelper() {
    }

    /**
     * Das Ressourcen-Verzeichnis in dem alle fxml-Dateien liegen.
     */
    public static final String FXML_ROOT = "/fxml/";

    /**
     * Das Stylesheet das in allen Fenstern standardmäßig eingebunden wird.
     */
    public static final String BASE_STYLESHEET = "/styles/styles.css";

    /**
     * Ermittelt das Wurzelverzeichnis, in dem die fxml-Dateien liegen (also das Verzeichnis /src/main/resources/fxml/.
     *
     * @return FXML-Wurzelverzeichnis
     */
    public static URL getFXMLResourceRoot() {
        return JavaFXHelper.class.getResource(FXML_ROOT);
    }

    /**
     * Lädt Fonts, sodass sie global (insbesondere dann auch über CSS) zur Verfügung stehen. Diese Methode sollte
     * möglichst nur ein Mal aufgerufen werden (mehrfach wäre Rechenzeit-Verschwendung).
     */
    public static void loadFonts() {
        Font.loadFont(JavaFXHelper.class.getResourceAsStream("/fonts/Bitter-Regular.ttf"), 40);
    }

    public static FXMLLoader createLoader(Callback<Class<?>, Object> controllerFactory) {
        FXMLLoader loader = new FXMLLoader(getFXMLResourceRoot());
        loader.setResources(Messages.getInstance());
        loader.setControllerFactory(controllerFactory);
        return loader;
    }

    /**
     * Lädt den Inhalt einer FXML-Datei und gibt ihn als JavaFX-Knoten zurück.
     *
     * @param fxmlName Der Name der zu ladenden Datei (wird in {@link #FXML_ROOT} gesucht).
     * @return Der Vaterknoten der geladenen UI-Struktur aus der FXML-Datei.
     * @throws IOException Wenn beim Laden der Datei ein Fehler auftritt.
     */
    public static Parent loadFXML(String fxmlName, Callback<Class<?>, Object> controllerFactory) throws IOException {
        InputStream inputStream = JavaFXHelper.class.getResourceAsStream(FXML_ROOT + fxmlName);
        if (inputStream == null) {
            throw new FileNotFoundException("FXML file with name " + fxmlName + " not found!");
        }
        return (Parent) createLoader(controllerFactory).load(inputStream);
    }

}
