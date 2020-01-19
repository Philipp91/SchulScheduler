package schulscheduler.model.schule;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import schulscheduler.model.base.BaseElement;
import schulscheduler.model.eingabe.EnumGewichtung;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

@XmlRootElement(name = "stunde")
public class Stunde extends BaseElement implements Comparable<Stunde> {

    private final SimpleIntegerProperty nummer = new SimpleIntegerProperty(this, "nummer"); // Constant
    private final SimpleStringProperty beginn = new SimpleStringProperty(this, "beginn");
    private final SimpleStringProperty ende = new SimpleStringProperty(this, "ende");
    private final SimpleObjectProperty<EnumGewichtung> unterrichtsprioritaet = new SimpleObjectProperty<>(this, "unterrichtsprioritaet");
    private final SimpleBooleanProperty doppelstunde = new SimpleBooleanProperty(this, "doppelstunde");
    private final SimpleListProperty<EnumWochentag> gesperrtAm = new SimpleListProperty<>(this, "gesperrtAm", FXCollections.observableArrayList());

    public Stunde() {
        toShortString.bind(nummer.asString());
        toLongString.bind(Bindings.format("Stunde %s", nummer));
    }

    public Stunde(int nummer) {
        this();
        this.nummer.set(nummer);
    }

    @Override
    public int compareTo(@Nonnull Stunde o) {
        return Integer.compare(this.getNummer(), o.getNummer());
    }

    public boolean isDoppelstundeWith(Stunde other) {
        if (this.getNummer() == other.getNummer() + 1) {
            return other.isDoppelstunde();
        } else if (this.getNummer() == other.getNummer() - 1) {
            return this.isDoppelstunde();
        } else {
            return false;
        }
    }

    public boolean isKernstunde() {
        return this.getUnterrichtsprioritaet() == EnumGewichtung.MAXIMAL;
    }

    @XmlElement(name = "nummer")
    public int getNummer() {
        return nummer.get();
    }

    public ReadOnlyIntegerProperty nummerProperty() {
        return nummer;
    }

    @XmlElement(name = "beginn")
    public String getBeginn() {
        return beginn.get();
    }

    public SimpleStringProperty beginnProperty() {
        return beginn;
    }

    public void setBeginn(String beginn) {
        this.beginn.set(beginn);
    }

    @XmlElement(name = "ende")
    public String getEnde() {
        return ende.get();
    }

    public SimpleStringProperty endeProperty() {
        return ende;
    }

    public void setEnde(String ende) {
        this.ende.set(ende);
    }

    @XmlElement(name = "unterrichtsprioritaet")
    public EnumGewichtung getUnterrichtsprioritaet() {
        return unterrichtsprioritaet.get();
    }

    public SimpleObjectProperty<EnumGewichtung> unterrichtsprioritaetProperty() {
        return unterrichtsprioritaet;
    }

    public void setUnterrichtsprioritaet(EnumGewichtung unterrichtsprioritaet) {
        this.unterrichtsprioritaet.set(unterrichtsprioritaet);
    }

    @XmlElement(name = "doppelstunde")
    public boolean isDoppelstunde() {
        return doppelstunde.get();
    }

    public SimpleBooleanProperty doppelstundeProperty() {
        return doppelstunde;
    }

    public void setDoppelstunde(boolean doppelstunde) {
        this.doppelstunde.set(doppelstunde);
    }

    @XmlElement(name = "gesperrtAm")
    public ObservableList<EnumWochentag> getGesperrtAm() {
        return gesperrtAm.get();
    }

    public SimpleListProperty<EnumWochentag> gesperrtAmProperty() {
        return gesperrtAm;
    }

    public void setGesperrtAm(ObservableList<EnumWochentag> gesperrtAm) {
        this.gesperrtAm.set(gesperrtAm);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stunde stunde = (Stunde) o;
        return getNummer() == stunde.getNummer() &&
                Objects.equals(getBeginn(), stunde.getBeginn()) &&
                Objects.equals(getEnde(), stunde.getEnde()) &&
                Objects.equals(getUnterrichtsprioritaet(), stunde.getUnterrichtsprioritaet()) &&
                isDoppelstunde() == stunde.isDoppelstunde() &&
                Objects.equals(getGesperrtAm(), stunde.getGesperrtAm());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNummer(), getBeginn(), getEnde(), getUnterrichtsprioritaet(), isDoppelstunde(), getGesperrtAm());
    }
}
