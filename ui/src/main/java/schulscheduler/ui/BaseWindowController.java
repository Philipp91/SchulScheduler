package schulscheduler.ui;

import javafx.animation.Transition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Window;
import javafx.util.Duration;
import schulscheduler.model.eingabe.Eingabedaten;
import schulscheduler.ui.windows.WindowManager;

import javax.inject.Inject;


/**
 * Basisklasse für Java-FX-Controller, die für ganze Fenster zuständig sind.
 *
 * @param <T> Der Typ des im Fenster angezeigten Datenmodells.
 */
public abstract class BaseWindowController<T> {

//    @FXML
//    protected ListView<ValidationMessage<?>> lstValidationMessages;

    @FXML
    protected Label lblAutoSaveStatus;

//    @FXML
//    protected MenuItem mnuUndo, mnuRedo;

    //    @Inject
//    private WorkspaceManager workspaceManager;
//
    @Inject
    WindowManager windowManager;
//
//    @Inject
//    private WindowInfo<T> windowInfo;
//
//    @Inject
//    private UndoTracker<T> undoTracker;
//
//    @Inject
//    private ValidationManager<T> validationManager;
//
//    @Inject
//    private Instance<AboutDialog> dlgAbout;

    /**
     * @return Das Fenster, für das der Controller zuständig ist.
     */
    protected Window getWindow() {
//        return lstValidationMessages.getScene().getWindow();
        return null;
    }

    /**
     * Bindet die Komponenten an. Bitte beim Überschreiben dieser Methode darauf achten, dass die super-Methode
     * weiterhin aufgerufen wird.
     */
    public void initialize() {
//        lstValidationMessages.itemsProperty().bind(validationManager.validationMessagesProperty());
//        lstValidationMessages.setCellFactory(new Callback<ListView<ValidationMessage<?>>, ListCell<ValidationMessage<?>>>() {
//            public ListCell<ValidationMessage<?>> call(ListView<ValidationMessage<?>> param) {
//                return new ValidationMessageCell();
//            }
//        });
//        if (windowInfo instanceof AutoSavedWindowInfo<?>) {
//            lblAutoSaveStatus.textProperty().bind(((AutoSavedWindowInfo<T>) windowInfo).autoSaveStatusProperty());
//        }
//
//        if (mnuUndo != null) {
//            mnuUndo.disableProperty().bind(undoTracker.canUndoProperty().not());
//        }
//        if (mnuRedo != null) {
//            mnuRedo.disableProperty().bind(undoTracker.canRedoProperty().not());
//        }
    }

    /**
     * Hebt das AutoSave-Label unten rechts im Fenster durch eine Animation von links nach rechts und von rot nach grau
     * hervor, um den Nutzer auf Änderungen dort hinzuweisen.
     */
    public void highlightAutoSaveLabel() {
        Color startColor = Color.valueOf("#ff0000");
        Color endColor = Color.valueOf("#808080");
        Transition transition = new Transition() {
            {
                setCycleDuration(Duration.seconds(1));
            }

            protected void interpolate(double frac) {
                lblAutoSaveStatus.setTextFill(startColor.interpolate(endColor, frac));
                lblAutoSaveStatus.setTranslateX(Math.min(0, (frac - 0.25) * 800));
            }
        };
        transition.playFromStart();
    }

    /**
     * Öffnet ein neues, leeres Fenster für die Dateneingabe. Wird üblicherweise vom Menüpunkt Datei/Neu ausgelöst.
     */
    @FXML
    public void openNewEingabeWindow() {
        windowManager.openNewEingabeWindow(new Eingabedaten());//ModelFunctions.createDefaultEingabedaten());
    }

    /**
     * Öffnet ein neues Fenster mit dem Datensatz aus der Datei. Wird üblicherweise vom Menüpunkt Datei/Öffnen
     * ausgelöst.
     */
    @FXML
    public void open() {
//        workspaceManager.openFile(getWindow(), new Runnable() {
//            public void run() {
//                closeIfEmpty();
//            }
//        });
    }

    /**
     * Speichert den Datensatz. Wird üblicherweise vom Menüpunkt Datei/Speichern oder dem Speichern-Button ausgelöst.
     */
    @FXML
    public void save() {
//        try {
//            windowInfo.save();
//            highlightAutoSaveLabel();
//
//        } catch (IOException e) {
//            JavaFXHelper.showMessage(getWindow(), "error_title", "error_save_file");
//            e.printStackTrace();
//        }
    }

    /**
     * Öffnet einen Datei-Dialog zum speichern des Datensatzes. Wird üblicherweise vom Menüounkt Datei/Speichern Unter
     * ausgelöst.
     */
    @FXML
    public void saveAs() {
//        try {
//            windowInfo.saveAs();
//            highlightAutoSaveLabel();
//        } catch (IOException e) {
//            JavaFXHelper.showMessage(getWindow(), "error_title", "error_save_file");
//            e.printStackTrace();
//        }
    }

    /**
     * Schließt das Fenster.
     */
    @FXML
    public void closeWindow() {
//        windowManager.closeCurrentWindow();
    }

    /**
     * Schließt das Fenster, wenn es leer ist und der Nutzer noch nichts eingegeben hat.
     */
    protected void closeIfEmpty() {
//        if (windowInfo.getFile() == null && undoTracker.isEmpty()) {
//            closeWindow();
//        }
    }

    /**
     * Schließt alle offenen Fenster und beendet dadurch die Anwendung. Wird üblicherweise vom Menüpunkt Datei/Beenden
     * ausgelöst.
     */
    @FXML
    public void handleBeendenAction() {
//        windowManager.closeAllWindows();
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

    /**
     * Öffnet das Einstellungen-Fenster.
     */
    @FXML
    public void openSettings() {
//        windowManager.openSettingsWindow();
    }

    /**
     * Zeigt das Fenster für die Berechnungsübersicht an.
     */
    @FXML
    public void openBerechnungsUebersicht() {
//        windowManager.openBerechnungsUebersicht();
    }

    /**
     * Öffnet einen Dialog um den Workspace zu ändern.
     */
    @FXML
    public void changeWorkspace() {
//        workspaceManager.changeWorkspace(getWindow());
    }

    /**
     * Öffnet einen Dialog zum Wiederherstellen von Backup-Dateien.
     */
    @FXML
    public void openAutoSavedFile() {
//        workspaceManager.openAutoSavedFile();
    }

    /**
     * Öffnet das About-Fenster.
     */
    @FXML
    public void openAboutDialog() {
//        dlgAbout.get().showAndWait();
    }

    /**
     * @return Die Liste, in der die Validierungsmeldungen angezeigt werden.
     */
//    protected ListView<ValidationMessage<?>> getLstValidationMessages() {
//        return lstValidationMessages;
//    }

}
