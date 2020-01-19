package schulscheduler.model.unterricht;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import schulscheduler.model.base.NamedElement;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

@XmlRootElement(name = "profil")
public class Profil extends NamedElement {

    private final SimpleListProperty<ProfilEintrag> eintraege = new SimpleListProperty<>(this, "eintraege", FXCollections.observableArrayList());

    @XmlElement(name = "eintraege")
    public ObservableList<ProfilEintrag> getEintraege() {
        return eintraege.get();
    }

    public SimpleListProperty<ProfilEintrag> eintraegeProperty() {
        return eintraege;
    }

    public void setEintraege(ObservableList<ProfilEintrag> eintraege) {
        this.eintraege.set(eintraege);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Profil profil = (Profil) o;
        return Objects.equals(getEintraege(), profil.getEintraege());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getEintraege());
    }
}
