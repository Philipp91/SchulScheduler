package schulscheduler.model.unterricht;

import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.When;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.fxmisc.easybind.EasyBind;
import schulscheduler.javafx.MoreBindings;
import schulscheduler.model.NoUndoTracking;
import schulscheduler.model.schule.Fach;
import schulscheduler.model.schule.Lehrer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;
import java.util.stream.Stream;

@XmlRootElement(name = "kopplung")
public class Kopplung extends Unterrichtseinheit {

    private final SimpleListProperty<Klasse> klassen = new SimpleListProperty<>(this, "klassen", FXCollections.observableArrayList()); // Reference
    private final SimpleListProperty<Zuweisung> zuweisungen = new SimpleListProperty<>(this, "zuweisungen", FXCollections.observableArrayList()); // Reference
    private final SimpleListProperty<KopplungsFach> faecher = new SimpleListProperty<>(this, "faecher", FXCollections.observableArrayList());
    private final Binding<Boolean> hart;

    public Kopplung() {
        toShortString.bind(new When(klassen.emptyProperty().or(faecher.emptyProperty()))
                .then(Bindings.format("Kopplung %s", idString))
                .otherwise(Bindings.format("Kopplung %s-%s",
                        MoreBindings.join("-", klassen, Klasse::nameProperty),
                        MoreBindings.join("-", faecher, KopplungsFach::toShortStringProperty)
                )));
        toLongString.bind(toShortString);

        hart = EasyBind.combine(EasyBind.map(faecher, KopplungsFach::hartBinding), stream -> stream.anyMatch(hart -> hart));
    }

    @Override
    public Stream<Klasse> getAllKlassen() {
        return klassen.stream();
    }

    @Override
    public Stream<Lehrer> getAllLehrer() {
        return faecher.stream().flatMap(fach -> fach.getLehrer().stream());
    }

    @Override
    public Stream<Fach> getAllFaecher() {
        return faecher.stream().map(KopplungsFach::getFach);
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
        return hart.getValue();
    }

    public Binding<Boolean> hartBinding() {
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
