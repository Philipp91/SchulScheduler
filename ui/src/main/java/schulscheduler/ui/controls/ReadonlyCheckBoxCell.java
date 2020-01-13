package schulscheduler.ui.controls;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 * Eine Tabellenzelle, die eine nicht editierbare {@link CheckBox} enthält und an ein {@link ObservableValue}
 * &lt;Boolean&gt; gebunden werden kann.
 * 
 * @param <S> Der Typ der TableView (d.h. S == TableView&lt;S&gt;)
 */
public class ReadonlyCheckBoxCell<S> extends TableCell<S, Boolean> {

    /**
     * @param <S> Der Typ der Tabellenzeilen.
     * @return Eine Factory für nicht editierbare CheckBox-Zellen.
     */
    public static <S> Callback<TableColumn<S, Boolean>, TableCell<S, Boolean>> createFactory() {
        return new Callback<TableColumn<S, Boolean>, TableCell<S, Boolean>>() {
            public TableCell<S, Boolean> call(TableColumn<S, Boolean> param) {
                return new ReadonlyCheckBoxCell<>();
            }
        };
    }

    /**
     * Die CheckBox, die die Zelle zur Anzeige verwendet.
     */
    private final CheckBox checkBox = new CheckBox();

    /**
     * Der Wert, an die die Zelle gebunden ist.
     */
    private ObservableValue<Boolean> currentValue = null;

    /**
     * Erstellt eine ReadonlyCheckBoxCell mit der Style-Class "checkbox-cell".
     */
    public ReadonlyCheckBoxCell() {
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        getStyleClass().add("checkbox-cell");
        this.checkBox.setDisable(true);
        this.checkBox.focusedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    onCheckBoxFocus();
                }
            }
        });
    }

    /**
     * Wenn die CheckBox angeklickt wird, muss sich die Zelle so verhalten, als wäre die Zelle selbst angeklickt worden,
     * d.h. die Zeile, in der sich die Zelle befindet, muss markiert werden. (Sonst wäre es in einer Tabelle, die nur
     * solche Zellen enthält, gar nicht mehr möglich, eine Zeile zum Löschen zu markieren.)
     */
    protected void onCheckBoxFocus() {
        getTableView().getSelectionModel().select(getTableRow().getIndex());
    }

    @Override
    protected void updateItem(Boolean item, boolean empty) {
        super.updateItem(item, empty);

        if (empty && (getIndex() >= getTableView().getItems().size() || getIndex() == -1)) {
            // Wenn die Zelle nur benutzt wird, um eine leere Tabellenzeile zu rendern, darf natürlich keine CheckBox
            // sichtbar sein.
            this.setGraphic(null);
            if (this.currentValue != null) {
                this.checkBox.selectedProperty().unbind();
                this.currentValue = null;
            }

        } else {
            // Die CheckBox anzeigen.
            this.setGraphic(checkBox);

            // Den Wert ermitteln, an die die CheckBox gebunden werden soll.
            ObservableValue<Boolean> observable = getTableColumn().getCellObservableValue(getIndex());

            // Nur wenn sich der Wert geändert hat, gibt es überhaupt etwas zu tun.
            if (observable != this.currentValue) {

                // Zunächst die Bindung zum alten Wert aufheben, sofern vorhanden.
                if (this.currentValue != null) {
                    this.checkBox.selectedProperty().unbind();
                }

                // Dann den neuen Wert anbinden
                this.checkBox.selectedProperty().bind(observable);
                this.currentValue = observable;
            }
        }
    }

}
