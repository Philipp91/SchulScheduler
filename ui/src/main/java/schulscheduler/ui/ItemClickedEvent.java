package schulscheduler.ui;

import javafx.event.ActionEvent;

/**
 * Wird ausgelöst, wenn ein Eintrag aus einer Liste/Tabelle angeklickt bzw. ausgewählt wurde.
 *
 * @param <T> Der Typ der Elemente in der Liste/Tabelle.
 */
public class ItemClickedEvent<T> extends ActionEvent {

    private final T item;

    /**
     * @param item Das angeklickte/ausgewählte Element.
     */
    public ItemClickedEvent(T item) {
        this.item = item;
    }

    /**
     * @return Das angeklickte/ausgewählte Element.
     */
    public T getItem() {
        return item;
    }

}
