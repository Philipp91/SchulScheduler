package schulscheduler.ui.controls;

import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;

/**
 * Eine Tabellenzelle, die eine {@link MultiSelectBox} enthält und an eine {@link ListProperty} gebunden werden kann.
 * 
 * @param <S> Der Typ der TableView (d.h. S == TableView&lt;S&gt;)
 * @param <E> Der Typ der MultiSelectBox
 */
public class MultiSelectBoxCell<S, E extends Comparable<? super E>> extends BaseGraphicTableCell<S, ObservableList<E>> {

    /**
     * Die MultiSelectBox, welche in der Zelle angezeigt wird.
     */
    private final MultiSelectBox<E> multiSelectBox = new MultiSelectBox<E>();

    /**
     * Erstellt eine neue MultiSelectBox
     */
    public MultiSelectBoxCell() {
        super("multi-select-box-cell");
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        setAlignment(Pos.CENTER);
        forwardFocusChanges(this.multiSelectBox);
    }

    @Override
    protected Node getContent() {
        return this.multiSelectBox;
    }

    @Override
    protected void bind(Property<ObservableList<E>> property) {
        this.multiSelectBox.selectedProperty().bindBidirectional(property);
    }

    @Override
    protected void unbind(Property<ObservableList<E>> property) {
        this.multiSelectBox.selectedProperty().unbindBidirectional(property);
    }

    /**
     * @return Die MultiSelectBox, die zur Anzeige der Zelle verwendet wird.
     */
    public MultiSelectBox<E> getMultiSelectBox() {
        return this.multiSelectBox;
    }

    /**
     * @return Die Elemente, die in der Box zur Auswahl stehen.
     */
    public ObservableList<E> getItems() {
        return this.multiSelectBox.getItems();
    }

    /**
     * @param items Die Elemente, die in der Box zur Auswahl stehen.
     */
    public void setItems(ObservableList<E> items) {
        this.multiSelectBox.setItems(items);
    }

    /**
     * @return Die Elemente, die in der Box zur Auswahl stehen.
     */
    public ListProperty<E> itemsProperty() {
        return this.multiSelectBox.itemsProperty();
    }

    /**
     * @return Der Placeholder Text für die MultiSelectBox.
     */
    public String getPromptText() {
        return this.multiSelectBox.getPromptText();
    }

    /**
     * @param promptText Der Placeholder Text für die MultiSelectBox.
     */
    public void setPromptText(String promptText) {
        this.multiSelectBox.setPromptText(promptText);
    }

    /**
     * @return Der Placeholder Text für die MultiSelectBox.
     */
    public StringProperty promptTextProperty() {
        return this.multiSelectBox.promptTextProperty();
    }

}
