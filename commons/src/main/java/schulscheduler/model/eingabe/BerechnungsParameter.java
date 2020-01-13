package schulscheduler.model.eingabe;

import javafx.beans.property.SimpleObjectProperty;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

@XmlRootElement(name = "berechnungsParameter")
class BerechnungsParameter {

    // Hier sind die Werte NULL, NIEDRIG, MITTEL, HOCH erlaubt, also
    // EnumGewichtung#berechnungPrioritaetValues()

    private final SimpleObjectProperty<EnumGewichtung> rechendauer = new SimpleObjectProperty<>(this, "rechendauer");
    private final SimpleObjectProperty<EnumGewichtung> harteFaecherFolgen = new SimpleObjectProperty<>(this, "harteFaecherFolgen");
    private final SimpleObjectProperty<EnumGewichtung> lehrerHohlstunden = new SimpleObjectProperty<>(this, "lehrerHohlstunden");
    private final SimpleObjectProperty<EnumGewichtung> unerwuenschteLehrerZeitslots = new SimpleObjectProperty<>(this, "unerwuenschteLehrerZeitslots");
    private final SimpleObjectProperty<EnumGewichtung> weicheNachmittagsFaecher = new SimpleObjectProperty<>(this, "weicheNachmittagsFaecher");

    /**
     * @return BerechnungsParameter mit den Standardeinstellungen.
     */
    @Nonnull
    public static BerechnungsParameter createDefault() {
        BerechnungsParameter parameter = new BerechnungsParameter();
        parameter.setHarteFaecherFolgen(EnumGewichtung.MITTEL);
        parameter.setLehrerHohlstunden(EnumGewichtung.MITTEL);
        parameter.setRechendauer(EnumGewichtung.MITTEL);
        parameter.setUnerwuenschteLehrerZeitslots(EnumGewichtung.MITTEL);
        parameter.setWeicheNachmittagsFaecher(EnumGewichtung.MITTEL);
        return parameter;
    }

    @XmlElement(name = "rechendauer")
    public EnumGewichtung getRechendauer() {
        return rechendauer.get();
    }

    public SimpleObjectProperty<EnumGewichtung> rechendauerProperty() {
        return rechendauer;
    }

    public void setRechendauer(EnumGewichtung rechendauer) {
        this.rechendauer.set(rechendauer);
    }

    @XmlElement(name = "harteFaecherFolgen")
    public EnumGewichtung getHarteFaecherFolgen() {
        return harteFaecherFolgen.get();
    }

    public SimpleObjectProperty<EnumGewichtung> harteFaecherFolgenProperty() {
        return harteFaecherFolgen;
    }

    public void setHarteFaecherFolgen(EnumGewichtung harteFaecherFolgen) {
        this.harteFaecherFolgen.set(harteFaecherFolgen);
    }

    @XmlElement(name = "lehrerHohlstunden")
    public EnumGewichtung getLehrerHohlstunden() {
        return lehrerHohlstunden.get();
    }

    public SimpleObjectProperty<EnumGewichtung> lehrerHohlstundenProperty() {
        return lehrerHohlstunden;
    }

    public void setLehrerHohlstunden(EnumGewichtung lehrerHohlstunden) {
        this.lehrerHohlstunden.set(lehrerHohlstunden);
    }

    @XmlElement(name = "unerwuenschteLehrerZeitslots")
    public EnumGewichtung getUnerwuenschteLehrerZeitslots() {
        return unerwuenschteLehrerZeitslots.get();
    }

    public SimpleObjectProperty<EnumGewichtung> unerwuenschteLehrerZeitslotsProperty() {
        return unerwuenschteLehrerZeitslots;
    }

    public void setUnerwuenschteLehrerZeitslots(EnumGewichtung unerwuenschteLehrerZeitslots) {
        this.unerwuenschteLehrerZeitslots.set(unerwuenschteLehrerZeitslots);
    }

    @XmlElement(name = "weicheNachmittagsFaecher")
    public EnumGewichtung getWeicheNachmittagsFaecher() {
        return weicheNachmittagsFaecher.get();
    }

    public SimpleObjectProperty<EnumGewichtung> weicheNachmittagsFaecherProperty() {
        return weicheNachmittagsFaecher;
    }

    public void setWeicheNachmittagsFaecher(EnumGewichtung weicheNachmittagsFaecher) {
        this.weicheNachmittagsFaecher.set(weicheNachmittagsFaecher);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BerechnungsParameter that = (BerechnungsParameter) o;
        return Objects.equals(getRechendauer(), that.getRechendauer()) &&
                Objects.equals(getHarteFaecherFolgen(), that.getHarteFaecherFolgen()) &&
                Objects.equals(getLehrerHohlstunden(), that.getLehrerHohlstunden()) &&
                Objects.equals(getUnerwuenschteLehrerZeitslots(), that.getUnerwuenschteLehrerZeitslots()) &&
                Objects.equals(getWeicheNachmittagsFaecher(), that.getWeicheNachmittagsFaecher());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRechendauer(), getHarteFaecherFolgen(), getLehrerHohlstunden(), getUnerwuenschteLehrerZeitslots(), getWeicheNachmittagsFaecher());
    }
}
