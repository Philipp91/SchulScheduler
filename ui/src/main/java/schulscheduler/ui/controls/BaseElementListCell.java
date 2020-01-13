package schulscheduler.ui.controls;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import javafx.util.StringConverter;
import schulscheduler.model.base.BaseElement;

/**
 * Eine ListCell-Implementierung, die ihren Zelleninhalt an die {@link BaseElement#toShortStringProperty()} bindet, wenn
 * es sich um einen {@link BaseElement} handelt - und ansonsten normal {@link #toString()} verwendet.
 *
 * @param <E> Der Typ der anzuzeigenden Elemente.
 */
public class BaseElementListCell<E> extends ListCell<E> {

    /**
     * @param <E> Der Typ der Elemente im ListView.
     * @return Eine Factory für BeanListCells.
     */
    public static <E> Callback<ListView<E>, ListCell<E>> createFactory() {
        return listView -> new BaseElementListCell<>();
    }

    /**
     * @param <E> Der Typ der Elemente im ListView.
     * @param converter Der Converter, der die Elemente für die Anzeige konvertiert.
     * @return Eine Factory für BeanListCells.
     */
    public static <E> Callback<ListView<E>, ListCell<E>> createFactory(StringConverter<E> converter) {
        return listView -> {
            BaseElementListCell<E> cell = new BaseElementListCell<>();
            cell.setConverter(converter);
            return cell;
        };
    }

    /**
     * Konvertiert die Elemente für die Anzeige.
     */
    private final ObjectProperty<StringConverter<E>> converter = new SimpleObjectProperty<>(this, "converter", null);

    /**
     * Das aktuell angezeigte Element.
     */
    private E currentItem;

    /**
     * Erstellt eine neue BeanListCell.
     */
    public BaseElementListCell() {
        setContentDisplay(ContentDisplay.TEXT_ONLY);
    }

    @Override
    protected void updateItem(E item, boolean empty) {
        super.updateItem(item, empty);

        if (item != currentItem) {
            if (currentItem != null) {
                textProperty().unbind();
                currentItem = null;
            }

            currentItem = item;

            if (currentItem == null) {
                setText(null);
            } else if (currentItem instanceof BaseElement) {
                textProperty().bind(((BaseElement) currentItem).toShortStringProperty());
            } else if (converter.get() == null) {
                setText(currentItem.toString());
            } else {
                setText(converter.get().toString(currentItem));
            }
        } else if (currentItem != null && !(currentItem instanceof BaseElement)) {
            if (converter.get() == null) {
                setText(currentItem.toString());
            } else {
                setText(converter.get().toString(currentItem));
            }
        }

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
