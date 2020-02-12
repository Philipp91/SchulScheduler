package schulscheduler.ui.eingabe;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;
import javafx.scene.Parent;
import schulscheduler.ui.JavaFXHelper;

import javax.inject.Provider;
import java.io.IOException;
import java.util.Map;

@Module
public abstract class EingabeWindowModule {

    @Binds
    @IntoMap
    @ClassKey(EingabeWindowController.class)
    public abstract Object eingabeWindowController(EingabeWindowController controller);

    @Binds
    @IntoMap
    @ClassKey(FaecherController.class)
    public abstract Object faecherController(FaecherController controller);

    @Binds
    @IntoMap
    @ClassKey(InnerWizardController.class)
    public abstract Object innerWizardController(InnerWizardController controller);

    @Provides
    public static Parent createUI(Map<Class<?>, Provider<Object>> controllerProvider) { // TODO Should this move to its own module to be shared with ErgebnisWindow?
        try {
            return JavaFXHelper.loadFXML("eingabe/window_eingabe.fxml", (type) -> controllerProvider.get(type).get());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
