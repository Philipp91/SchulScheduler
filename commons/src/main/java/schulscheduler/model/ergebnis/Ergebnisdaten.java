package schulscheduler.model.ergebnis;

import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import schulscheduler.model.eingabe.Eingabedaten;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.util.Objects;

@XmlRootElement(name = "ergebnisdaten")
public class Ergebnisdaten {

    @XmlElement(name = "modelVersion")
    public final int modelVersion = 1;

    private final SimpleObjectProperty<Eingabedaten> eingabedaten = new SimpleObjectProperty<>(this, "eingabedaten");

    private final SimpleListProperty<Unterricht> unterricht = new SimpleListProperty<>(this, "unterricht", FXCollections.observableArrayList());
    private final SimpleListProperty<Lehrerstundenplan> lehrerStundenplaene = new SimpleListProperty<>(this, "lehrerStundenplaene", FXCollections.observableArrayList());
    private final SimpleListProperty<Klassenstundenplan> klassenStundenplaene = new SimpleListProperty<>(this, "klassenStundenplaene", FXCollections.observableArrayList());

    /**
     * @return A deep-copy of this using Java Object Serialization (JOS).
     */
    public Ergebnisdaten copy() {
//        try {
        return null; // TODO Serialization.javaClone(this);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }

    @XmlElement(name = "eingabedaten")
    public Eingabedaten getEingabedaten() {
        return eingabedaten.get();
    }

    public SimpleObjectProperty<Eingabedaten> eingabedatenProperty() {
        return eingabedaten;
    }

    public void setEingabedaten(Eingabedaten eingabedaten) {
        this.eingabedaten.set(eingabedaten);
    }

    @XmlElement(name = "unterricht")
    public ObservableList<Unterricht> getUnterricht() {
        return unterricht.get();
    }

    public SimpleListProperty<Unterricht> unterrichtProperty() {
        return unterricht;
    }

    public void setUnterricht(ObservableList<Unterricht> unterricht) {
        this.unterricht.set(unterricht);
    }

    @XmlElement(name = "lehrerStundenplaene")
    public ObservableList<Lehrerstundenplan> getLehrerStundenplaene() {
        return lehrerStundenplaene.get();
    }

    public SimpleListProperty<Lehrerstundenplan> lehrerStundenplaeneProperty() {
        return lehrerStundenplaene;
    }

    public void setLehrerStundenplaene(ObservableList<Lehrerstundenplan> lehrerStundenplaene) {
        this.lehrerStundenplaene.set(lehrerStundenplaene);
    }

    @XmlElement(name = "klassenStundenplaene")
    public ObservableList<Klassenstundenplan> getKlassenStundenplaene() {
        return klassenStundenplaene.get();
    }

    public SimpleListProperty<Klassenstundenplan> klassenStundenplaeneProperty() {
        return klassenStundenplaene;
    }

    public void setKlassenStundenplaene(ObservableList<Klassenstundenplan> klassenStundenplaene) {
        this.klassenStundenplaene.set(klassenStundenplaene);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ergebnisdaten that = (Ergebnisdaten) o;
        return Objects.equals(getEingabedaten(), that.getEingabedaten()) &&
                Objects.equals(getUnterricht(), that.getUnterricht()) &&
                Objects.equals(getLehrerStundenplaene(), that.getLehrerStundenplaene()) &&
                Objects.equals(getKlassenStundenplaene(), that.getKlassenStundenplaene());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEingabedaten(), getUnterricht(), getLehrerStundenplaene(), getKlassenStundenplaene());
    }
}
