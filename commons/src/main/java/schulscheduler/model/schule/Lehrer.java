package schulscheduler.model.schule;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import schulscheduler.model.NoUndoTracking;
import schulscheduler.model.base.KuerzelElement;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

@XmlRootElement(name = "lehrer")
public class Lehrer extends KuerzelElement {

    private final SimpleIntegerProperty deputat = new SimpleIntegerProperty(this, "deputat", 25);
    private final SimpleListProperty<Fach> lehrt = new SimpleListProperty<>(this, "lehrt", FXCollections.observableArrayList()); // Reference
    private final SimpleListProperty<LehrerVerfuegbarkeit> verfuegbarkeit = new SimpleListProperty<>(this, "verfuegbarkeit", FXCollections.observableArrayList());
    private final SimpleBooleanProperty freierTag = new SimpleBooleanProperty(this, "freierTag");

    public Lehrer() {
    }

    public Lehrer(String name, String kuerzel) {
        super(name, kuerzel);
    }

    /**
     * Ermittelt die Verfügbarkeit des Lehrers zu einem bestimmten Zeitpunkt.
     *
     * @param zeitslot Der Zeitpunkt, zu dem die Verfügbarkeit ermittelt werden soll.
     * @return Die Verfügbarkeit des Lehrers zum gegebenen Zeitpunkt, oder null, wenn kein Eintrag gefunden wurde.
     */
    public EnumVerfuegbarkeit getVerfuegbar(@Nonnull Zeitslot zeitslot) {
        for (LehrerVerfuegbarkeit eintrag : this.verfuegbarkeit) {
            if (zeitslot.equals(eintrag.getZeitslot())) {
                return eintrag.getVerfuegbarkeit();
            }
        }
        return null;
    }

    @XmlElement(name = "deputat")
    public int getDeputat() {
        return deputat.get();
    }

    public SimpleIntegerProperty deputatProperty() {
        return deputat;
    }

    public void setDeputat(int deputat) {
        this.deputat.set(deputat);
    }

    @XmlElement(name = "lehrt")
    @XmlIDREF
    public ObservableList<Fach> getLehrt() {
        return lehrt.get();
    }

    @NoUndoTracking
    public SimpleListProperty<Fach> lehrtProperty() {
        return lehrt;
    }

    public void setLehrt(ObservableList<Fach> lehrt) {
        this.lehrt.set(lehrt);
    }

    @XmlElement(name = "verfuegbarkeit")
    public ObservableList<LehrerVerfuegbarkeit> getVerfuegbarkeit() {
        return verfuegbarkeit.get();
    }

    public SimpleListProperty<LehrerVerfuegbarkeit> verfuegbarkeitProperty() {
        return verfuegbarkeit;
    }

    public void setVerfuegbarkeit(ObservableList<LehrerVerfuegbarkeit> verfuegbarkeit) {
        this.verfuegbarkeit.set(verfuegbarkeit);
    }

    @XmlElement(name = "freierTag")
    public boolean isFreierTag() {
        return freierTag.get();
    }

    public SimpleBooleanProperty freierTagProperty() {
        return freierTag;
    }

    public void setFreierTag(boolean freierTag) {
        this.freierTag.set(freierTag);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lehrer lehrer = (Lehrer) o;
        return getDeputat() == lehrer.getDeputat() &&
                Objects.equals(getLehrt(), lehrer.getLehrt()) &&
                Objects.equals(getVerfuegbarkeit(), lehrer.getVerfuegbarkeit()) &&
                isFreierTag() == lehrer.isFreierTag();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDeputat(), getLehrt(), getVerfuegbarkeit(), isFreierTag());
    }
}
