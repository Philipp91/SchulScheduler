package schulscheduler.model.unterricht;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import schulscheduler.model.NoUndoTracking;
import schulscheduler.model.base.BaseElement;
import schulscheduler.model.schule.Fach;
import schulscheduler.model.schule.Lehrer;
import schulscheduler.model.schule.Zeitslot;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;
import java.util.stream.Stream;

@XmlRootElement(name = "unterrichtseinheit")
public abstract class Unterrichtseinheit extends BaseElement {

    private final SimpleIntegerProperty wochenstunden = new SimpleIntegerProperty(this, "wochenstunden");
    private final SimpleListProperty<Zeitslot> fixeStunden = new SimpleListProperty<>(this, "fixeStunden", FXCollections.observableArrayList()); // Reference

    /**
     * @return Alle Klassen, die an dieser Unterrichtseinheit beteiligt sind, d.h. die anwesend sein müssen.
     */
    public abstract Stream<Klasse> getAllKlassen();

    /**
     * @return Alle Lehrer, die an dieser Unterrichtseinheit beteiligt sind, d.h. die anwesend sein müssen.
     */
    public abstract Stream<Lehrer> getAllLehrer();

    /**
     * @return Alle Fächer, die im Rahmen dieser Unterrichtseinheit unterrichtet werden.
     */
    public abstract Stream<Fach> getAllFaecher();

    /**
     * @param klasse Eine Klasse.
     * @return True, wenn die Klasse an diesem Unterricht teilnimmt.
     */
    public abstract boolean hasKlasse(@Nonnull Klasse klasse);

    /**
     * @param lehrer Ein Lehrer.
     * @return True, wenn der Lehrer an diesem Unterricht teilnimmt.
     */
    public abstract boolean hasLehrer(@Nonnull Lehrer lehrer);

    /**
     * @return True wenn mindestens eines der unterrichteten Fächer hart ist.
     */
    public abstract boolean isHart();

    /**
     * @param fach Ein Fach.
     * @return True, wenn das Fach in diesem Unterricht unterrichtet wird.
     */
    public abstract boolean hasFach(@Nonnull Fach fach);

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
