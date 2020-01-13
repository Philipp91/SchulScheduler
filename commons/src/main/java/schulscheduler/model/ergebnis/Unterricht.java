package schulscheduler.model.ergebnis;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.When;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import schulscheduler.javafx.MoreBindings;
import schulscheduler.model.NoUndoTracking;
import schulscheduler.model.base.BaseElement;
import schulscheduler.model.schule.Fach;
import schulscheduler.model.schule.Lehrer;
import schulscheduler.model.schule.Zeitslot;
import schulscheduler.model.unterricht.Klasse;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

@XmlRootElement(name = "unterricht")
public class Unterricht extends BaseElement {

    private final SimpleObjectProperty<Zeitslot> zeitslot = new SimpleObjectProperty<>(this, "zeitslot"); // Reference
    private final SimpleListProperty<Lehrer> lehrer = new SimpleListProperty<>(this, "lehrer", FXCollections.observableArrayList()); // Reference
    private final SimpleListProperty<Klasse> klassen = new SimpleListProperty<>(this, "klassen", FXCollections.observableArrayList()); // Reference
    private final SimpleListProperty<Fach> faecher = new SimpleListProperty<>(this, "faecher", FXCollections.observableArrayList()); // Reference

    public Unterricht() {
        toShortString.bind(new When(klassen.emptyProperty().or(faecher.emptyProperty()))
                .then(Bindings.format("Unterricht %s", idString))
                .otherwise(Bindings.format("Unterricht %s-%s-%s",
                        MoreBindings.join("-", klassen, Klasse::nameProperty),
                        MoreBindings.join("-", faecher, Fach::kuerzelProperty),
                        MoreBindings.join("-", lehrer, Lehrer::kuerzelProperty)
                )));
        toLongString.bind(toShortString);
    }

    @XmlElement(name = "zeitslot")
    @XmlIDREF
    public Zeitslot getZeitslot() {
        return zeitslot.get();
    }

    @NoUndoTracking
    public SimpleObjectProperty<Zeitslot> zeitslotProperty() {
        return zeitslot;
    }

    public void setZeitslot(Zeitslot zeitslot) {
        this.zeitslot.set(zeitslot);
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

    @XmlElement(name = "klassen")
    @XmlIDREF
    public ObservableList<Klasse> getKlassen() {
        return klassen.get();
    }

    @NoUndoTracking
    public SimpleListProperty<Klasse> klassenProperty() {
        return klassen;
    }

    public void setKlassen(ObservableList<Klasse> klassen) {
        this.klassen.set(klassen);
    }

    @XmlElement(name = "faecher")
    @XmlIDREF
    public ObservableList<Fach> getFaecher() {
        return faecher.get();
    }

    @NoUndoTracking
    public SimpleListProperty<Fach> faecherProperty() {
        return faecher;
    }

    public void setFaecher(ObservableList<Fach> faecher) {
        this.faecher.set(faecher);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Unterricht that = (Unterricht) o;
        return Objects.equals(getZeitslot(), that.getZeitslot()) &&
                Objects.equals(getLehrer(), that.getLehrer()) &&
                Objects.equals(getKlassen(), that.getKlassen()) &&
                Objects.equals(getFaecher(), that.getFaecher());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getZeitslot(), getLehrer(), getKlassen(), getFaecher());
    }
}
