package schulscheduler.beans;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * Vereinfacht die Implementierung von {@link ListChangeListener}. Entpackt das {@link Change}-Objekt und gibt es als
 * Einzelaufrufe an die implementierende Kindklasse weiter.
 * 
 * @param <E> Der Elementtyp der Liste.
 */
public abstract class BaseListChangeListener<E> implements ListChangeListener<E> {

    @Override
    public void onChanged(ListChangeListener.Change<? extends E> change) {
        change.reset();
        while (change.next()) {
            if (change.wasAdded()) {
                for (E item : change.getAddedSubList()) {
                    onAdded(change.getList(), item);
                }
            }
            if (change.wasRemoved()) {
                for (E item : change.getRemoved()) {
                    onRemoved(change.getList(), item);
                }
            }
        }
    }

    /**
     * Wird aufgerufen, wenn ein neues Element zur Liste hinzugefügt wurde.
     * 
     * @param list Die Liste, zu der das Element hinzugefügt wurde.
     * @param item Das hinzugefügte Element.
     */
    public abstract void onAdded(ObservableList<? extends E> list, E item);

    /**
     * Wird aufgerufen, wenn ein Element aus der Liste entfernt wurde.
     * 
     * @param list Die Liste, aus der das Element entfernt wurde.
     * @param item Das entfernte Element.
     */
    public abstract void onRemoved(ObservableList<? extends E> list, E item);

    /**
     * Führt zunächst {@link #onAdded(ObservableList, Object)} auf alle bereits vorhandenen Objekte aus und fügt dann
     * den Listener zur Property hinzu, damit auch zukünftige Objekte erkannt werden.
     * 
     * @param property Die zu überwachende Property.
     */
    public void attachTo(ObservableList<? extends E> property) {
        for (E item : property) {
            onAdded(property, item);
        }
        property.addListener(this);
    }

    /**
     * Macht {@link #attachTo(ObservableList)} wieder rückgängig, indem zunächst der Listener entfernt wird und
     * anschließend {@link #onRemoved(ObservableList, Object)} auf alle in der Liste noch vorhandenen Elemente
     * aufgerufen wird.
     * 
     * @param property Die überwachte Property.
     */
    public void detachFrom(ObservableList<? extends E> property) {
        property.removeListener(this);
        for (E item : property) {
            onRemoved(property, item);
        }
    }

}
