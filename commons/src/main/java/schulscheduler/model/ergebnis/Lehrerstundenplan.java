package schulscheduler.model.ergebnis;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import schulscheduler.model.NoUndoTracking;
import schulscheduler.model.schule.Lehrer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

@XmlRootElement(name = "lehrerstundenplan")
public class Lehrerstundenplan extends Stundenplan {

    private final SimpleObjectProperty<Lehrer> lehrer = new SimpleObjectProperty<>(this, "lehrer"); // Reference

    public Lehrerstundenplan() {
        toShortString.bind(Bindings.concat(lehrer));
        toLongString.bind(Bindings.concat("Lehrer ", lehrer));
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Lehrerstundenplan that = (Lehrerstundenplan) o;
        return Objects.equals(getLehrer(), that.getLehrer());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getLehrer());
    }

}
