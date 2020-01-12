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

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 * Eine Tabellenspalte, die ihre Zellen als MultiSelectBoxen rendert, wenn sie editierbar ist, oder einfach eine
 * Auflistung der Items, wenn die Zelle nicht editierbar sein soll.
 * 
 * @param <S> Der Typ der TableView (d.h. S == TableView&lt;S&gt;)
 * @param <E> Der Typ für die MultiSelectBox
 */
public class MultiSelectBoxColumn<S, E extends Comparable<? super E>> extends BasePropertyTableColumn<S, ObservableList<E>> {

    /**
     * Der Placeholder Text für die MultiSelectBox.
     */
    private final StringProperty promptText = new SimpleStringProperty();

    /**
     * Die Elemente, die in der Box zur Auswahl stehen.
     */
    private final ListProperty<E> items = new SimpleListProperty<>();

    /**
     * Erstellt eine MultiSelectBoxColumn.
     */
    public MultiSelectBoxColumn() {
        this.setCellFactory(new Callback<TableColumn<S, ObservableList<E>>, TableCell<S, ObservableList<E>>>() {
            public TableCell<S, ObservableList<E>> call(TableColumn<S, ObservableList<E>> param) {
                MultiSelectBoxCell<S, E> cell = new MultiSelectBoxCell<S, E>();
                cell.itemsProperty().bind(items);
                cell.promptTextProperty().bind(promptText);
                return cell;
            }
        });
    }

    /**
     * @return Der Placeholder Text für die MultiSelectBox.
     */
    public String getPromptText() {
        return this.promptText.get();
    }

    /**
     * @param promptText Der Placeholder Text für die MultiSelectBox.
     */
    public void setPromptText(String promptText) {
        this.promptText.set(promptText);
    }

    /**
     * @return Der Placeholder Text für die MultiSelectBox.
     */
    public StringProperty promptTextProperty() {
        return this.promptText;
    }

    /**
     * @return Die Elemente, die in der MultiSelectBox zur Auswahl stehen.
     */
    public ObservableList<E> getItems() {
        return this.items.get();
    }

    /**
     * @param items Die Elemente, die in der MultiSelectBox zur Auswahl stehen.
     */
    public void setItems(ObservableList<E> items) {
        this.items.set(items);
    }

    /**
     * @return Die Elemente, die in der MultiSelectBox zur Auswahl stehen.
     */
    public ListProperty<E> itemsProperty() {
        return this.items;
    }

}
