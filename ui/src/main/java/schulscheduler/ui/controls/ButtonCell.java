package schulscheduler.ui.controls;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;

import java.util.List;

/**
 * Eine Tabellenzelle, die einen {@link Button} enthält. Wenn die Zelle an eine {@link BooleanProperty} gebunden wird,
 * gibt diese an, ob der Button disabled ist oder nicht.
 *
 * @param <S> Der Typ der TableView (d.h. S == TableView&lt;S&gt;)
 */
public class ButtonCell<S> extends BaseGraphicTableCell<S, Boolean> {

    /**
     * Der Button, den die Zelle zur Anzeige verwendet.
     */
    private final Button button = new Button();

    /**
     * Erstellt eine normale ButtonCell mit der Style-Class "button-cell".
     */
    public ButtonCell() {
        super("button-cell");
        forwardFocusChanges(this.button);
        this.button.setOnAction(event -> ((ButtonColumn<S>) getTableColumn()).onButtonClicked(getRowData()));
    }

    @Override
    protected Node getContent() {
        return this.button;
    }

    @Override
    protected boolean allowNullProperties() {
        return true; // Default ist "immer enabled".
    }

    @Override
    protected void bind(Property<Boolean> property) {
        if (property != null) {
            this.button.disableProperty().bind(property);
        } else {
            this.button.setDisable(false);
        }
    }

    @Override
    protected void unbind(Property<Boolean> property) {
        this.button.disableProperty().unbind();
    }

    /**
     * @return Das Element, das in der aktuellen Tabellenzeile dargestellt wird, oder <tt>null</tt>, wenn die Zeile leer
     * ist.
     */
    private S getRowData() {
        int index = getIndex();
        if (index < 0) {
            return null;
        }

        // Get the table
        final TableView<S> table = getTableView();
        if (table == null || table.getItems() == null) {
            return null;
        }

        // Get the rowData
        final List<S> items = table.getItems();
        if (index >= items.size()) {
            return null; // Out of range
        }

        return items.get(index);
    }

    /**
     * @return Die Beschriftung des Buttons.
     */
    public StringProperty labelProperty() {
        return this.button.textProperty();
    }

}
