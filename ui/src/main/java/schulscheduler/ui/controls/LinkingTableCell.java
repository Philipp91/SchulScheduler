package schulscheduler.ui.controls;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;

/**
 * Eine Tabellenzelle, die zwei {@link ToggleButton}s rendert, die über den oberen bzw. unteren Rand hinausragen und mit
 * den jeweiligen benachbarten Zellen synchronisiert sind, sodass es aussieht, als läge ein Button zwischen den Zeilen.
 * Das Aktivieren des Buttons setzt den Wert der oberen Stunde auf <tt>true</tt>. Ein Button kann nicht aktiviert
 * werden, wenn einer seiner Nachbarn aktiviert ist, das heißt er ist dann gesperrt.
 * 
 * @param <S> Der Typ der TableView (d.h. S == TableView&lt;S&gt;)
 */
public class LinkingTableCell<S> extends TableCell<S, Boolean> {

    /**
     * Der Button, der über den oberen Rand hinausragt.
     */
    private final ToggleButton upperButton = new ToggleButton();

    /**
     * Der Button, der über den unteren Rand hinausragt.
     */
    private final ToggleButton lowerButton = new ToggleButton();

    /**
     * Der Container für die beiden Buttons.
     */
    private final StackPane stackPane = new StackPane();

    /**
     * Die Property der Zeile zwei über der Zeile, an die die Zelle momentan gebunden ist.
     */
    private Property<Boolean> currentPrePreviousProperty;

    /**
     * Die Property der Zeile über der Zeile, an die die Zelle momentan gebunden ist.
     */
    private Property<Boolean> currentPreviousProperty;

    /**
     * Die Property der Zeile, an die die Zelle momentan gebunden ist.
     */
    private Property<Boolean> currentProperty;

    /**
     * Die Property der Zeile unter der Zeile, an die die Zelle momentan gebunden ist.
     */
    private Property<Boolean> currentNextProperty;

    /**
     * Wird ausgelöst, wenn einer der Nachbarn des oberen Buttons geklickt wird, das heißt entweder der untere Button
     * (currentProperty) oder der Button, der noch eins weiter oben ist (currentPrePreviousProperty).
     */
    private final InvalidationListener onUpperNeighborsChanged = new InvalidationListener() {
        public void invalidated(Observable observable) {
            updateUpperButton();
        }
    };

    /**
     * Wird ausgelöst, wenn einer der Nachbarn des unteren Buttons geklickt wird, das heißt entweder der obere Button
     * (currentPreviousProperty) oder der Button, der noch eins weiter unten ist (currentNextProperty).
     */
    private final InvalidationListener onLowerNeighborsChanged = new InvalidationListener() {
        public void invalidated(Observable observable) {
            updateLowerButton();
        }
    };

    /**
     * Erstellt eine normale, LinkingTableCell mit der Style-Class "linking-cell".
     */
    public LinkingTableCell() {
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        getStyleClass().add("linking-cell");
        upperButton.getStyleClass().add("upper-button");
        lowerButton.getStyleClass().add("lower-button");

        stackPane.getChildren().addAll(upperButton, lowerButton);
        stackPane.setAlignment(Pos.CENTER);
        stackPane.setPrefHeight(0);
        upperButton.prefHeightProperty().bind(this.heightProperty());
        lowerButton.prefHeightProperty().bind(this.heightProperty());
        upperButton.translateYProperty().bind(upperButton.heightProperty().divide(2).multiply(-1).subtract(3));
        lowerButton.translateYProperty().bind(lowerButton.heightProperty().divide(2).add(3));
    }

    @Override
    protected void updateItem(Boolean item, boolean empty) {
        super.updateItem(item, empty);

        int ownIndex = getIndex();
        if (empty && (ownIndex < 0 || ownIndex >= getTableView().getItems().size())) {
            // Wenn die Zelle nur benutzt wird, um eine leere Tabellenzeile zu rendern, darf nichts sichtbar sein.
            this.setGraphic(null);
            unbindFromCurrentProperties();

        } else {

            // Die Buttons anzeigen
            this.setGraphic(stackPane);

            Property<Boolean> prePreviousProperty = getProperty(ownIndex - 2);
            Property<Boolean> previousProperty = getProperty(ownIndex - 1);
            Property<Boolean> newProperty = getProperty(ownIndex);
            Property<Boolean> nextProperty = getProperty(ownIndex + 1);

            if (newProperty != currentProperty || nextProperty != currentNextProperty || previousProperty != currentPreviousProperty || prePreviousProperty != currentPrePreviousProperty) {

                // Alte Bindungen aufheben
                unbindFromCurrentProperties();

                // Neue Properties speichern
                currentPrePreviousProperty = prePreviousProperty;
                currentPreviousProperty = previousProperty;
                currentProperty = newProperty;
                currentNextProperty = nextProperty;

                // Neue Properties anbinden.
                bindToCurrentProperties();

                // Buttons entsprechend ein-/ausblenden
                updateUpperButton();
                updateLowerButton();

            }

        }
    }

    /**
     * Bindet die Zelle an die Properties.
     */
    private void bindToCurrentProperties() {

        // Der obere Button gehört zur Property der Zeile drüber und soll nur angezeigt werden, wenn es diese gibt.
        if (currentPreviousProperty != null) {
            this.upperButton.selectedProperty().bindBidirectional(currentPreviousProperty);

            if (currentPrePreviousProperty != null) {
                currentPrePreviousProperty.addListener(onUpperNeighborsChanged);
            }
            if (currentProperty != null) {
                currentProperty.addListener(onUpperNeighborsChanged);
            }
        }

        // Der untere Button gehört zur Property der eigenen Zeile, soll aber nur angezeigt werden, wenn es noch eine
        // nächste Zeile gibt.
        if (currentNextProperty != null) {
            this.lowerButton.selectedProperty().bindBidirectional(currentProperty);

            if (currentPreviousProperty != null) {
                currentPreviousProperty.addListener(onLowerNeighborsChanged);
            }
            if (currentNextProperty != null) {
                currentNextProperty.addListener(onLowerNeighborsChanged);
            }
        }

    }

    /**
     * Aktualisiert die Sichtbarkeit des oberen Buttons.
     */
    private void updateUpperButton() {
        upperButton.setVisible(currentPreviousProperty != null && !isSet(currentProperty) && !isSet(currentPrePreviousProperty));
    }

    /**
     * Aktualisiert die Sichtbarkeit des unteren Buttons.
     */
    private void updateLowerButton() {
        lowerButton.setVisible(currentNextProperty != null && !isSet(currentPreviousProperty) && !isSet(currentNextProperty));
    }

    /**
     * Hebt die aktuellen Bindungen auf.
     */
    private void unbindFromCurrentProperties() {

        if (currentPreviousProperty != null) {
            this.upperButton.selectedProperty().unbindBidirectional(currentPreviousProperty);

            if (currentPrePreviousProperty != null) {
                currentPrePreviousProperty.removeListener(onUpperNeighborsChanged);
            }
            if (currentProperty != null) {
                currentProperty.removeListener(onUpperNeighborsChanged);
            }
        }

        if (currentNextProperty != null) {
            this.lowerButton.selectedProperty().unbindBidirectional(currentProperty);

            if (currentPreviousProperty != null) {
                currentPreviousProperty.removeListener(onLowerNeighborsChanged);
            }
            if (currentNextProperty != null) {
                currentNextProperty.removeListener(onLowerNeighborsChanged);
            }
        }
    }

    /**
     * Ruft die Property ab, die angibt, ob die Zeile <tt>index</tt> und die Zeile <tt>index+1</tt> verbunden sind.
     * 
     * @param index Index der Property.
     * @return Die Property oder null, wenn die Zeile nicht existiert.
     */
    private Property<Boolean> getProperty(int index) {
        ObservableValue<Boolean> observable = getTableColumn().getCellObservableValue(index);
        if (observable == null) {
            return null;
        } else if (!(observable instanceof Property)) {
            throw new IllegalArgumentException("A LinkingTableCell cannot be bound to " + observable);
        }
        return (Property<Boolean>) observable;
    }

    /**
     * @param property Die zu überprüfende Property.
     * @return True, wenn die property nicht <tt>null</tt> ist und ihr Wert <tt>true</tt> ist.
     */
    private boolean isSet(Property<Boolean> property) {
        return property != null && property.getValue();
    }

}
