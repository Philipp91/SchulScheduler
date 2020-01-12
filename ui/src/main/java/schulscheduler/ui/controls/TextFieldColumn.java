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
