package schulscheduler.ui.controls;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 * Eine Tabellenspalte, die ihre Zellen als {@link NumberSpinner} rendert, wenn sie editierbar ist, oder einfach nur den
 * aktuellen Wert anzeigt, wenn die Zelle nicht editierbar sein soll.
 * 
 * @param <S> Der Typ der TableView (d.h. S == TableView&lt;S&gt;)
 */
public class NumberSpinnerColumn<S> extends BasePropertyTableColumn<S, Number> {

    /**
     * Der maximal erlaubte Wert für das Feld (inklusive). Ein Wert von null bedeutet, dass es keine Begrenzung gibt.
     */
    private final ObjectProperty<Number> maxValue = new SimpleObjectProperty<>(null);

    /**
     * Der minimal erlaubte Wert für das Feld (inklusive). Ein Wert von null bedeutet, dass es keine Begrenzung gibt.
     */
    private final ObjectProperty<Number> minValue = new SimpleObjectProperty<>(null);

    /**
     * Die Schrittweite, d.h. das Intervall zur Erhöhung bzw. Verringerung des Wertes, wenn die Buttons angeklickt
     * werden.
     */
    private final ObjectProperty<Number> stepWidth = new SimpleObjectProperty<Number>(1);

    /**
     * Erstellt eine NumberSpinnerColumn.
     */
    public NumberSpinnerColumn() {
        this.setCellFactory(new Callback<TableColumn<S, Number>, TableCell<S, Number>>() {
            public TableCell<S, Number> call(TableColumn<S, Number> param) {
                NumberSpinnerCell<S> cell = new NumberSpinnerCell<S>();
                cell.maxValueProperty().bind(maxValue);
                cell.minValueProperty().bind(minValue);
                cell.stepWidthProperty().bind(stepWidth);
                return cell;
            }
        });
    }

    /**
     * Der maximal erlaubte Wert für das Feld (inklusive). Ein Wert von null bedeutet, dass es keine Begrenzung gibt.
     * 
     * @return Die maxValue-Property.
     * @see #getMaxValue()
     * @see #setMaxValue(Number)
     */
    public final ObjectProperty<Number> maxValueProperty() {
        return maxValue;
    }

    /**
     * @return Der maximal erlaubte Wert für das Feld (inklusive).
     * @see #maxValueProperty()
     */
    public final Number getMaxValue() {
        return maxValue.get();
    }

    /**
     * @param maxValue Der maximal erlaubte Wert für das Feld (inklusive).
     * @see #maxValueProperty()
     */
    public final void setMaxValue(final Number maxValue) {
        this.maxValue.set(maxValue);
    }

    /**
     * Der minimal erlaubte Wert für das Feld (inklusive). Ein Wert von null bedeutet, dass es keine Begrenzung gibt.
     * 
     * @return Die minValue-Property.
     * @see #getMinValue()
     * @see #setMinValue(Number)
     */
    public final ObjectProperty<Number> minValueProperty() {
        return minValue;
    }

    /**
     * @return Der minimal erlaubte Wert für das Feld (inklusive).
     * @see #minValueProperty()
     */
    public final Number getMinValue() {
        return minValue.get();
    }

    /**
     * @param minValue Der minimal erlaubte Wert für das Feld (inklusive).
     * @see #minValueProperty()
     */
    public final void setMinValue(final Number minValue) {
        this.minValue.set(minValue);
    }

    /**
     * Die Schrittweite, d.h. das Intervall zur Erhöhung bzw. Verringerung des Wertes, wenn die Buttons angeklickt
     * werden.
     * 
     * @return Die stepWidth-Property.
     * @see #getStepWidth()
     * @see #setStepWidth(Number)
     */
    public final ObjectProperty<Number> stepWidthProperty() {
        return stepWidth;
    }

    /**
     * @return Die Schrittweite.
     * @see #stepWidthProperty()
     */
    public final Number getStepWidth() {
        return this.stepWidth.get();
    }

    /**
     * @param stepWidth Die Schrittweite.
     * @see #stepWidthProperty()
     */
    public final void setStepWidth(final Number stepWidth) {
        this.stepWidth.setValue(stepWidth);
    }

}
