package schulscheduler.main;

import dagger.BindsInstance;
import dagger.Component;
import javafx.application.Application;
import javafx.application.HostServices;
import schulscheduler.ui.eingabe.EingabeWindowComponent;
import schulscheduler.ui.windows.WindowManager;

import javax.inject.Singleton;

@Singleton
@Component(modules = {RootModule.class, EingabeWindowComponent.InstallationModule.class})
public interface RootComponent {
    @Component.Factory
    interface Factory {
        RootComponent create(@BindsInstance HostServices hostServices, @BindsInstance Application.Parameters parameters);
    }

    WindowManager windowManager();
}
