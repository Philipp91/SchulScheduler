package schulscheduler.ui.controls;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 * Eine Tabellenspalte, die ihre Zellen als Checkboxen rendert.
 * 
 * @param <S> Der Typ der TableView (d.h. S == TableView&lt;S&gt;)
 */
public class CheckBoxColumn<S> extends BasePropertyTableColumn<S, Boolean> {

    /**
     * Erstellt eine CheckBoxColumn.
     */
    public CheckBoxColumn() {
        this.setCellFactory(new Callback<TableColumn<S, Boolean>, TableCell<S, Boolean>>() {
            public TableCell<S, Boolean> call(TableColumn<S, Boolean> param) {
                return new CheckBoxCell<S>();
            }
        });
    }

}
