package schulscheduler.ui.controls;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Ermöglicht das Setzen von {@link #cellValueFactoryProperty()} aus FXML mit dem Attribut <tt>property="xxx"</tt>.
 *
 * @param <S> Der Typ der Elemente, die pro Zeile angezeigt werden.
 * @param <T> Der Typ der Elemente, die in dieser Spalte angezeigt werden.
 */
public class BasePropertyTableColumn<S, T> extends TableColumn<S, T> {

    private final StringProperty property = new SimpleStringProperty();

    /**
     * Erstellt eine neue BaseTableColumn.
     */
    public BasePropertyTableColumn() {
        this.property.addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                setCellValueFactory(null);
            } else {
                setCellValueFactory(new PropertyValueFactory<S, T>(newValue));
            }
        });
    }

    /**
     * @return Der Name der Property (von <tt>S</tt>), an die die Spalte gebunden wird. Die Property muss den Typ
     * <tt>T</tt> haben.
     */
    public String getProperty() {
        return this.property.get();
    }

    /**
     * @param property Der Name der Property (von <tt>S</tt>), an die die Spalte gebunden wird. Die Property muss den
     * Typ <tt>T</tt> haben.
     */
    public void setProperty(String property) {
        this.property.set(property);
    }

    /**
     * @return Der Name der Property (von <tt>S</tt>), an die die Spalte gebunden wird. Die Property muss den Typ
     * <tt>T</tt> haben.
     */
    public StringProperty propertyProperty() {
        return this.property;
    }

}
