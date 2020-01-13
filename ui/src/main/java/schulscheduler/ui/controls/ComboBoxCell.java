package schulscheduler.ui.controls;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.util.StringConverter;

/**
 * Eine Tabellenzelle, die eine {@link ComboBox} enthält und an eine {@link ObjectProperty} gebunden werden kann.
 *
 * @param <S> Der Typ der TableView (d.h. S == TableView&lt;S&gt;)
 * @param <E> Der Typ der ObjectProperty.
 */
public class ComboBoxCell<S, E> extends BaseGraphicTableCell<S, E> {

    /**
     * Die ComboBox, welche in der Zelle angezeigt wird.
     */
    private final ComboBox<E> comboBox = new ComboBox<>();

    /**
     * Das Label, das angezeigt wird, wenn die ComboBox leer ist.
     */
    private final Label placeholderLabel = new Label();

    /**
     * Erstellt eine neue ComboBoxCell.
     */
    public ComboBoxCell() {
        super("combo-box-cell");
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        setAlignment(Pos.CENTER);
        forwardFocusChanges(this.comboBox);

        // Wert leeren bei Strg+Click oder Shift+Click
        this.comboBox.setOnMouseReleased(event -> {
            if (event.isControlDown() || event.isShiftDown()) {
                comboBox.getSelectionModel().clearSelection();
                comboBox.hide();
                event.consume();
            }
        });
        this.comboBox.setCellFactory(param -> {
            BaseElementListCell<E> result = new BaseElementListCell<>();
            result.converterProperty().bind(comboBox.converterProperty());
            return result;
        });
        this.comboBox.setButtonCell(this.comboBox.getCellFactory().call(null));
        this.comboBox.setPlaceholder(placeholderLabel);

        this.placeholderLabel.setPadding(new Insets(3));
    }

    @Override
    protected Node getContent() {
        return this.comboBox;
    }

    @Override
    protected void bind(Property<E> property) {
        if (!(property instanceof ObjectProperty)) {
            throw new IllegalArgumentException("A ComboBoxCell cannot be bound to " + property);
        }
        if (((ObjectProperty<E>) property).get() == null) {
            this.comboBox.getSelectionModel().clearSelection();
        }
        this.comboBox.valueProperty().bindBidirectional(property);
    }

    @Override
    protected void unbind(Property<E> property) {
        this.comboBox.valueProperty().unbindBidirectional(property);
        this.comboBox.setValue(null);
    }

    /**
     * @return Die ComboBox, die zur Anzeige der Zelle verwendet wird.
     */
    public ComboBox<E> getComboBox() {
        return this.comboBox;
    }

    /**
     * @return Die Elemente, die in der ComboBox zur Auswahl stehen.
     */
    public ObservableList<E> getItems() {
        return this.comboBox.getItems();
    }

    /**
     * @param items Die Elemente, die in der ComboBox zur Auswahl stehen.
     */
    public void setItems(ObservableList<E> items) {
        this.comboBox.setItems(items);
    }

    /**
     * @return Die Elemente, die in der ComboBox zur Auswahl stehen.
     */
    public ObjectProperty<ObservableList<E>> itemsProperty() {
        return this.comboBox.itemsProperty();
    }

    /**
     * @return Der Text, der angezeigt wird, wenn die Liste leer ist.
     */
    public String getPlaceholder() {
        return this.placeholderLabel.getText();
    }

    /**
     * @param placeholder Der Text, der angezeigt wird, wenn die Liste leer ist.
     */
    public void setPlaceholder(String placeholder) {
        this.placeholderLabel.setText(placeholder);
    }

    /**
     * @return Der Text, der angezeigt wird, wenn die Liste leer ist.
     */
    public StringProperty placeholderProperty() {
        return this.placeholderLabel.textProperty();
    }

    /**
     * @return Der Converter, der die Elemente für die Anzeige konvertiert.
     */
    public StringConverter<E> getConverter() {
        return this.comboBox.getConverter();
    }

    /**
     * @param converter Der Converter, der die Elemente für die Anzeige konvertiert.
     */
    public void setConverter(StringConverter<E> converter) {
        this.comboBox.setConverter(converter);
    }

    /**
     * @return Der Converter, der die Elemente für die Anzeige konvertiert.
     */
    public ObjectProperty<StringConverter<E>> converterProperty() {
        return this.comboBox.converterProperty();
    }

}
