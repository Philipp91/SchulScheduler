package schulscheduler.ui.windows;

import javafx.scene.Parent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

/**
 * Enthält die folgenden Informationen zu einem geöffneten Fenster: TODO Update
 * <ul>
 * <li>Referenz auf den BeanStore (für CDI), der alle zugehörigen {@literal @}{@link WindowScoped} Beans enthält.</li>
 * <li>Referenz auf die JavaFX Stage (das für den Nutzer sichtbare Fenster).</li>
 * <li>Referenz auf den zugehörigen Datensatz (model).</li>
 * <li>evtl. Referenz zu einer Datei (file).</li>
 * </ul>
 * <br>
 * <b>WICHTIG:</b> Alle Kindklassen sollten unbedingt mit {@literal @}{@link WindowScoped} markiert werden.
 * 
 * @param <T> Der Typ des zugehörigen Datensatzes.
 */
public abstract class AbstractWindowComponent<T> {

    private Stage stage;
    @Inject
    T model;

    public abstract Parent createUI() throws IOException;

    /**
     * @return Das zugehörige JavaFX Fenster.
     */
    public Stage getStage() {
        return this.stage;
    }

    /**
     * Diese Funktion sollte nur vom WindowManager verwendet werden.
     * 
     * @param stage Das JavaFX-Fenster.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

}
