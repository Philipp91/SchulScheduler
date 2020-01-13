package schulscheduler.ui.controls;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.scene.Node;

/**
 * Eine Tabellenzelle, die einen {@link NumberSpinner} enthält und an eine {@link ObjectProperty}&lt;{@link Number}&gt;
 * gebunden werden kann.
 *
 * @param <S> Der Typ der TableView (d.h. S == TableView&lt;S&gt;)
 */
public class NumberSpinnerCell<S> extends BaseGraphicTableCell<S, Number> {

    /**
     * Der NumberSpinner, den die Zelle zur Anzeige verwendet.
     */
    private final NumberSpinner numberSpinner = new NumberSpinner(false);

    /**
     * Erstellt eine normale NumberSpinnerCell mit der Style-Class "numberspinner-cell".
     */
    public NumberSpinnerCell() {
        super("numberspinner-cell");
        forwardFocusChanges(this.numberSpinner);
    }

    @Override
    protected Node getContent() {
        return this.numberSpinner;
    }

    @Override
    protected void bind(Property<Number> property) {
        this.numberSpinner.valueProperty().bindBidirectional(property);
    }

    @Override
    protected void unbind(Property<Number> property) {
        this.numberSpinner.valueProperty().unbindBidirectional(property);
    }

    /**
     * @return Der minimal erlaubte Wert.
     */
    public Number getMinValue() {
        return this.numberSpinner.getMinValue();
    }

    /**
     * @param minValue Der minimal erlaubte Wert.
     */
    public void setMinValue(Number minValue) {
        this.numberSpinner.setMinValue(minValue);
    }

    /**
     * @return Der minimal erlaubte Wert.
     */
    public ObjectProperty<Number> minValueProperty() {
        return this.numberSpinner.minValueProperty();
    }

    /**
     * @return Der maximal erlaubte Wert.
     */
    public Number getMaxValue() {
        return this.numberSpinner.getMaxValue();
    }

    /**
     * @param maxValue Der maximal erlaubte Wert.
     */
    public void setMaxValue(Number maxValue) {
        this.numberSpinner.setMaxValue(maxValue);
    }

    /**
     * @return Der maximal erlaubte Wert.
     */
    public ObjectProperty<Number> maxValueProperty() {
        return this.numberSpinner.maxValueProperty();
    }

    /**
     * @return Die Schrittweite beim Klicken der Buttons.
     */
    public Number getStepWidth() {
        return this.numberSpinner.getStepWidth();
    }

    /**
     * @param stepWidth Die Schrittweite beim Klicken der Buttons.
     */
    public void setStepWidth(Number stepWidth) {
        this.numberSpinner.setStepWidth(stepWidth);
    }

    /**
     * @return Die Schrittweite beim Klicken der Buttons.
     */
    public ObjectProperty<Number> stepWidthProperty() {
        return this.numberSpinner.stepWidthProperty();
    }

}
