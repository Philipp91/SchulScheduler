package schulscheduler.ui.controls;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import schulscheduler.model.base.BaseElement;

/**
 * Eine TableCell-Implementierung, die ihren Zelleninhalt an die {@link BaseElement#toShortStringProperty()} bindet, wenn
 * es sich um einen {@link BaseElement} handelt - und ansonsten normal {@link #toString()} verwendet.
 *
 * @param <S> Der Typ der Elemente in der zugehörigen TableView.
 * @param <T> Der Typ der Elemente, die in der Zelle angezeigt werden.
 */
public class BaseElementTableCell<S, T> extends TableCell<S, T> {

    /**
     * @param <S> Der Typ der Elemente in der zugehörigen TableView.
     * @param <T> Der Typ der Elemente, die in der Zelle angezeigt werden.
     * @return Eine Factory für BeanTableCell.
     */
    public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> createFactory() {
        return column -> new BaseElementTableCell<S, T>();
    }

    /**
     * Das aktuell angezeigte Element.
     */
    private T currentItem;

    /**
     * Erstellt eine neue BeanTableCell.
     */
    public BaseElementTableCell() {
        setContentDisplay(ContentDisplay.TEXT_ONLY);
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        if (item != currentItem) {
            if (currentItem != null) {
                textProperty().unbind();
                currentItem = null;
            }

            currentItem = item;

            if (currentItem == null) {
                setText(null);
            } else if (currentItem instanceof BaseElement) {
                textProperty().bind(((BaseElement) currentItem).toShortStringProperty());
            } else {
                setText(currentItem.toString());
            }
        } else if (currentItem != null && !(currentItem instanceof BaseElement)) {
            setText(currentItem.toString());
        }

    }

}
