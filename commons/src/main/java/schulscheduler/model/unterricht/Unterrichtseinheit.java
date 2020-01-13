package schulscheduler.model.unterricht;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import schulscheduler.model.NoUndoTracking;
import schulscheduler.model.base.BaseElement;
import schulscheduler.model.schule.Zeitslot;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

@XmlRootElement(name = "unterrichtseinheit")
public abstract class Unterrichtseinheit extends BaseElement {

    private final SimpleIntegerProperty wochenstunden = new SimpleIntegerProperty(this, "wochenstunden");
    private final SimpleListProperty<Zeitslot> fixeStunden = new SimpleListProperty<>(this, "fixeStunden", FXCollections.observableArrayList()); // Reference

    @XmlElement(name = "wochenstunden")
    public int getWochenstunden() {
        return wochenstunden.get();
    }

    public SimpleIntegerProperty wochenstundenProperty() {
        return wochenstunden;
    }

    public void setWochenstunden(int wochenstunden) {
        this.wochenstunden.set(wochenstunden);
    }

    @XmlElement(name = "fixeStunden")
    @XmlIDREF
    public ObservableList<Zeitslot> getFixeStunden() {
        return fixeStunden.get();
    }

    @NoUndoTracking
    public SimpleListProperty<Zeitslot> fixeStundenProperty() {
        return fixeStunden;
    }

    public void setFixeStunden(ObservableList<Zeitslot> fixeStunden) {
        this.fixeStunden.set(fixeStunden);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Unterrichtseinheit that = (Unterrichtseinheit) o;
        return getWochenstunden() == that.getWochenstunden() &&
                Objects.equals(getFixeStunden(), that.getFixeStunden());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getWochenstunden(), getFixeStunden());
    }
}
