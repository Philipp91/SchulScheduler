package schulscheduler.ui.eingabe;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import schulscheduler.model.eingabe.Eingabedaten;
import schulscheduler.model.schule.Fach;

import javax.inject.Inject;

/**
 * JavaFX-Controller für tab_faecher.fxml.
 */
public class FaecherController extends AbstractWizardController {

    @FXML
    private TableView<Fach> tblFaecher;

    @Inject
    Eingabedaten eingabedaten;

//    @Inject
//    private UndoTracker<Eingabedaten> undoTracker;

    @Inject
    public FaecherController() {

    }

    //@Override
    protected void bindComponents() {
        // Liste der Fächer ans Datenmodell anbinden
        tblFaecher.itemsProperty().bind(eingabedaten.faecherProperty());

        // Neue Zeile hinzufügen mit Enter
        tblFaecher.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                addFach();
                event.consume();
            }
        });
    }

    /**
     * Fügt ein neues Fach zur Tabelle hinzu.
     */
    @FXML
    public void addFach() {
//        undoTracker.beginMultiCommand();
//        ModelFunctions.addFach(eingabedaten);
//        undoTracker.endMultiCommand();
//        JavaFXHelper.selectAndFocusLastRow(tblFaecher);
        eingabedaten.getFaecher().add(new Fach());
    }

    /**
     * Löscht das ausgewählte Fach. Wenn keine Zeile ausgewählt ist, wird die letzte gelöscht.
     */
    @FXML
    public void deleteFach() {
        if (tblFaecher.getItems().isEmpty()) return;
        Fach fach = tblFaecher.getSelectionModel().getSelectedItem();
        if (fach == null) fach = tblFaecher.getItems().get(tblFaecher.getItems().size() - 1);
        //undoTracker.checkAndExecute(tblFaecher.getScene().getWindow(), new RemoveFachCommand(fach));
        tblFaecher.getItems().remove(fach);
    }

}
