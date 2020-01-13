package schulscheduler.model.unterricht;

import griffon.javafx.beans.binding.MatchingBindings;
import griffon.javafx.collections.ElementObservableList;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.When;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import schulscheduler.javafx.MoreBindings;
import schulscheduler.model.NoUndoTracking;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

@XmlRootElement(name = "kopplung")
public class Kopplung extends Unterrichtseinheit {

    private final SimpleListProperty<Klasse> klassen = new SimpleListProperty<>(this, "klassen", FXCollections.observableArrayList()); // Reference
    private final SimpleListProperty<Zuweisung> zuweisungen = new SimpleListProperty<>(this, "zuweisungen", FXCollections.observableArrayList()); // Reference
    private final SimpleListProperty<KopplungsFach> faecher = new SimpleListProperty<>(this, "faecher", FXCollections.observableArrayList());
    private final BooleanBinding hart;

    public Kopplung() {
        toShortString.bind(new When(klassen.emptyProperty().or(faecher.emptyProperty()))
                .then(Bindings.format("Kopplung %s", idString))
                .otherwise(Bindings.format("Kopplung %s-%s",
                        MoreBindings.join("-", klassen, Klasse::nameProperty),
                        MoreBindings.join("-", faecher, KopplungsFach::toShortStringProperty)
                )));
        toLongString.bind(toShortString);

        hart = MatchingBindings.anyMatch(
                new ElementObservableList<>(faecher, fach -> new ObservableValue[]{Objects.requireNonNull(fach).hartBinding()}),
                KopplungsFach::isHart
        );
    }

    /**
     * Fügt die Klassen aller Zuweisungen in der Kopplung zur {@link #klassenProperty()} hinzu, wenn sie
     * dort noch nicht vorhanden sind.
     */
    public void addMissingKlassenToKopplung() {
        for (Zuweisung zuweisung : zuweisungen) {
            if (!klassen.contains(zuweisung.getKlasse())) {
                klassen.add(zuweisung.getKlasse());
            }
        }
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

    @XmlElement(name = "zuweisungen")
    @XmlIDREF
    public ObservableList<Zuweisung> getZuweisungen() {
        return zuweisungen.get();
    }

    @NoUndoTracking
    public SimpleListProperty<Zuweisung> zuweisungenProperty() {
        return zuweisungen;
    }

    public void setZuweisungen(ObservableList<Zuweisung> zuweisungen) {
        this.zuweisungen.set(zuweisungen);
    }

    @XmlElement(name = "faecher")
    public ObservableList<KopplungsFach> getFaecher() {
        return faecher.get();
    }

    public SimpleListProperty<KopplungsFach> faecherProperty() {
        return faecher;
    }

    public void setFaecher(ObservableList<KopplungsFach> faecher) {
        this.faecher.set(faecher);
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
        if (!super.equals(o)) return false;
        Kopplung kopplung = (Kopplung) o;
        return Objects.equals(getKlassen(), kopplung.getKlassen()) &&
                Objects.equals(getZuweisungen(), kopplung.getZuweisungen()) &&
                Objects.equals(getFaecher(), kopplung.getFaecher());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getKlassen(), getZuweisungen(), getFaecher());
    }
}
