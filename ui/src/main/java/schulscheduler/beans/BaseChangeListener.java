package schulscheduler.beans;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * Vereinfacht die Implementierung von {@link ChangeListener}. Der jeweils aktuelle aller {@link ObservableValue}s, an
 * die ein solcher Listener gebunden wird, wird an {@link BaseChangeListener#bind(Object)} übergeben. Wenn ein Wert
 * entfernt oder durch einen anderen ersetzt wird, wird er an {@link #unbind(Object)} übergeben.
 * 
 * @param <T> Der Typ der überwachten {@link ObservableValue}s.
 */
public abstract class BaseChangeListener<T> implements ChangeListener<T> {

    /**
     * Bindet den neuen Wert an.
     * 
     * @param value Der neue Wert der Property.
     */
    public abstract void bind(T value);

    /**
     * Entfernt die Bindung von einem alten Wert.
     * 
     * @param value Der alte Wert der Property.
     */
    public abstract void unbind(T value);

    /**
     * Überwacht die gegebene {@link ObservableValue}. Der aktuelle Wert wird - sofern vorhanden - direkt an
     * {@link #bind(Object)} übergeben.
     * 
     * @param observable Die zu überwachende Property.
     */
    public void attachTo(ObservableValue<? extends T> observable) {
        observable.addListener(this);
        T value = observable.getValue();
        if (value != null) {
            bind(value);
        }
    }

    /**
     * Beendet die Überwachung der gegebenen {@link ObservableValue}. Der aktuelle Wert wird - sofern vorhanden - noch
     * an {@link #unbind(Object)} übergeben.
     * 
     * @param observable Die Property, deren Überwachung beendet werden soll.
     */
    public void detachFrom(ObservableValue<? extends T> observable) {
        T value = observable.getValue();
        if (value != null) {
            unbind(value);
        }
        observable.removeListener(this);
    }

    /**
     * Bearbeitet Änderungen der überwachten Property.
     * 
     * @param observable Die geänderte Property.
     * @param oldValue Der alte Wert der Property (wird an {@link #unbind(Object)} übergeben).
     * @param newValue Der neue Wert der Property (wird an {@link #bind(Object)} übergeben).
     */
    public final void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
        if (oldValue != null) {
            unbind(oldValue);
        }
        if (newValue != null) {
            bind(newValue);
        }
    }

}
