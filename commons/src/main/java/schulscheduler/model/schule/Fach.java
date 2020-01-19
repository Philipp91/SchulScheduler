package schulscheduler.model.schule;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import schulscheduler.model.base.KuerzelElement;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

/**
 * Ein Schulfach ist eine Art von Unterricht.
 */
@XmlRootElement(name = "fach")
public class Fach extends KuerzelElement {

    /**
     * "Harte" Fächer sind für die Schüler mental anspruchsvoll und sollten daher nicht aufeinander folgen.
     */
    private final BooleanProperty hart = new SimpleBooleanProperty(this, "hart");

    public Fach() {
        super();
    }

    public Fach(String name, String kuerzel, boolean hart) {
        super(name, kuerzel);
        setHart(hart);
    }

    @XmlElement(name = "hart")
    public boolean isHart() {
        return hart.get();
    }

    public BooleanProperty hartProperty() {
        return hart;
    }

    public void setHart(boolean hart) {
        this.hart.set(hart);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Fach fach = (Fach) o;
        return isHart() == fach.isHart();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), isHart());
    }
}
