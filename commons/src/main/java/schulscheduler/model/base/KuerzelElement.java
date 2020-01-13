package schulscheduler.model.base;

import javafx.beans.property.SimpleStringProperty;
import schulscheduler.javafx.MoreBindings;

import javax.xml.bind.annotation.XmlElement;

/**
 * Base class for elements with a name and short name that the user can enter.
 */
public abstract class KuerzelElement extends NamedElement {

    private final SimpleStringProperty kuerzel = new SimpleStringProperty(this, "kuerzel");

    public KuerzelElement() {
        idString.bind(MoreBindings.coalesceString(nameProperty(), idProperty().asString()));
        toShortString.bind(MoreBindings.coalesceString(kuerzelProperty(), nameProperty(), typeAndIdExpression));
    }

    public KuerzelElement(String name, String kuerzel) {
        this();
        setKuerzel(kuerzel);
        setName(name);
    }

    @XmlElement(name = "kuerzel")
    public String getKuerzel() {
        return kuerzel.get();
    }

    public SimpleStringProperty kuerzelProperty() {
        return kuerzel;
    }

    public void setKuerzel(String name) {
        this.kuerzel.set(name);
    }

    @Override
    public int compareTo(NamedElement o) {
        if (o instanceof KuerzelElement) {
            return nullSafeStringComparator.compare(this.getKuerzel(), ((KuerzelElement) o).getKuerzel());
        } else {
            return super.compareTo(o);
        }
    }
}
