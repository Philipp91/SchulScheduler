package schulscheduler.ui.controls;

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
        this.setCellFactory(param -> new CheckBoxCell<>());
    }

}
