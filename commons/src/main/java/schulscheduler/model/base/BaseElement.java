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

    private final ReadOnlyStringWrapper idString = new ReadOnlyStringWrapper(); // For "overrides".
    private final ReadOnlyStringWrapper toShortString = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper toLongString = new ReadOnlyStringWrapper();

    public BaseElement() {
        idProperty.set(idCounter++);
        idString.bind(idProperty.asString());
        toShortString.bind(Bindings.concat(getClass().getSimpleName(), " ", idString));
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
        return idString;
    }

    @Override
    public String toString() {
        return toShortString();
    }

    public final String toShortString() {
        return toShortString.get();
    }

    public ReadOnlyStringWrapper toShortStringProperty() {
        return toShortString;
    }

    public String toLongString() {
        return toLongString.get();
    }

    public ReadOnlyStringWrapper toLongStringProperty() {
        return toLongString;
    }
}
