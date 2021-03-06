package schulscheduler.model.base;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleIntegerProperty;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import java.util.Objects;

/**
 * Base class for elements with an observable ID and toString.
 */
public abstract class BaseElement implements IDElement {
    private static int idCounter = 0;

    private final SimpleIntegerProperty id = new SimpleIntegerProperty(this, "id");

    protected final ReadOnlyStringWrapper idString = new ReadOnlyStringWrapper(); // For "overrides".
    protected final StringExpression typeAndIdExpression = Bindings.concat(getClass().getSimpleName(), " ", idString);
    protected final ReadOnlyStringWrapper toShortString = new ReadOnlyStringWrapper();
    protected final ReadOnlyStringWrapper toLongString = new ReadOnlyStringWrapper();

    public BaseElement() {
        id.set(idCounter++);
        idString.bind(id.asString());
        toShortString.bind(typeAndIdExpression);
        toLongString.bind(typeAndIdExpression);
    }

    @XmlAttribute(name = "id")
    @XmlID
    @Override
    public Integer getId() {
        return id.get();
    }

    public void setId(final Integer id) {
        this.id.set(id);
        if ((idCounter < id)) {
            idCounter = id;
        }
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    protected ReadOnlyStringProperty idStringProperty() {
        return idString.getReadOnlyProperty();
    }

    @Override
    public String toString() {
        return toShortString();
    }

    public final String toShortString() {
        return toShortString.get();
    }

    public ReadOnlyStringProperty toShortStringProperty() {
        return toShortString.getReadOnlyProperty();
    }

    public String toLongString() {
        return toLongString.get();
    }

    public ReadOnlyStringProperty toLongStringProperty() {
        return toLongString.getReadOnlyProperty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseElement that = (BaseElement) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
