package schulscheduler.model.unterricht;

import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.fxmisc.easybind.EasyBind;
import schulscheduler.model.NoUndoTracking;
import schulscheduler.model.base.BaseElement;
import schulscheduler.model.schule.Fach;
import schulscheduler.model.schule.Lehrer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

@XmlRootElement(name = "kopplungsFach")
public class KopplungsFach extends BaseElement {

    private final SimpleObjectProperty<Fach> fach = new SimpleObjectProperty<>(this, "fach"); // Constant Reference
    private final SimpleListProperty<Lehrer> lehrer = new SimpleListProperty<>(this, "lehrer", FXCollections.observableArrayList()); // Reference

    private final Binding<Boolean> hart;

    public KopplungsFach() {
        toShortString.bind(Bindings.concat(fach));
        toLongString.bind(toShortString);
        hart = EasyBind.select(fach).selectObject(Fach::hartProperty).orElse(false);
    }

    public KopplungsFach(Fach fach) {
        this();
        this.fach.set(fach);
    }

    @XmlElement(name = "fach")
    @XmlIDREF
    public Fach getFach() {
        return fach.get();
    }

    @NoUndoTracking
    public ReadOnlyObjectProperty<Fach> fachProperty() {
        return fach;
    }

    public void setFach(Fach fach) {
        if (this.fach.getValue() != null) throw new IllegalStateException("Cannot overwrite constant property");
        this.fach.set(fach);
    }

    @XmlElement(name = "lehrer")
    @XmlIDREF
    public ObservableList<Lehrer> getLehrer() {
        return lehrer.get();
    }

    @NoUndoTracking
    public SimpleListProperty<Lehrer> lehrerProperty() {
        return lehrer;
    }

    public void setLehrer(ObservableList<Lehrer> lehrer) {
        this.lehrer.set(lehrer);
    }

    public boolean isHart() {
        return hart.getValue();
    }

    public Binding<Boolean> hartBinding() {
        return hart;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KopplungsFach that = (KopplungsFach) o;
        return Objects.equals(getFach(), that.getFach()) &&
                Objects.equals(getLehrer(), that.getLehrer());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFach(), getLehrer());
    }
}
