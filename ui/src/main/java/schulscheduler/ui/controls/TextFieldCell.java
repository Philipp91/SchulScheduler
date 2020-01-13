package schulscheduler.ui.controls;

import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.TextField;

/**
 * Eine Tabellenzelle, die ein {@link TextField} enthält und an eine {@link StringProperty} gebunden werden kann.
 * 
 * @param <S> Der Typ der TableView (d.h. S == TableView&lt;S&gt;)
 */
public class TextFieldCell<S> extends BaseGraphicTableCell<S, String> {

    /**
     * Das Textfeld, das die Zelle zur Anzeige verwendet.
     */
    private final TextField textField = new TextField();

    /**
     * Erstellt eine TextFieldCell mit der Style-Class "textfield-cell".
     */
    public TextFieldCell() {
        super("textfield-cell");
        forwardFocusChanges(textField);
    }

    @Override
    protected Node getContent() {
        return this.textField;
    }

    @Override
    protected void bind(Property<String> property) {
        this.textField.textProperty().bindBidirectional(property);
    }

    @Override
    protected void unbind(Property<String> property) {
        this.textField.textProperty().unbindBidirectional(property);
    }

    /**
     * @return Siehe {@link TextField#promptTextProperty()}.
     */
    public StringProperty promptTextProperty() {
        return this.textField.promptTextProperty();
    }

}
