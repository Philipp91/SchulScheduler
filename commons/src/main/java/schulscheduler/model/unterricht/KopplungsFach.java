package schulscheduler.model.unterricht;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import schulscheduler.model.NoUndoTracking;
import schulscheduler.model.base.BaseElement;
import schulscheduler.model.schule.Fach;
import schulscheduler.model.schule.Lehrer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

@XmlRootElement(name = "kopplungsFach")
class KopplungsFach extends BaseElement {

    private final SimpleObjectProperty<Fach> fach = new SimpleObjectProperty<>(this, "fach"); // Constant Reference
    private final SimpleListProperty<Lehrer> lehrer = new SimpleListProperty<>(this, "lehrer", FXCollections.observableArrayList()); // Reference

    private final BooleanBinding hart;

    public KopplungsFach() {
        toShortString.bind(Bindings.concat(fach));
        toLongString.bind(toShortString);
        hart = Bindings.selectBoolean(fach, "hart");
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
        return hart.get();
    }

    public BooleanBinding hartBinding() {
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
