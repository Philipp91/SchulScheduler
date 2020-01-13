package schulscheduler.model.ergebnis;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import schulscheduler.model.NoUndoTracking;
import schulscheduler.model.unterricht.Klasse;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

@XmlRootElement(name = "klassenstundenplan")
public class Klassenstundenplan extends Stundenplan {

    private final SimpleObjectProperty<Klasse> klasse = new SimpleObjectProperty<>(this, "klasse"); // Reference

    public Klassenstundenplan() {
        toShortString.bind(Bindings.concat(klasse));
        toLongString.bind(Bindings.concat("Klasse ", klasse));
    }

    @XmlElement(name = "klasse")
    @XmlIDREF
    public Klasse getKlasse() {
        return klasse.get();
    }

    @NoUndoTracking
    public SimpleObjectProperty<Klasse> klasseProperty() {
        return klasse;
    }

    public void setKlasse(Klasse klasse) {
        this.klasse.set(klasse);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Klassenstundenplan that = (Klassenstundenplan) o;
        return Objects.equals(getKlasse(), that.getKlasse());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getKlasse());
    }
}
