package schulscheduler.model.ergebnis;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import schulscheduler.model.NoUndoTracking;
import schulscheduler.model.base.BaseElement;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

@XmlRootElement(name = "stundenplan")
public class Stundenplan extends BaseElement {

    private final SimpleListProperty<Unterricht> unterricht = new SimpleListProperty<>(this, "unterricht", FXCollections.observableArrayList()); // Reference

    @XmlElement(name = "unterricht")
    @XmlIDREF
    public ObservableList<Unterricht> getUnterricht() {
        return unterricht.get();
    }

    @NoUndoTracking
    public SimpleListProperty<Unterricht> unterrichtProperty() {
        return unterricht;
    }

    public void setUnterricht(ObservableList<Unterricht> unterricht) {
        this.unterricht.set(unterricht);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stundenplan that = (Stundenplan) o;
        return Objects.equals(getUnterricht(), that.getUnterricht());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUnterricht());
    }
}
