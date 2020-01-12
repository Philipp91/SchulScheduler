package schulscheduler.model.base;

import javafx.beans.property.SimpleStringProperty;
import schulscheduler.javafx.MoreBindings;

import java.util.Comparator;

/**
 * Base class for elements with a name that the user can enter.
 */
public abstract class NamedElement extends BaseElement implements Comparable<NamedElement> {

    protected static final Comparator<String> nullSafeStringComparator = Comparator.nullsFirst(String::compareToIgnoreCase);

    private final SimpleStringProperty name = new SimpleStringProperty();

    public NamedElement() {
        idStringProperty().bind(MoreBindings.coalesceString(nameProperty(), idProperty().asString()));
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    @Override
    public int compareTo(NamedElement o) {
        return nullSafeStringComparator.compare(this.getName(), o.getName());
    }
}
