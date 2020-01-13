package schulscheduler.ui.controls;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

/**
 * Die MultiSelectBox dient dazu einzelne Objekte aus einer Liste auszuwählen. Die auswählbaren Einträge werden in einer
 * Liste in einem Popup dargestellt, das bei Bedarf aufgerufen werden kann. Darin können einzelene Einträge ausgewählt
 * werden. In der Liste kann nach einzelnen Einträgen gefiltert werden. Wenn ein Eintrag ausgewählt wurde, wird dieser
 * anschließend in einem Flowpane, welches die ausgewählten Einträge anzeigt, hinzugefügt. Bereits ausgewählte Einträge
 * können anschließend wieder gelöscht werden.
 *
 * @param <E> Der Typ der auswählbaren Einträge.
 */
public class MultiSelectBox<E extends Comparable<? super E>> extends Control {

    /**
     * Enthält die ausgewählten Einträge.
     */
    private final ListProperty<E> selected = new SimpleListProperty<>(FXCollections.observableArrayList());

    /**
     * Der Placeholder Text, der angezeigt wird, wenn keine Einträge ausgewählt sind.
     */
    private final StringProperty promptText = new SimpleStringProperty();

    /**
     * Enthält die gesamte Anzahl an Einträgen, die ausgewählt werden können.
     */
    private final ListProperty<E> items = new SimpleListProperty<>(FXCollections.observableArrayList());

    /**
     * Erstellt eine neue MultiSelectBox und erstellt einen neuen Skin für diese Klasse.
     */
    public MultiSelectBox() {
        getStyleClass().add("multiselectbox");
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new MultiSelectBoxSkin<>(this);
    }

    /**
     * Gibt die ausgewählten Einträge zurück.
     *
     * @return value
     */
    public ListProperty<E> selectedProperty() {
        return selected;
    }

    /**
     * Gibt die Items, die ausgewählt werden können zurück.
     *
     * @return items ObservableList die die Items enthält.
     */
    public ObservableList<E> getItems() {
        return items.get();
    }

    /**
     * Legt die Liste an Einträgen fest, die ausgewählt werden können.
     *
     * @param items Die Liste mit Items, die ausgewählt werden sollen.
     */
    public void setItems(ObservableList<E> items) {
        this.items.set(items);
    }

    /**
     * Gibt die Property für die Itemliste zurück.
     *
     * @return items Die Liste mit den items.
     */
    public ListProperty<E> itemsProperty() {
        return items;
    }

    /**
     * Gibt die Observablist der Property für die selektierten items zurück.
     *
     * @return items, die selektiert wurden.
     */
    public ObservableList<E> getSelected() {
        return selected.get();
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

    @Override
    public void requestFocus() {
        if (getSkin() != null) {
            getSkin().getNode().requestFocus();
        }
    }

}
