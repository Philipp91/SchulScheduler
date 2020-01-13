package schulscheduler.model.unterricht;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import schulscheduler.model.NoUndoTracking;
import schulscheduler.model.base.BaseElement;
import schulscheduler.model.schule.Fach;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

@XmlRootElement(name = "profilEintrag")
public class ProfilEintrag extends BaseElement {

    private final SimpleObjectProperty<Fach> fach = new SimpleObjectProperty<>(this, "fach"); // Constant Reference
    private final SimpleIntegerProperty wochenstunden = new SimpleIntegerProperty(this, "wochenstunden");

    public ProfilEintrag() {
    }

    public ProfilEintrag(@Nonnull Fach fach, int wochenstunden) {
        this();
        this.fach.set(fach);
        this.wochenstunden.set(wochenstunden);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProfilEintrag that = (ProfilEintrag) o;
        return Objects.equals(getFach(), that.getFach()) &&
                getWochenstunden() == that.getWochenstunden();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFach(), getWochenstunden());
    }
}
