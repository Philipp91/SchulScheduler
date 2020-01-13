package schulscheduler.ui.controls;

import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;

/**
 * Eine Tabellenspalte, die ihre Zellen als ComboBoxen rendert, wenn sie editierbar ist, bzw. den
 * {@link Object#toString()} -Wert des Inhalts ausgibt, wenn sie nicht editierbar ist.
 *
 * @param <S> Der Typ der TableView (d.h. S == TableView&lt;S&gt;)
 * @param <E> Der Typ des auszuwählenden Objekts.
 */
public class ComboBoxColumn<S, E> extends BasePropertyTableColumn<S, E> {

    /**
     * Die Elemente, die in der ComboBox zur Auswahl stehen.
     */
    private final ListProperty<E> items = new SimpleListProperty<>();

    /**
     * Der Text, der angezeigt wird, wenn die Liste leer ist.
     */
    private final SimpleStringProperty placeholder = new SimpleStringProperty();

    /**
     * Konvertiert die Elemente für die Anzeige.
     */
    private final ObjectProperty<StringConverter<E>> converter = new SimpleObjectProperty<>(this, "converter", new ComboBox<E>().getConverter());

    /**
     * Erstellt eine ComboBoxColumn.
     */
    public ComboBoxColumn() {
        this.setCellFactory(param -> {
            ComboBoxCell<S, E> cell = new ComboBoxCell<>();
            cell.itemsProperty().bind(items);
            cell.placeholderProperty().bind(placeholder);
            cell.converterProperty().bind(converter);
            return cell;
        });
    }

    /**
     * @return Die Elemente, die in der ComboBox zur Auswahl stehen.
     */
    public ObservableList<E> getItems() {
        return this.items.get();
    }

    /**
     * @param items Die Elemente, die in der ComboBox zur Auswahl stehen.
     */
    public void setItems(ObservableList<E> items) {
        this.items.set(items);
    }

    /**
     * @return Die Elemente, die in der ComboBox zur Auswahl stehen.
     */
    public ListProperty<E> itemsProperty() {
        return this.items;
    }

    /**
     * @return Der Text, der angezeigt wird, wenn die Liste leer ist.
     */
    public String getPlaceholder() {
        return this.placeholder.get();
    }

    /**
     * @param placeholder Der Text, der angezeigt wird, wenn die Liste leer ist.
     */
    public void setPlaceholder(String placeholder) {
        this.placeholder.set(placeholder);
    }

    /**
     * @return Der Text, der angezeigt wird, wenn die Liste leer ist.
     */
    public StringProperty placeholderProperty() {
        return this.placeholder;
    }

    /**
     * @return Der Converter, der die Elemente für die Anzeige konvertiert.
     */
    public StringConverter<E> getConverter() {
        return this.converter.get();
    }

    /**
     * @param converter Der Converter, der die Elemente für die Anzeige konvertiert.
     */
    public void setConverter(StringConverter<E> converter) {
        this.converter.set(converter);
    }

    /**
     * @return Der Converter, der die Elemente für die Anzeige konvertiert.
     */
    public ObjectProperty<StringConverter<E>> converterProperty() {
        return this.converter;
    }

}
