package schulscheduler.model.schule;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import schulscheduler.model.NoUndoTracking;
import schulscheduler.model.base.BaseElement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

@XmlRootElement(name = "lehrerVerfuegbarkeit")
public class LehrerVerfuegbarkeit extends BaseElement {

    private final SimpleObjectProperty<Zeitslot> zeitslot = new SimpleObjectProperty<>(this, "zeitslot"); // Constant Reference
    private final SimpleObjectProperty<EnumVerfuegbarkeit> verfuegbarkeit = new SimpleObjectProperty<>(this, "verfuegbarkeit");

    public LehrerVerfuegbarkeit() {
        toShortString.bind(Bindings.format("%s->%s", zeitslot, verfuegbarkeit));
        toLongString.bind(Bindings.format("%s -> %s", zeitslot, verfuegbarkeit));
    }

    public LehrerVerfuegbarkeit(@Nonnull Zeitslot zeitslot, @Nullable EnumVerfuegbarkeit verfuegbarkeit) {
        this();
        setZeitslot(zeitslot);
        setVerfuegbarkeit(verfuegbarkeit);
    }

    @XmlElement(name = "zeitslot")
    @XmlIDREF
    public Zeitslot getZeitslot() {
        return zeitslot.get();
    }

    @NoUndoTracking
    public ReadOnlyObjectProperty<Zeitslot> zeitslotProperty() {
        return zeitslot;
    }

    public void setZeitslot(Zeitslot zeitslot) {
        if (this.zeitslot.getValue() != null) throw new IllegalStateException("Cannot overwrite constant property");
        this.zeitslot.set(zeitslot);
    }

    @XmlElement(name = "verfuegbarkeit")
    public EnumVerfuegbarkeit getVerfuegbarkeit() {
        return verfuegbarkeit.get();
    }

    public SimpleObjectProperty<EnumVerfuegbarkeit> verfuegbarkeitProperty() {
        return verfuegbarkeit;
    }

    public void setVerfuegbarkeit(EnumVerfuegbarkeit verfuegbarkeit) {
        this.verfuegbarkeit.set(verfuegbarkeit);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LehrerVerfuegbarkeit that = (LehrerVerfuegbarkeit) o;
        return Objects.equals(getZeitslot(), that.getZeitslot()) &&
                Objects.equals(getVerfuegbarkeit(), that.getVerfuegbarkeit());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getZeitslot(), getVerfuegbarkeit());
    }
}
