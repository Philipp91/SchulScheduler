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

import de.schulscheduler.i18n.Messages;
import de.schulscheduler.model.KuerzelElementBean;
import de.schulscheduler.model.NamedElementBean;
import de.schulscheduler.ui.ItemClickedEvent;
import de.schulscheduler.ui.JavaFXHelper;
import de.schulscheduler.windows.AttachedScene;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Stellt die Skinklasse für MultiSelectBoxen dar. Im Skin werden die einzelnen Komponenten erstellt und initialisiert.
 * 
 * @param <E> Der Typ, der in der MultiSelectBox dargestellt wird.
 */
public class MultiSelectBoxSkin<E extends Comparable<? super E>> extends StackPane implements Skin<MultiSelectBox<E>> {

    /**
     * Minimale Höhe der MultiSelectBox (insbesondere einer leeren Box).
     */
    private static final int MIN_HEIGHT = 26;

    /**
     * PopUp Window für die Auswahlliste.
     */
    private static final Popup POPUP = new Popup();

    /**
     * Listener für die Scene in der sich die MultiSelectBox befindet.
     */
    private static final ChangeListener<Scene> SCENE_LISTENER = new ChangeListener<Scene>() {
        @Override
        public void changed(ObservableValue<? extends Scene> observable, Scene oldValue, Scene newValue) {
            if (oldValue != null) {
                oldValue.windowProperty().removeListener(WINDOW_LISTENER);
                if (oldValue.getWindow() != null) {
                    removeListener(oldValue.getWindow());
                }
            }
            if (newValue != null) {
                newValue.windowProperty().addListener(WINDOW_LISTENER);
                if (newValue.getWindow() != null) {
                    addListener(newValue.getWindow());
                    // Falls das Fenster mit dem Popup geschlossen wird.
                    if (newValue.getWindow().getOnHiding() == null) {
                        newValue.getWindow().setOnHiding(new EventHandler<WindowEvent>() {
                            public void handle(WindowEvent event) {
                                POPUP.hide();
                            }
                        });
                    }
                    if (newValue.getWindow().getOnCloseRequest() == null) {
                        newValue.getWindow().setOnCloseRequest(new EventHandler<WindowEvent>() {
                            public void handle(WindowEvent event) {
                                POPUP.hide();
                            }
                        });
                    }
                }
            }
        }
    };

    /**
     * Listener für das Fenster in der sich die MultiSelectBox befindet.
     */
    private static final ChangeListener<Window> WINDOW_LISTENER = new ChangeListener<Window>() {
        @Override
        public void changed(ObservableValue<? extends Window> observable, Window oldValue, Window newValue) {
            if (oldValue != null) {
                oldValue.sceneProperty().removeListener(SCENE_LISTENER);
                removeListener(oldValue);
            }
            if (newValue != null) {
                newValue.sceneProperty().addListener(SCENE_LISTENER);
                if (newValue.getScene() != null) {
                    SCENE_LISTENER.changed(newValue.sceneProperty(), null, newValue.getScene());
                }
                addListener(newValue);
                if (newValue.getOnCloseRequest() == null) {
                    newValue.setOnCloseRequest(new EventHandler<WindowEvent>() {
                        public void handle(WindowEvent event) {
                            POPUP.hide();
                        }
                    });
                }
            }
        }
    };

    /**
     * Wird ausgelöst, wenn sich die Fenstergröße ändert.
     */
    private static final InvalidationListener WINDOW_SIZE_LISTENER = new InvalidationListener() {
        public void invalidated(Observable obserable) {
            POPUP.hide();
        }
    };

    /**
     * Die MultiSelectBox, die der Skin darstellen soll.
     */
    private MultiSelectBox<E> control;

    /**
     * FlowPane zur Darstellung der ausgewählten Einträge.
     */
    private final FlowPane flowPane = new FlowPane();

    /**
     * ListView mit den Einträgen.
     */
    private final ListView<E> lstEntries = new ListView<E>();

    /**
     * Suchfeld zum Filtern.
     */
    private final TextField searchField = new TextField();

    /**
     * Alle auswählbaren Einträge, das heißt alle aus {@link MultiSelectBox#itemsProperty()} abzüglich
     * {@link MultiSelectBox#selectedProperty()}.
     */
    private final ObservableList<E> selectableItems = FXCollections.observableArrayList();

    /**
     * Liste mit den Labels die in der MultiSelectBox angezeigt werden.
     */
    private final ObservableList<MultiSelectLabel<E>> labels = FXCollections.observableArrayList();

    /**
     * Der Text, der in dem Suchfeld angezeigt wird. (Wird zur Berechnung der Breite des Suchfeldes benötigt)
     */
    private final Text text = new Text();

    /**
     * Die Scene, in der die MultiSelectBox angezeigt wird.
     */
    private AttachedScene attachedScene;

    /**
     * Wird ausgelöst bei Änderungen im Filtertext.
     */
    private final ChangeListener<String> textListener = new ChangeListener<String>() {
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            searchEntries(newValue.toLowerCase());
        }
    };

    /**
     * Wird ausgelöst bei Änderungen an der Größe der MultiSelectBox.
     */
    private final ChangeListener<Number> onSizeChange = new ChangeListener<Number>() {
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            positionPopup();
            setSearchfieldLength();
        }
    };

    private final ChangeListener<Number> onFlowPaneHeightChanged = new ChangeListener<Number>() {
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            double newHeight = newValue.doubleValue();
            if (newHeight < MIN_HEIGHT) {
                newHeight = MIN_HEIGHT;
            }
            setPrefHeight(newHeight);
            setMinHeight(newHeight);
            control.setPrefHeight(newHeight);
            control.setMinHeight(newHeight);
        }
    };

    /**
     * Wird ausgelöst, wenn sich die gewählten Einträge ändern.
     */
    private final ListChangeListener<E> selectedChange = new ListChangeListener<E>() {
        public void onChanged(javafx.collections.ListChangeListener.Change<? extends E> change) {
            while (change.next()) {
                if (change.wasRemoved()) {
                    removeFlowpaneLabels(change.getRemoved());
                }
                if (change.wasAdded()) {
                    removeDeleteStyleClass();
                    addFlowpaneLabels(change.getAddedSubList());
                }
            }
            setSearchfieldLength();
        }
    };

    /**
     * Wird ausgelöst, wenn sich die zur Auswahl stehenden Einträge ändern.
     */
    private final InvalidationListener changeItems = new InvalidationListener() {
        public void invalidated(Observable observable) {
            // Aktualisiert die SubItemliste.
            setSubItems(control.getItems());
        }
    };
    
    /**
     * Wird ausgelöst, wenn sich die Anzahl an auswählbaren Items ändert.
     */
    private final InvalidationListener changeItemsSize = new InvalidationListener() {
        @Override
        public void invalidated(Observable observable) {
            // Aktualisiert die Größe der Auswahlliste, wenn sich der Inhalt ändert
            lstEntries.setPrefHeight(lstEntries.getFixedCellSize() * selectableItems.size() + 3);
        }
    };

    /**
     * Auswahlliste ausblenden, wenn Textfield defokussiert wird.
     */
    private final ChangeListener<Boolean> onSearchFieldFocusLost = new ChangeListener<Boolean>() {
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (!newValue && (!flowPane.isHover() || POPUP.isFocused())) {
                // Prüft, ob ein Klick auf das Flowpane stattfindet. Falls nein, wird die Tastatur benutzt um die
                // MultiSelectBox zu verlassen und das Menü wird ausgeblendet.
                // Falls ja, wird ein Mausklick ausgeführt. Die Aus- bzw. Einblendaktion wird durch die onTextFieldClick
                // methode behandelt.
                if (!flowPane.isPressed()) {
                    removeDeleteStyleClass();
                    POPUP.hide();
                }
            }
            setSearchfieldLength();
        }
    };

    /**
     * Wird ausgelöst, wenn der Nutzer einen Eintrag in der Auswahlliste anklickt. Dieser wird dann ausgewählt und das
     * Popup geschlossen.
     */
    private final EventHandler<MouseEvent> onListEntryClick = new EventHandler<MouseEvent>() {
        public void handle(MouseEvent event) {
            selectEntry();
        }
    };

    /**
     * Wird ausgelöst, wenn der Nutzer das Suchfeld anklickt (bzw. das Flowpane drumrum). Dann wird das Popup angezeigt
     * oder ausgeblendet.
     */
    private final EventHandler<MouseEvent> onTextFieldClick = new EventHandler<MouseEvent>() {
        public void handle(MouseEvent event) {
            searchField.requestFocus();
            searchField.end();
            removeDeleteStyleClass();
            if (POPUP.isShowing()) {
                POPUP.hide();
            } else {
                showPopup();
                if (lstEntries.getSelectionModel().isEmpty()) {
                    lstEntries.getSelectionModel().selectFirst();
                }
            }
        }
    };

    /**
     * Wird ausgelöst, wenn eine Taste gedrückt wird.
     */
    private final EventHandler<KeyEvent> onKeyPressed = new EventHandler<KeyEvent>() {
        public void handle(KeyEvent event) {
            // Überprüft, ob der Tastendruck dazu benutzt wird, um die Auswahlliste anzuzeigen.
            if (event.getSource() == searchField) {
                if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.DOWN) {
                    showPopup();
                }
                // Überprüft, ob der Tastendruck dazu benutzt wird um ein Eintrag zu löschen.
                if (!control.selectedProperty().isEmpty()) {
                    if (event.getCode() == KeyCode.BACK_SPACE && searchField.getText().isEmpty()) {
                        if (labels.get(labels.size() - 1).getStyleClass().contains("multiselect-before-delete")) {
                            deleteEntry(labels.get(labels.size() - 1).getObject());
                            // Falls es noch weitere Einträge gibt, wird der nächste markiert.
                            if (!labels.isEmpty()) {
                                labels.get(labels.size() - 1).getStyleClass().add("multiselect-before-delete");
                                searchField.setPrefWidth(0);
                            }
                        } else {
                            labels.get(labels.size() - 1).getStyleClass().add("multiselect-before-delete");
                            searchField.setPrefWidth(0);
                        }
                    } else {
                        // Falls eine andere Taste gedrückt wird, dann wird die Styleklasse entfernt.
                        if (searchField.getText().length() == 0) {
                            labels.get(labels.size() - 1).getStyleClass().remove("multiselect-before-delete");
                        }
                    }
                }
            }
            // Überprüft, ob der Tastendruck dazu benutzt wird um einen Eintrag auszuwählen.
            if (event.getSource() == lstEntries) {
                if (event.getCode() == KeyCode.ENTER) {
                    selectEntry();
                }
                // Falls der Escape button gedrückt wird, oder Pfeil nach oben während der oberste Eintrag
                // selektiert ist, wird das Popup ausgeblendet.
                if (event.getCode() == KeyCode.ESCAPE || (lstEntries.getSelectionModel().getSelectedIndex() == 0 && event.getCode() == KeyCode.UP)) {
                    POPUP.hide();
                }
            }

            // Escape immer abfangen, weil sonst eventuell ein Dialog aus Versehen geschlossen wird
            if (event.getCode() == KeyCode.ESCAPE) {
                event.consume();
            }
        }
    };

    /**
     * Leitet Fokussierungen des äußeren Controls an {@link #searchField} weiter.
     */
    private final ChangeListener<Boolean> onControlFocused = new ChangeListener<Boolean>() {
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (newValue) {
                searchField.requestFocus();
            }
        }
    };

    /**
     * Erstellt einen neuen Skin für eine gegebene MultiSelectBox.
     * 
     * @param control Die MultiSelectBox, welche durch diesen Skin initialisiert wird.
     */
    public MultiSelectBoxSkin(final MultiSelectBox<E> control) {
        this.control = control;
        addNodes();
        initialize();
    }

    /**
     * Initialisiert die Komponenten und fügt die Listener/Eventhandler hinzu.
     */
    private void initialize() {
        this.getStyleClass().add("multiselectbox-skin");

        // Initialisiert die Control.
        flowPane.heightProperty().addListener(onFlowPaneHeightChanged);
        onFlowPaneHeightChanged.changed(flowPane.heightProperty(), 0, flowPane.getHeight());
        control.heightProperty().addListener(onSizeChange);
        control.widthProperty().addListener(onSizeChange);
        control.selectedProperty().addListener(selectedChange);
        control.itemsProperty().addListener(changeItems);
        changeItems.invalidated(control.itemsProperty());
        control.setOnKeyPressed(onKeyPressed);
        control.focusedProperty().addListener(onControlFocused);
        addFlowpaneLabels(control.selectedProperty());

        // Initialisiert das Flowpane.
        flowPane.getStyleClass().add("multiselectbox-flowpane");
        flowPane.setHgap(2);
        flowPane.setVgap(1);
        flowPane.setOnMouseClicked(onTextFieldClick);

        // Initialisiert das Suchfeld.
        searchField.getStyleClass().add("multiselectbox-searchfield");
        searchField.textProperty().addListener(textListener);
        searchField.setOnMouseClicked(onTextFieldClick);
        searchField.setOnKeyPressed(onKeyPressed);
        searchField.focusedProperty().addListener(onSearchFieldFocusLost);
        searchField.promptTextProperty().bind(control.promptTextProperty());

        // Initialisiert den Text, die angezeigt wird, wenn keine Einträge auswählbar sind.
        Label placeholder = new Label(Messages.getInstance().getString("noEntries"));
        text.setText(placeholder.getText());
        lstEntries.setFixedCellSize(text.getLayoutBounds().getHeight() * 1.5);       
        lstEntries.setPlaceholder(placeholder);
        // Initialisiert die Auswahlliste.
        lstEntries.getStyleClass().add("multiselectbox-entries");
        lstEntries.setMinHeight(lstEntries.getFixedCellSize());
        lstEntries.prefWidthProperty().bind(flowPane.widthProperty().subtract(flowPane.getHgap()));
        lstEntries.setPrefHeight(lstEntries.getFixedCellSize() * selectableItems.size() + 3);
        lstEntries.setVisible(true);
        lstEntries.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        lstEntries.setOnMouseClicked(onListEntryClick);
        lstEntries.setOnKeyPressed(onKeyPressed);
        lstEntries.setCellFactory(BaseElementListCell.<E> createFactory());
        
        selectableItems.addListener(changeItemsSize);
        sceneProperty().addListener(SCENE_LISTENER);
        if (getScene() != null) {
            SCENE_LISTENER.changed(sceneProperty(), null, getScene());
        }

    }

    /**
     * Fügt die einzelnen Komponenten ihren zugeordneten Elternkomponenten hinzu.
     */
    private void addNodes() {
        flowPane.getChildren().add(searchField);
        getChildren().add(flowPane);
        if (control.getItems() == null) {
            selectableItems.clear();
        } else {
            selectableItems.setAll(control.getItems());
        }
        lstEntries.setItems(selectableItems);
    }

    /**
     * Legt die Länge des Suchfeldes fest:
     * <ul>
     * <li>Falls Text angezeigt wird, wird die Länge an den Text angepasst und um einen Puffer von 12 Pixeln vergrößert.
     * </li>
     * <li>Falls kein Text enthalten ist und das Feld einen Placeholder hat, wird es an die Länge des Placeholders
     * angepasst.</li>
     * <li>Wenn es keinen Placeholder gibt und das Feld den Fokus hat, wird es mit Breite 12 angezeigt, damit der Cursor
     * sichtbar ist.</li>
     * <li>Wenn es keinen Placeholder gibt und das Feld nicht den Fokus hat, wird es mit Breite 0 angezeigt.</li>
     * </ul>
     */
    public void setSearchfieldLength() {
        if (searchField.getText().isEmpty()) {
            if (control.selectedProperty().size() == 0 && control.getPromptText() != null) {
                text.setText(searchField.getPromptText());
                searchField.setPrefWidth(text.getLayoutBounds().getWidth() + 8);
            } else if (searchField.isFocused()) {
                searchField.setPrefWidth(8);
            } else {
                searchField.setPrefWidth(0);
            }
        } else {
            text.setText(searchField.getText());
            searchField.setPrefWidth(text.getLayoutBounds().getWidth() + 8);
        }
    }

    /**
     * Prüft auf Übereinstimmungen eines Objektes mit einem gegebenen Suchstring. Es wird geprüft, von welchem Typ die
     * Einträge sind und dementsprechend werden verschiedene Attribute abgeglichen. Falls es sich um ein
     * KuerzelElementBean handelt, werden Kuerzel und Name abgeglichen. Falls es sich um ein NamedElementBean handelt,
     * wird der Name abgeglichen. Sonstige Objekte werden mit ihrer toString() Methode abgeglichen.
     * 
     * @param obj Das Objekt, das abgeglichen werden soll.
     * @param searchText Der Text, nachdem gesucht werden soll.
     * @return true, wenn das Objekt mit dem Suchtext übereinstimmt, andernfalls false.
     */
    private boolean matches(Object obj, String searchText) {
        if (obj instanceof KuerzelElementBean) {
            KuerzelElementBean element = (KuerzelElementBean) obj;
            return matches(element.getName(), searchText) || matches(element.getKuerzel(), searchText);
        } else if (obj instanceof NamedElementBean) {
            NamedElementBean element = (NamedElementBean) obj;
            return matches(element.getName(), searchText);
        } else {
            return matches(obj.toString(), searchText);
        }
    }

    /**
     * Vergleicht 2 Strings miteinander.
     * 
     * @param value Der Wert eines Elements in Stringform, der abgeglichen werden soll.
     * @param searchText Der Suchtext, nachdem gesucht werden soll.
     * @return true, wenn der Wert des elements nicht null ist und den Suchtext enthält, andernfalls false.
     */
    private boolean matches(String value, String searchText) {
        return value != null && value.toLowerCase().contains(searchText);
    }

    /**
     * Sucht nach Übereinstimmung der Einträge mit dem Suchtext. Die Itemliste wird durchlaufen und die einzelnen
     * Einträge werden mit den Suchtext abgeglichen. Falls die Einträge mit dem Suchtext übereinstimmen, werden sie der
     * Auswahlliste hinzugefügt, andernfalls entfernt.
     * 
     * @param searchText Der Suchtext nach dem gefiltert wird.
     */
    public void searchEntries(String searchText) {
        setSearchfieldLength();
        // Überprüft, ob ein Komma gedrückt wird.
        if (searchText.endsWith(",")) {
            // Falls das Popup angezeigt wird, wird der ausgewählte Eintrag ausgeführt.
            if (POPUP.isShowing() && !selectableItems.isEmpty()) {
                selectEntry();
            } else {
                // Falls das Popup ausgeblendet ist werden Kommatastendrücke komplett ignoriert.
                searchField.textProperty().removeListener(textListener);
                searchField.setText(searchField.getText().replace(",", ""));
                searchField.textProperty().addListener(textListener);
            }
            return;
        }
        if (!POPUP.isShowing()) {
            showPopup();
        }
        // Falls es Einträge gibt für die Werte null sind, dann werden diese bei der Suche nicht angezeigt.
        // Wenn der Suchtext gelöscht wird, werden sie wieder eingefügt.
        if (searchText.isEmpty()) {
            setSubItems(control.getItems());
            FXCollections.sort(selectableItems);
        } else {
            // Iteriert durch alle Einträge.
            if (control.getItems() != null) {
                for (E obj : control.getItems()) {
                    // Sucht nach Übereinstimmung des Suchtextes.
                    if (!control.getSelected().contains(obj) && matches(obj, searchText)) {
                        // Fügt den Eintrag der Auswahlliste hinzu.
                        if (!selectableItems.contains(obj)) {
                            selectableItems.add(obj);
                        }
                    } else {
                        // Entfernt den Eintrag aus der Auswahlliste.
                        selectableItems.remove(obj);
                    }
                }
            }
            // Sortiert die Auswahlliste und zeigt sie an, wählt den ersten Eintrag aus.
            sortList(searchText);
        }
        if (!selectableItems.isEmpty() && control.getItems() != null) {
            lstEntries.getSelectionModel().selectFirst();
        }
    }

    /**
     * Sortiert die Auswahlliste, wenn der Benutzer nach einem bestimmten Eintrag filtert. Die Einträge werden nach
     * ihrer Übereinstimmung mit dem Suchtext sortiert. Die Priorität der Sortierung lautet wie folgt: 1. Komplette
     * Übereinstimmung des Elements mit dem Suchtext 2. Element beginnt mit dem Suchtext 3. Element enthält den Suchtext
     * 
     * @param searchString Der Text, nach dem gefiltert wird.
     */
    private void sortList(String searchString) {
        FXCollections.sort(selectableItems, new Comparator<E>() {
            @Override
            public int compare(E o1, E o2) {
                return Integer.compare(getPriority(searchString, o2), getPriority(searchString, o1));
            }
        });
    }

    /**
     * Prüft, von welchem Objekttyp ein Element ist und gibt die Priorität im Vergleich zu dem Suchstring zurück.
     * 
     * @param searchString Der Suchstring, nach dem gefiltert wird.
     * @param element Das Element, das abzugleichen ist.
     * @return 1, falls das Element mit dem String identisch ist, 0 falls das Element mit dem Suchstring beginnt und -1
     *         wenn der Suchstring im Element enthalten ist.
     */
    private int getPriority(String searchString, E element) {
        if (element instanceof KuerzelElementBean) {
            KuerzelElementBean newElement = (KuerzelElementBean) element;
            return Math.max(getPriority(searchString, newElement.getName()), getPriority(searchString, newElement.getKuerzel()));
        } else if (element instanceof NamedElementBean) {
            NamedElementBean newElement = (NamedElementBean) element;
            return getPriority(searchString, newElement.getName());
        } else {
            return getPriority(searchString, element.toString());
        }
    }

    /**
     * Vergleicht 2 Strings miteinander und gibt die Priorität der Übereinstimmung zurück.
     * 
     * @param searchString Der String, nach dem gesucht wird.
     * @param element Das Element in dem nach dem Suchstring gesucht wird.
     * @return 1, falls die 2 Strings identisch sind, 0 falls das element mit dem Suchstring beginnt und -1 wenn der
     *         Suchstring im element enthalten ist.
     */
    private int getPriority(String searchString, String element) {
        element = element.toLowerCase();
        if (element.equals(searchString)) {
            return 1;
        } else if (element.startsWith(searchString)) {
            return 0;
        } else {
            return -1;
        }
    }

    /**
     * Selektiert einen Eintrag in der Auswahlliste. Für den ausgewählten Eintrag wird ein neues MultiSelectLabel
     * erstellt und dem FlowPane hinzugefügt. Anschließend wird er aus der Auswahlliste entfernt um eine Neuselektierung
     * zu verhindern.
     */
    public void selectEntry() {
        E object = lstEntries.getSelectionModel().getSelectedItem();
        // Bricht ab, falls kein Eintrag ausgewählt wurde. Beispielsweise beim Anklicken der Scrollbar.
        if (object == null) {
            return;
        }

        if (attachedScene == null) {
            attachedScene = JavaFXHelper.findAttachedParent(control);
        }
        if (attachedScene == null || attachedScene.isWindowContextActive()) {
            control.selectedProperty().add(object);
        } else {
            attachedScene.activateWindowContext();
            try {
                control.selectedProperty().add(object);
            } finally {
                attachedScene.deactivateWindowContext();
            }
        }
        
        searchField.setText("");
        POPUP.hide();
        
        // Benötigt, da bei normalen Funktionsaufruf das Suchfeld nicht
        // fokusiert wird.
        Platform.runLater(new Runnable() {
            public void run() {
                searchField.requestFocus();
                searchField.end();
            }
        });
        setSearchfieldLength();
    }

    /**
     * Löscht einen Ausgewählten Eintrag aus dem Flowpane und macht den Eintrag wieder selektierbar.
     * 
     * @param entry Das Item, welches gelöscht werden soll.
     */
    public void deleteEntry(E entry) {
        if (attachedScene == null) {
            attachedScene = JavaFXHelper.findAttachedParent(control);
        }
        if (attachedScene == null || attachedScene.isWindowContextActive()) {
            control.selectedProperty().remove(entry);
        } else {
            attachedScene.activateWindowContext();
            try {
                control.selectedProperty().remove(entry);
            } finally {
                attachedScene.deactivateWindowContext();
            }
        }
        // Fügt den Eintrag wieder der Auswahlliste hinzu und sortiert sie anschließend.
        FXCollections.sort(selectableItems);

        searchField.requestFocus();
        searchField.end();
    }

    /**
     * Zeigt die Auswahlliste an.
     */
    public void showPopup() {
        POPUP.getContent().setAll(lstEntries);
        Window window = this.getScene().getWindow();
        positionPopup();
        removeDeleteStyleClass();
        // Legt die Maximalgröße der Auswahlliste fest
        lstEntries.setMaxHeight(this.getScene().getHeight() - localToScene(0, getHeight() - 1).getY() - 10);
        lstEntries.getSelectionModel().selectFirst();
        POPUP.show(window);       
        FXCollections.sort(selectableItems);
    }

    /**
     * Positioniert das Popup unter der Box.
     */
    public void positionPopup() {
        Point2D coord = localToScreen(0, getHeight() - 1);
        POPUP.setX(coord.getX());
        POPUP.setY(coord.getY());
    }

    /**
     * Löscht Labels aus der MultiSelectBox.
     * 
     * @param list Liste mit items die gelöscht werden sollen.
     */
    private void removeFlowpaneLabels(List<? extends E> list) {
        List<MultiSelectLabel<E>> delete = new ArrayList<MultiSelectLabel<E>>();
        for (E object : list) {
            for (MultiSelectLabel<E> label : labels) {
                if (label.getObject() == object) {
                    delete.add(label);
                    if (control.getItems() != null && control.getItems().contains(object)) {
                        selectableItems.add(object);
                    }
                    flowPane.getChildren().remove(label);
                }
            }
        }
        labels.removeAll(delete);
    }

    /**
     * Fügt Labels in die MultiSelectBox hinzu.
     * 
     * @param list Liste mit den Items die hinzugefügt wurden.
     */
    private void addFlowpaneLabels(List<? extends E> list) {
        for (E object : list) {
            final MultiSelectLabel<E> label = new MultiSelectLabel<E>(object);
            label.setOnDelete(new EventHandler<ItemClickedEvent<E>>() {
                public void handle(ItemClickedEvent<E> event) {
                    deleteEntry(label.getObject());
                }
            });
            labels.add(label);
            flowPane.getChildren().add(flowPane.getChildren().size() - 1, label);
            selectableItems.remove(object);
        }
    }

    /**
     * @param window Das Fenster, an den die Listener angebunden werden sollen.
     */
    private static void addListener(Window window) {
        window.xProperty().addListener(WINDOW_SIZE_LISTENER);
        window.yProperty().addListener(WINDOW_SIZE_LISTENER);
        window.widthProperty().addListener(WINDOW_SIZE_LISTENER);
        window.heightProperty().addListener(WINDOW_SIZE_LISTENER);
    }

    /**
     * @param window Das Fenster, von dem die Listener entfernt werden sollen.
     */
    private static void removeListener(Window window) {
        window.xProperty().removeListener(WINDOW_SIZE_LISTENER);
        window.yProperty().removeListener(WINDOW_SIZE_LISTENER);
        window.widthProperty().removeListener(WINDOW_SIZE_LISTENER);
        window.heightProperty().removeListener(WINDOW_SIZE_LISTENER);
    }

    @Override
    public MultiSelectBox<E> getSkinnable() {
        return control;
    }

    /**
     * Legt die Liste, mit Einträgen, die ausgewählt werden könnten fest.
     * 
     * @param list Liste mit Einträgen.
     */
    public void setSubItems(ObservableList<E> list) {
        if (list == null || list.isEmpty()) {
            selectableItems.clear();
        } else {
            selectableItems.setAll(list);
            // Entfernt die bereits selektierten Einträge damit diese nicht 2 mal ausgewählt werden können.
            selectableItems.removeAll(control.getSelected());
            FXCollections.sort(selectableItems);
        }
    }

    /**
     * Löscht die Styleclass für das zum Löschen markierten MultiSelectLabel.
     */
    private void removeDeleteStyleClass() {
        if (!labels.isEmpty()) {
            labels.get(labels.size() - 1).getStyleClass().remove("multiselect-before-delete");
        }
    }

    @Override
    public void requestFocus() {
        searchField.requestFocus();
    }

    @Override
    public Node getNode() {
        return this;
    }

    @Override
    public void dispose() {
        this.control = null;
        this.attachedScene = null;
    }

}
