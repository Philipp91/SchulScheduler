package schulscheduler.ui.controls;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * Eine Spalte, die ihre Zellen als {@link TextFieldTableCell} rendert. Da die Zellen aber nicht editierbar sind, wird
 * das Textfeld gar nie sichtbar. Stattdessen kann man den {@link StringConverter} verwenden um beispielsweise
 * Enum-Properties korrekt anzuzeigen ({@link schulscheduler.i18n.MessagesConverter}).
 *
 * @param <S> Der Typ der TableView (d.h. S == TableView&lt;S&gt;)
 * @param <T> Der Typ der anzuzeigenden Daten (vor dem eventuellen Konvertieren).
 */
public class ReadonlyTextColumn<S, T> extends BasePropertyTableColumn<S, T> {

    /**
     * Der Converter, der zur Anzeige der Daten verwendet wird.
     */
    private final ObjectProperty<StringConverter<T>> converter = new SimpleObjectProperty<>();

    /**
     * Erstellt eine ReadonlyTextColumn.
     */
    public ReadonlyTextColumn() {
        this.setCellFactory(column -> {
            TextFieldTableCell<S, T> cell = new TextFieldTableCell<>(getConverter());
            cell.setEditable(false);
            return cell;
        });
    }

    /**
     * @return Der Converter, der zur Anzeige der Daten verwendet wird.
     */
    public StringConverter<T> getConverter() {
        return this.converter.get();
    }

    /**
     * @param converter Der Converter, der zur Anzeige der Daten verwendet wird.
     */
    public void setConverter(StringConverter<T> converter) {
        this.converter.set(converter);
    }

    /**
     * @return Der Converter, der zur Anzeige der Daten verwendet wird.
     */
    public ObjectProperty<StringConverter<T>> converterProperty() {
        return this.converter;
    }

}
