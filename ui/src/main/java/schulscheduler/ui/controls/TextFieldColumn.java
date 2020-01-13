package schulscheduler.ui.controls;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 * Eine Tabellenspalte, die ihre Zellen als Textfelder rendert, wenn sie editierbar ist, oder einfach nur den Text
 * anzeigt, wenn die Zelle nicht editierbar sein soll.
 * 
 * @param <S> Der Typ der TableView (d.h. S == TableView&lt;S&gt;)
 */
public class TextFieldColumn<S> extends BasePropertyTableColumn<S, String> {

    /**
     * Der Text, der bei leerem Textfeld angezeigt wird.
     */
    private final StringProperty promptText = new SimpleStringProperty();

    /**
     * Erstellt eine TextFieldColumn.
     */
    public TextFieldColumn() {
        this.setCellFactory(new Callback<TableColumn<S, String>, TableCell<S, String>>() {
            public TableCell<S, String> call(TableColumn<S, String> param) {
                TextFieldCell<S> cell = new TextFieldCell<>();
                cell.promptTextProperty().bind(promptText);
                return cell;
            }
        });
    }

    /**
     * @return Der Text, der bei leerem Textfeld angezeigt wird.
     */
    public String getPromptText() {
        return this.promptText.get();
    }

    /**
     * @param promptText Der Text, der bei leerem Textfeld angezeigt wird.
     */
    public void setPromptText(String promptText) {
        this.promptText.set(promptText);
    }

    /**
     * @return Der Text, der bei leerem Textfeld angezeigt wird.
     */
    public StringProperty promptTextProperty() {
        return this.promptText;
    }

}
