package schulscheduler.model.schule;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import schulscheduler.model.base.KuerzelElement;

/**
 * Ein Schulfach ist eine Art von Unterricht.
 */
public class Fach extends KuerzelElement {

    /**
     * "Harte" Fächer sind für die Schüler mental anspruchsvoll und sollten daher nicht aufeinander folgen.
     */
    private final BooleanProperty hart = new SimpleBooleanProperty();

    public boolean isHart() {
        return hart.get();
    }

    public BooleanProperty hartProperty() {
        return hart;
    }

    public void setHart(boolean hart) {
        this.hart.set(hart);
    }
}
