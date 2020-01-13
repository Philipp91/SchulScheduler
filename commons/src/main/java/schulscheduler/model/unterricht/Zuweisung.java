package schulscheduler.model.unterricht;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import schulscheduler.model.NoUndoTracking;
import schulscheduler.model.schule.Fach;
import schulscheduler.model.schule.Lehrer;
import schulscheduler.model.schule.Zeitslot;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@XmlRootElement(name = "zuweisung")
public class Zuweisung extends Unterrichtseinheit {

    private final SimpleObjectProperty<Lehrer> lehrer = new SimpleObjectProperty<>(this, "lehrer"); // Reference
    private final SimpleObjectProperty<Fach> fach = new SimpleObjectProperty<>(this, "fach"); // Constant Reference
    private final SimpleObjectProperty<Klasse> klasse = new SimpleObjectProperty<>(this, "klasse"); // Constant Reference

    public Zuweisung() {
        toShortString.bind(Bindings.format("{0}-{1}", klasse, fach));
        toLongString.bind(Bindings.concat("Zuweisung ", toShortString));
    }

    public Zuweisung(int wochenstunden, @Nullable List<Zeitslot> fixeStunden, @Nullable Lehrer lehrer, @Nullable Fach fach, @Nullable Klasse klasse) {
        this();
        setWochenstunden(wochenstunden);
        if (fixeStunden != null) setFixeStunden(FXCollections.observableList(fixeStunden));
        setLehrer(lehrer);
        this.fach.set(fach);
        this.klasse.set(klasse);
    }

    /**
     * Überprüft, ob diese Zuweisung gekoppelt ist.
     *
     * @param kopplungen Alle Kopplungen im Datenmodell.
     * @return True gdw. die Zuweisung in mindestens einer Kopplung vorkommt.
     */
    public boolean isGekoppelt(@Nonnull Collection<Kopplung> kopplungen) {
        return kopplungen.stream().anyMatch(k -> k.getZuweisungen().contains(this));
    }

    @XmlElement(name = "lehrer")
    @XmlIDREF
    public Lehrer getLehrer() {
        return lehrer.get();
    }

    @NoUndoTracking
    public SimpleObjectProperty<Lehrer> lehrerProperty() {
        return lehrer;
    }

    public void setLehrer(Lehrer lehrer) {
        this.lehrer.set(lehrer);
    }

    @XmlElement(name = "fach")
    @XmlIDREF
    public Fach getFach() {
        return fach.get();
    }

    public ReadOnlyObjectProperty<Fach> fachProperty() {
        return fach;
    }

    @XmlElement(name = "klasse")
    @XmlIDREF
    public Klasse getKlasse() {
        return klasse.get();
    }

    public ReadOnlyObjectProperty<Klasse> klasseProperty() {
        return klasse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Zuweisung zuweisung = (Zuweisung) o;
        return Objects.equals(getLehrer(), zuweisung.getLehrer()) &&
                Objects.equals(getFach(), zuweisung.getFach()) &&
                Objects.equals(getKlasse(), zuweisung.getKlasse());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getLehrer(), getFach(), getKlasse());
    }
}