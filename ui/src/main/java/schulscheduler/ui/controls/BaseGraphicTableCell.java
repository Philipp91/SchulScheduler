package schulscheduler.ui.controls;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewFocusModel;
import schulscheduler.beans.BaseChangeListener;

/**
 * Basisklasse für Tabellenzellen, die ihren Inhalt als einzelne {@link Node} rendern (auch im Nicht-Edit-Modus) und
 * diese an eine Property vom Typ <tt>T</tt> anbinden.
 *
 * @param <S> Der Zeilen-Typ der Tabelle.
 * @param <T> Der Zellen-Typ der Spalte.
 */
public abstract class BaseGraphicTableCell<S, T> extends TableCell<S, T> {

    /**
     * Die Property an die die Zelle derzeit gebunden ist.
     */
    private Property<T> currentProperty;

    /**
     * Wird aufgerufen wenn eines der in der Zelle enthaltenen UI-Elemente fokussiert wird. Dann muss auch die Zelle
     * fokussiert werden.
     */
    private final ChangeListener<Boolean> onContentFocusListener = (observable, oldValue, newValue) -> {
        if (newValue) {
            getTableView().getSelectionModel().select(getTableRow().getIndex());
        }
    };

    /**
     * Sorgt dafür, dass die {@link TableViewFocusModel#focusedCellProperty()} des jeweils aktuellen
     * {@link TableView#getFocusModel()} von {@link #focusedCellListener} überwacht wird.
     */
    @SuppressWarnings("FieldCanBeLocal")
    private final BaseChangeListener<TableView<S>> tableViewListener = new BaseChangeListener<>() {
        public void bind(TableView<S> tableView) {
            tableView.getFocusModel().focusedCellProperty().addListener(weakFocusedCellListener);
            Platform.runLater(() -> updateFocus());
        }

        public void unbind(TableView<S> tableView) {
            tableView.getFocusModel().focusedCellProperty().removeListener(weakFocusedCellListener);
        }
    };

    /**
     * Aufgrund von https://javafx-jira.kenai.com/browse/RT-29369 und https://javafx-jira.kenai.com/browse/RT-31808
     * können {@link TableCell}s derzeit nicht reagieren, wenn sie fokussiert werden. Dieser Listener bildet zusammen
     * mit updateFocus() die entsprechenden Member aus {@link TableCell} nach, um reagieren zu können, wenn die
     * Zelle mittels des {@link TableViewFocusModel} fokussiert wird.
     */
    private final InvalidationListener focusedCellListener = value -> updateFocus();

    /**
     * Teil des Workarounds um mitzubekommen, wenn die Zelle fokussiert wird. Siehe {@link #focusedCellListener}.
     */
    private final WeakInvalidationListener weakFocusedCellListener = new WeakInvalidationListener(focusedCellListener);

    /**
     * Teil des Workarounds um mitzubekommen, wenn die Zelle fokussiert wird. Siehe {@link #focusedCellListener}.
     *
     * @param i Der neue Index der Zeile, in der die Zelle eingesetzt wird.
     */
    @Override
    public void updateIndex(int i) {
        super.updateIndex(i);
        updateFocus();
    }

    /**
     * Teil des Workarounds um mitzubekommen, wenn die Zelle fokussiert wird. Siehe {@link #focusedCellListener}.
     */
    private void updateFocus() {
        TableView<S> tableView = getTableView();
        if (tableView == null) {
            return;
        }

        TableViewFocusModel<S> focusModel = tableView.getFocusModel();
        if (focusModel == null) {
            return;
        }

        if (focusModel.isFocused(getIndex(), getTableColumn())) {
            onReceivedFocus();
        }
    }

    /**
     * Wird ausgeführt, wenn die Zelle über das {@link TableViewFocusModel} den Fokus erhält. Da die Zelle in
     * {@link #graphicProperty()} eigenen Inhalt enthält, sollte dieser dann sinnvoll fokussiert werden.
     */
    protected void onReceivedFocus() {
        if (!getContent().isFocused()) {
            getContent().requestFocus();
        }
    }

    /**
     * Erstellt eine neue Tabellenzelle.
     *
     * @param styleClass Zusätzlicher Wert für das styleClass Attribut.
     */
    public BaseGraphicTableCell(String styleClass) {
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        getStyleClass().add(styleClass);
        tableViewListener.attachTo(tableViewProperty());
    }

    /**
     * @return Der Inhalt der Tabellenzelle.
     */
    protected abstract Node getContent();

    /**
     * Bindet den Inhalt der Tabellenzelle an die gegebene Property.
     *
     * @param property Die Property, an die gebunden werden soll. Dieser Parameter kann <tt>null</tt> sein, wenn
     * {@link #allowNullProperties()} <tt>true</tt> zurückgegeben hat.
     */
    protected abstract void bind(Property<T> property);

    /**
     * Löst die Bindung von {@link #bind(Property)} wieder auf. Diese Methode wird genau ein Mal pro Aufruf von
     * {@link #bind(Property)} aufgerufen, wobei es für <tt>bind</tt>-Aufrufe mit <tt>null</tt> als <tt>property</tt>
     * -Parameter keinen <tt>unbind</tt>-Aufruf gibt.
     *
     * @param property Die Property, von der die Bindung gelöst werden soll. Dieser Parameter ist nie <tt>null</tt>.
     */
    protected abstract void unbind(Property<T> property);

    /**
     * @return True, wenn die Zelle keine Property braucht, an die sie gebunden werden kann.
     */
    protected boolean allowNullProperties() {
        return false;
    }

    /**
     * Fokussiert die gesamte Tabellenzelle, wenn die gegebene Node fokussiert wird.
     *
     * @param node Das UI-Element, dessen Fokusänderungen weitergeleitet werden sollen. Das ist typischerweise der
     * Inhalt der Tabellenzelle, also {@link #getContent()}.
     */
    protected void forwardFocusChanges(Node node) {
        node.focusedProperty().addListener(onContentFocusListener);
    }

    /**
     * Löst die Bindung von der aktuellen Property.
     */
    protected void unbind() {
        if (currentProperty != null) {
            unbind(currentProperty);
            currentProperty = null;
        }
    }

    /**
     * Zeigt den Inhalt der Zelle nur an, wenn die Zelle auch einen hat. Der Inhalt {@link #getContent()} wird mittels
     * {@link #bind(Property)} angebunden, die Bindung zum alten Inhalt mittels {@link #unbind(Property)} gelöst.
     *
     * @param item Der Wert der in der Zelle angezeigt werden soll.
     * @param empty True wenn die Zelle leer ist.
     */
    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        if (empty && (getIndex() >= getTableView().getItems().size() || getIndex() == -1)) {
            // Wenn die Zelle nur benutzt wird, um eine leere Tabellenzeile zu rendern, darf natürlich kein Inhalt
            // sichtbar sein.
            setGraphic(null);
            unbind();

        } else {

            // Den Zelleninhalt anzeigen.
            setGraphic(getContent());

            // Die Property ermitteln, an die das Feld gebunden werden soll.
            ObservableValue<T> observable = getTableColumn().getCellObservableValue(getIndex());
            if (!(observable instanceof Property || (allowNullProperties() && observable == null))) {
                if (item == null && !getTableView().getColumns().contains(getTableColumn())) {
                    // Die TableViewSkinBase-Implementierung hat einen Bug:
                    // Wenn man Spalten löscht und gleichzeitig (d.h. noch vor dem nächsten Layout-Pass) den Inhalt der
                    // Tabelle ändert (insbesondere neue Zeilen hinzufügt), dann updated der Skin auch Tabellenzellen,
                    // die in einer der gelöschten Spalten sind. Dieses Update kann dann zu einem Fehler führen, der
                    // hier abgefangen wird. In diesem fall sollte sich die Zelle einfach so verhalten, als wäre das
                    // Update nicht aufgerufen worden, und weiterhin eine leere Zelle anzeigen. Siehe Ticket #289.
                    setGraphic(null);
                    unbind();
                    return;
                } else {
                    throw new IllegalArgumentException("A " + getClass().getSimpleName() + " cannot be bound to " + observable);
                }
            }
            Property<T> newProperty = (Property<T>) observable;

            // Nur wenn sich die Property geändert hat, gibt es überhaupt etwas zu tun.
            if (newProperty != this.currentProperty) {

                // Zunächst die Bindung zur alten Property aufheben, sofern vorhanden.
                if (this.currentProperty != null) {
                    unbind(currentProperty);
                }

                // Dann die neue Property anbinden
                bind(newProperty);
                this.currentProperty = newProperty;
            }

        }
    }

}
