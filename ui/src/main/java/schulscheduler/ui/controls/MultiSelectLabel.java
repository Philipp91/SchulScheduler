package schulscheduler.ui.controls;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import schulscheduler.model.base.BaseElement;
import schulscheduler.ui.ItemClickedEvent;

import java.util.Objects;

/**
 * Stellt einen Eintrag in einer MultiSelectBox dar. Ein MultiSelectLabel besteht aus einem Label mit Text des
 * Eintrages und einem Löschen-Button. Per Bestätigung des Löschen-Buttons wird das MultiSelectLabel aus der MultiSelectBox
 * gelöscht und wird anschließend wieder auswählbar.
 *
 * @param <E> Der Typ, welcher im MultiSelectLabels enthalten ist.
 */
public class MultiSelectLabel<E> extends HBox {

    /**
     * Label mit dem Eintrag
     */
    private final Label textLabel = new Label();

    /**
     * Löschen-Button
     */
    private final Button deleteButton = new Button();

    /**
     * Das Objekt, welches dargestellt wird.
     */
    private final E object;

    /**
     * Listener für das Löschen des Elements.
     */
    private ObjectProperty<EventHandler<ItemClickedEvent<E>>> onDelete = new SimpleObjectProperty<>();

    /**
     * Erstellt ein MultiSelectLabel und bindet den Text des Objekts an das Label.
     *
     * @param object Das Object, welches den Inhalt für das MultiSelectLabel enthält.
     */
    public MultiSelectLabel(E object) {
        this.object = Objects.requireNonNull(object);
        if (object instanceof BaseElement) {
            textLabel.textProperty().bind(((BaseElement) object).toShortStringProperty());
        } else {
            textLabel.setText(object.toString());
        }
        initialize();
    }

    /**
     * Initialisiert die Komponenten. Legt Größe und ActionListener fest.
     */
    private void initialize() {
        getStyleClass().add("multiselect-label");
        textLabel.getStyleClass().add("multiselect-label-text");
        // Initialisiert den Button
        deleteButton.getStyleClass().add("multiselect-deletebutton");
        deleteButton.setOnAction(event -> {
            EventHandler<ItemClickedEvent<E>> handler = getOnDelete();
            if (handler != null) {
                handler.handle(new ItemClickedEvent<>(object));
            }
        });
        // Sub-Komponenten anzeigen
        this.getChildren().setAll(textLabel, deleteButton);
    }

    /**
     * @param onDelete Die Aktion die ausgeführt wird, wenn das Objekt gelöscht wird.
     */
    public void setOnDelete(EventHandler<ItemClickedEvent<E>> onDelete) {
        this.onDelete.set(onDelete);
    }

    /**
     * @return onDelete Die Aktion für das Löschen.
     */
    public EventHandler<ItemClickedEvent<E>> getOnDelete() {
        return onDelete.get();
    }

    /**
     * @return onDelete Die Property, die die Aktion für das Löschen enthält.
     */
    public ObjectProperty<EventHandler<ItemClickedEvent<E>>> onDeleteProperty() {
        return onDelete;
    }

    /**
     * Gibt die Breite des TextLabels zurück
     *
     * @return die Breite des TextLabels
     */
    public double getLabelWidth() {
        return textLabel.getPrefWidth();
    }

    /**
     * Gibt das Objekt, welches in dem MultiSelectLabel dargestellt wird zurück.
     *
     * @return object Das dargestellte Objekt.
     */
    public E getObject() {
        return object;
    }

    /**
     * Gibt den Text des Labels zurück.
     *
     * @return der Text des textLabels.
     */
    public String getText() {
        return textLabel.getText();
    }

}
