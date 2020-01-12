package schulscheduler.ui.controls;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;

/**
 * Eine Tabellenzelle, die eine {@link CheckBox} enthält und an eine {@link BooleanProperty} gebunden werden kann.
 * 
 * @param <S> Der Typ der TableView (d.h. S == TableView&lt;S&gt;)
 */
public class CheckBoxCell<S> extends BaseGraphicTableCell<S, Boolean> {

    /**
     * Die CheckBox, die die Zelle zur Anzeige verwendet.
     */
    private final CheckBox checkBox = new CheckBox();

    /**
     * Erstellt eine CheckBoxCell mit der Style-Class "checkbox-cell".
     */
    public CheckBoxCell() {
        super("checkbox-cell");
        forwardFocusChanges(this.checkBox);
    }

    @Override
    protected Node getContent() {
        return this.checkBox;
    }

    @Override
    protected void bind(Property<Boolean> property) {
        this.checkBox.selectedProperty().bindBidirectional(property);
    }

    @Override
    protected void unbind(Property<Boolean> property) {
        this.checkBox.selectedProperty().unbindBidirectional(property);
    }

}
