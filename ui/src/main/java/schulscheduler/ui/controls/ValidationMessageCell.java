/**
 * Copyright 2013-2014 Universitaet Stuttgart FMI SchulScheduler Team
 * Team members: Mark Aukschlat, Philipp Keck, Mathias Landwehr, Dominik Lekar,
 * Dennis Maseluk, Alexander Miller, Sebastian Pirk, Sven Schnaible
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package schulscheduler.ui.controls;

import de.schulscheduler.validation.ValidationError;
import de.schulscheduler.validation.ValidationMessage;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Übernimmt die Darstellung einer Validierungsmeldung in der Liste. Für Validierungswarnungen wird ein gelbes Dreieck
 * angezeigt, für Fehler ein rotes Aufrufezeichen. Dahinter wird der Text der Meldung angezeigt, bestehend aus einer
 * kurzen Bezeichnung des invaliden Beans und der eigentlichen Validierungsmeldung. Der Meldungstext wird zusammengebaut
 * von {@link ValidationMessage#toString()}.
 */
public class ValidationMessageCell extends ListCell<ValidationMessage<?>> {

    /**
     * Das rote Ausrufezeichen.
     */
    private static final Image ERROR_ICON = new Image("icons/error_16.png");

    /**
     * Das gelbe Dreieck.
     */
    private static final Image WARNING_ICON = new Image("icons/warning_16.png");

    /**
     * Die ImageView, die zur Anzeige des Icons verwendet wird.
     */
    private final ImageView imageView = new ImageView();

    /**
     * Das Label, das zur Anzeige des Textes verwendet wird.
     */
    private final Label label = new Label();

    /**
     * Die aktuell angezeigte Validierungsmeldung.
     */
    private ValidationMessage<?> currentMessage;

    /**
     * Erstellt eine neue ValidationMessageCell mit der Style-Class "validationmessage-cell".
     */
    public ValidationMessageCell() {
        HBox hBox = new HBox(5);
        hBox.getChildren().setAll(imageView, label);
        hBox.getStyleClass().add("validationmessage-hbox");
        this.getStyleClass().add("validationmessage-cell");
        this.setGraphic(hBox);
    }

    @Override
    protected void updateItem(ValidationMessage<?> item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            // Wenn die Zelle nur benutzt wird, um eine leere Zeile zu rendern, darf natürlich kein Icon
            // sichtbar sein.
            if (currentMessage != null) {
                label.textProperty().unbind();
                currentMessage = null;
            }
            this.setContentDisplay(ContentDisplay.TEXT_ONLY);

        } else if (currentMessage != item) {
            this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

            if (currentMessage != null) {
                label.textProperty().unbind();
            }

            currentMessage = item;

            if (currentMessage instanceof ValidationError) {
                imageView.setImage(ERROR_ICON);
            } else {
                imageView.setImage(WARNING_ICON);
            }
            label.textProperty().bind(currentMessage);
        }

    }

}
