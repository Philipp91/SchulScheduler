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

import de.schulscheduler.i18n.MessagesConverter;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * Eine Spalte, die ihre Zellen als {@link TextFieldTableCell} rendert. Da die Zellen aber nicht editierbar sind, wird
 * das Textfeld gar nie sichtbar. Stattdessen kann man den {@link StringConverter} verwenden um beispielsweise
 * Enum-Properties korrekt anzuzeigen ({@link MessagesConverter}).
 * 
 * @param <S> Der Typ der TableView (d.h. S == TableView&lt;S&gt;)
 * @param <T> Der Typ der anzuzeigenden Daten (vor dem eventuellen Konvertieren).
 */
public class ReadonlyTextColumn<S, T> extends BasePropertyTableColumn<S, T> {

    /**
     * Der Converter, der zur Anzeige der Daten verwendet wird.
     */
    private final ObjectProperty<StringConverter<T>> converter = new SimpleObjectProperty<>();

    /**
     * Erstellt eine ReadonlyTextColumn.
     */
    public ReadonlyTextColumn() {
        this.setCellFactory(new Callback<TableColumn<S, T>, TableCell<S, T>>() {
            public TableCell<S, T> call(TableColumn<S, T> column) {
                TextFieldTableCell<S, T> cell = new TextFieldTableCell<>(getConverter());
                cell.setEditable(false);
                return cell;
            }
        });
    }

    /**
     * @return Der Converter, der zur Anzeige der Daten verwendet wird.
     */
    public StringConverter<T> getConverter() {
        return this.converter.get();
    }

    /**
     * @param converter Der Converter, der zur Anzeige der Daten verwendet wird.
     */
    public void setConverter(StringConverter<T> converter) {
        this.converter.set(converter);
    }

    /**
     * @return Der Converter, der zur Anzeige der Daten verwendet wird.
     */
    public ObjectProperty<StringConverter<T>> converterProperty() {
        return this.converter;
    }

}
