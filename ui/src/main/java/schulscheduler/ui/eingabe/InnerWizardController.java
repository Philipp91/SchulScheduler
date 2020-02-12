package schulscheduler.ui.eingabe;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import schulscheduler.ui.controls.TextFlowNode;

import javax.inject.Inject;

/**
 * JavaFX-Controller für alle Tabs, die (am oberen Rand) den Wizard verwenden. ACHTUNG: Diesen Controller darf man nie
 * WindowScoped machen!
 */
public class InnerWizardController {

    @FXML
    private TextFlowNode wizardText;

    @FXML
    private Button undoButton, redoButton, backButton, forwardButton;

    @Inject
    EingabeWindowController eingabeWindowController;

//    @Inject
//    UndoTracker<Eingabedaten> undoTracker;

    @Inject
    public InnerWizardController() {
    }

    /**
     * Bindet die Komponenten an.
     */
    public void initialize() {
//        undoButton.disableProperty().bind(undoTracker.canUndoProperty().not());
//        redoButton.disableProperty().bind(undoTracker.canRedoProperty().not());
    }

    /**
     * Zeigt den neuen Text auf dem Wizard an.
     *
     * @param newText Der neue Hilfstext für den Nutzer.
     */
    public void setWizardText(String newText) {
        wizardText.setText(newText);
    }

    /**
     * Blendet den Zurück-Button aus.
     */
    public void hideBackButton() {
        backButton.setVisible(false);
    }

    /**
     * Blendet den Weiter-Button aus.
     */
    public void hideForwardButton() {
        forwardButton.setVisible(false);
    }

    /**
     * Speichert den aktuellen Datensatz. Diese Methode wird üblicherweise vom Speichern-Button aufgerufen.
     */
    @FXML
    public void save() {
        eingabeWindowController.save();
    }

    /**
     * Aktiviert den vorherigen Tab auf dem Hauptfenster.
     */
    @FXML
    public void goBack() {
//        eingabeWindowController.showPreviousTab();
    }

    /**
     * Aktiviert den nachfolgenden Tab auf dem Hauptfenster.
     */
    @FXML
    public void goForward() {
//        eingabeWindowController.showNextTab();
    }

    /**
     * Macht den letzten Eintrag im Änderungsprotokoll rückgängig.
     */
    @FXML
    public void undoLastChange() {
//        undoTracker.undoLastChange();
    }

    /**
     * Stellt den letzten Eintrag im Redo-Protokoll wieder her
     */
    @FXML
    public void redoLastUndo() {
//        undoTracker.redoLastUndo();
    }

}
