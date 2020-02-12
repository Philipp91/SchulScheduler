package schulscheduler.ui.eingabe;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import schulscheduler.model.eingabe.Eingabedaten;
import schulscheduler.ui.BaseWindowController;

import javax.inject.Inject;

/**
 * JavaFX-Controller für window_eingabe.fxml.
 */
public class EingabeWindowController extends BaseWindowController<Eingabedaten> {

    @Inject
    public EingabeWindowController() {
    }

    @FXML
    TabPane mainTabPane;

}
