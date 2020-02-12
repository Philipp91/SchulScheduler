package schulscheduler.ui.windows;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import schulscheduler.model.eingabe.Eingabedaten;
import schulscheduler.ui.JavaFXHelper;
import schulscheduler.ui.eingabe.EingabeWindowComponent;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

/**
 * Manages open windows and opens new ones.
 */
@Singleton
public class WindowManager {

    @Inject
    EingabeWindowComponent.Factory eingabeWindowFactory;

    @Inject
    public WindowManager() {
    }

    public void openStartWindow() {
//        startWindow.show();
    }

    /**
     * @param model The data to be displayed in the new window.
     */
    public void openNewEingabeWindow(Eingabedaten model) {
        openNewWindow(eingabeWindowFactory.create(model));
    }

    /**
     * @param component A freshly created Dagger component for the new window.
     */
    private void openNewWindow(AbstractWindowComponent<?> component) {
        Platform.runLater(() -> {
            try {
                Scene scene = new Scene(component.createUI());
                scene.getStylesheets().add(JavaFXHelper.BASE_STYLESHEET);

                Stage stage = new Stage();
                stage.getIcons().add(JavaFXHelper.WINDOW_ICON);
                stage.setScene(scene);
//            stage.setTitle(windowInfo.getWindowTitle());
//            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
//                public void handle(WindowEvent event) {
//                    if (fireWindowCloseEvents(beanStore)) {
//                        openWindows.remove(beanStore);
//                    } else {
//                        event.consume();
//                    }
//                }
//            });
//            windowInfo.setStage(stage);
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
