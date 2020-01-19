package schulscheduler.model.schule;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import schulscheduler.model.NoUndoTracking;
import schulscheduler.model.base.BaseElement;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Comparator;
import java.util.Objects;

@XmlRootElement(name = "zeitslot")
public class Zeitslot extends BaseElement implements Comparable<Zeitslot> {

    private static final Comparator<Zeitslot> COMPARATOR =
            Comparator.comparing(Zeitslot::getWochentag).thenComparing(Zeitslot::getStunde);

    private final SimpleObjectProperty<Stunde> stunde = new SimpleObjectProperty<>(this, "stunde"); // Constant Reference
    private final SimpleObjectProperty<EnumWochentag> wochentag = new SimpleObjectProperty<>(this, "wochentag"); // Constant

    public Zeitslot() {
        toShortString.bind(Bindings.concat(wochentag, stunde));
    }

    public Zeitslot(Stunde stunde, EnumWochentag wochentag) {
        this();
        this.stunde.set(stunde);
        this.wochentag.set(wochentag);
    }

    public boolean isGesperrt() {
        return this.getStunde() != null && getStunde().getGesperrtAm().contains(this.getWochentag());
    }

    @Override
    public int compareTo(@Nonnull Zeitslot other) {
        return COMPARATOR.compare(this, other);
    }

    @XmlElement(name = "stunde")
    @XmlIDREF
    public Stunde getStunde() {
        return stunde.get();
    }

    @NoUndoTracking
    public ReadOnlyObjectProperty<Stunde> stundeProperty() {
        return stunde;
    }

    public void setStunde(Stunde stunde) {
        if (this.stunde.getValue() != null) throw new IllegalStateException("Cannot overwrite constant property");
        this.stunde.set(stunde);
    }

    @XmlElement(name = "tag")
    public EnumWochentag getWochentag() {
        return wochentag.get();
    }

    public ReadOnlyObjectProperty<EnumWochentag> wochentagProperty() {
        return wochentag;
    }

    public void setWochentag(EnumWochentag wochentag) {
        if (this.wochentag.getValue() != null) throw new IllegalStateException("Cannot overwrite constant property");
        this.wochentag.set(wochentag);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Zeitslot zeitslot = (Zeitslot) o;
        return Objects.equals(getStunde(), zeitslot.getStunde()) &&
                Objects.equals(getWochentag(), zeitslot.getWochentag());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStunde(), getWochentag());
    }
}
