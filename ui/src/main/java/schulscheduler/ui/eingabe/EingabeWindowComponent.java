package schulscheduler.ui.eingabe;

import dagger.BindsInstance;
import dagger.Module;
import dagger.Subcomponent;
import schulscheduler.model.eingabe.Eingabedaten;
import schulscheduler.ui.windows.AbstractWindowComponent;
import schulscheduler.ui.windows.WindowScoped;

@WindowScoped
@Subcomponent(modules = {EingabeWindowModule.class})
public abstract class EingabeWindowComponent extends AbstractWindowComponent<Eingabedaten> {

    @Subcomponent.Factory
    public interface Factory {
        EingabeWindowComponent create(@BindsInstance Eingabedaten eingabedaten);
    }

    @Module(subcomponents = EingabeWindowComponent.class)
    public interface InstallationModule {
    }

}
