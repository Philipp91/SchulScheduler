package schulscheduler.main;

import javafx.application.Application;
import javafx.stage.Stage;
import schulscheduler.model.eingabe.Eingabedaten;

public class Bootstrap extends Application {

    private RootComponent rootComponent;

    @Override
    public void init() {
        this.rootComponent = DaggerRootComponent.factory()
                .create(getHostServices(), getParameters());
    }

    /**
     * Zeigt zunächst einen Splash-Screen an und startet dann den Weld-Container. Dadurch wird
     * {@link Launcher#launch(Stage)} ausgeführt.
     *
     * @param primaryStage Hauptfenster von JavaFX, wird für den Splash-Screen verwendet.
     */
    @Override
    public void start(Stage primaryStage) {
        GlobalExceptionHandler.initialize();

        rootComponent.windowManager().openNewEingabeWindow(new Eingabedaten());
    }

    public static void main(String[] args) {
        launch();
    }

}