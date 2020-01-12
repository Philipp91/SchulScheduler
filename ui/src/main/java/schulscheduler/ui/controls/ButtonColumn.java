package schulscheduler.ui.controls;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import schulscheduler.ui.ItemClickedEvent;

/**
 * Eine Tabellenspalte, die ihre Zellen als Buttons rendert.
 * 
 * @param <S> Der Typ der TableView (d.h. S == TableView&lt;S&gt;)
 */
public class ButtonColumn<S> extends BasePropertyTableColumn<S, Boolean> {

    /**
     * Die Aktion, die ausgeführt wird, wenn ein Button angeklickt wird.
     */
    private final ObjectProperty<EventHandler<ItemClickedEvent<S>>> onAction = new SimpleObjectProperty<>();

    /**
     * Die Aufschrift der Buttons.
     */
    private final StringProperty label = new SimpleStringProperty();

    /**
     * Erstellt eine ButtonColumn.
     */
    public ButtonColumn() {
        this.setCellFactory(new Callback<TableColumn<S, Boolean>, TableCell<S, Boolean>>() {
            public TableCell<S, Boolean> call(TableColumn<S, Boolean> param) {
                ButtonCell<S> cell = new ButtonCell<>();
                cell.labelProperty().bind(label);
                return cell;
            }
        });
    }

    /**
     * Führt die {@link #getOnAction()} für das angeklickte Element aus.
     * 
     * @param item Das angeklickte Element (Datensatz der angeklickten Zeile).
     */
    void onButtonClicked(S item) {
        EventHandler<ItemClickedEvent<S>> handler = getOnAction();
        if (handler != null && item != null) {
            handler.handle(new ItemClickedEvent<S>(item));
        }
    }

    /**
     * @return Die Aktion, die ausgeführt wird, wenn der Button angeklickt wird.
     */
    public EventHandler<ItemClickedEvent<S>> getOnAction() {
        return this.onAction.get();
    }

    /**
     * @param onAction Die Aktion, die ausgeführt wird, wenn der Button angeklickt wird.
     */
    public void setOnAction(EventHandler<ItemClickedEvent<S>> onAction) {
        this.onAction.set(onAction);
    }

    /**
     * @return Die Aktion, die ausgeführt wird, wenn der Button angeklickt wird.
     */
    public ObjectProperty<EventHandler<ItemClickedEvent<S>>> onActionProperty() {
        return this.onAction;
    }

    /**
     * @return Die Aufschrift der Buttons.
     */
    public String getLabel() {
        return this.label.get();
    }

    /**
     * @param label Die Aufschrift der Buttons.
     */
    public void setLabel(String label) {
        this.label.set(label);
    }

    /**
     * @return Die Aufschrift der Buttons.
     */
    public StringProperty labelProperty() {
        return this.label;
    }

}
