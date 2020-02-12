package schulscheduler.main;

import dagger.Module;
import dagger.Provides;
import javafx.fxml.FXMLLoader;

@Module()
public class RootModule {

    @Provides
    FXMLLoader provideFXMLLoader() {
        return new FXMLLoader();
    }

}
