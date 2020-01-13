package schulscheduler.ui.controls;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

/**
 * Eine Tabellenspalte, die ihre Zellen als MultiSelectBoxen rendert, wenn sie editierbar ist, oder einfach eine
 * Auflistung der Items, wenn die Zelle nicht editierbar sein soll.
 *
 * @param <S> Der Typ der TableView (d.h. S == TableView&lt;S&gt;)
 * @param <E> Der Typ für die MultiSelectBox
 */
public class MultiSelectBoxColumn<S, E extends Comparable<? super E>> extends BasePropertyTableColumn<S, ObservableList<E>> {

    /**
     * Der Placeholder Text für die MultiSelectBox.
     */
    private final StringProperty promptText = new SimpleStringProperty();

    /**
     * Die Elemente, die in der Box zur Auswahl stehen.
     */
    private final ListProperty<E> items = new SimpleListProperty<>();

    /**
     * Erstellt eine MultiSelectBoxColumn.
     */
    public MultiSelectBoxColumn() {
        this.setCellFactory(param -> {
            MultiSelectBoxCell<S, E> cell = new MultiSelectBoxCell<>();
            cell.itemsProperty().bind(items);
            cell.promptTextProperty().bind(promptText);
            return cell;
        });
    }

    /**
     * @return Der Placeholder Text für die MultiSelectBox.
     */
    public String getPromptText() {
        return this.promptText.get();
    }

    /**
     * @param promptText Der Placeholder Text für die MultiSelectBox.
     */
    public void setPromptText(String promptText) {
        this.promptText.set(promptText);
    }

    /**
     * @return Der Placeholder Text für die MultiSelectBox.
     */
    public StringProperty promptTextProperty() {
        return this.promptText;
    }

    /**
     * @return Die Elemente, die in der MultiSelectBox zur Auswahl stehen.
     */
    public ObservableList<E> getItems() {
        return this.items.get();
    }

    /**
     * @param items Die Elemente, die in der MultiSelectBox zur Auswahl stehen.
     */
    public void setItems(ObservableList<E> items) {
        this.items.set(items);
    }

    /**
     * @return Die Elemente, die in der MultiSelectBox zur Auswahl stehen.
     */
    public ListProperty<E> itemsProperty() {
        return this.items;
    }

}
