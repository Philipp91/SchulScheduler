package schulscheduler.model.base;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Base class for elements with an observable ID and toString.
 */
public abstract class BaseElement implements IDElement {
    private static int idCounter = 0;

    private final SimpleIntegerProperty idProperty = new SimpleIntegerProperty(this, "id");

    private final ReadOnlyStringWrapper idStringProperty = new ReadOnlyStringWrapper(); // For "overrides".
    private final ReadOnlyStringWrapper toShortStringProperty = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper toLongStringProperty = new ReadOnlyStringWrapper();

    public BaseElement() {
        idProperty.set(idCounter++);
        idStringProperty.bind(idProperty.asString());
        toShortStringProperty.bind(Bindings.concat(getClass().getSimpleName(), " ", idStringProperty));
    }

    @Override
    public Integer getId() {
        return idProperty.get();
    }

    public void setId(final Integer id) {
        idProperty.set(id);
        if ((idCounter < id)) {
            idCounter = id;
        }
    }

    public SimpleIntegerProperty idProperty() {
        return idProperty;
    }

    protected ReadOnlyStringWrapper idStringProperty() {
        return idStringProperty;
    }

    @Override
    public String toString() {
        return toShortString();
    }

    public final String toShortString() {
        return toShortStringProperty.get();
    }

    public String toLongString() {
        return toLongStringProperty.get();
    }

}
