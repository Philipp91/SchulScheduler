package schulscheduler.ui.controls;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

/**
 * Eine betitelte Box, die mehrere Elemente gruppiert (aka GroupBox, TitledBorder, etc.).
 */
public class GroupPane extends StackPane {

    /**
     * Das Label, das den Titel anzeigt.
     */
    private final Label title = new Label();

    /**
     * Bestimmt, ob die Box (am oberen Ende) vergrößert und verkleinert werden kann.
     */
    private final SimpleBooleanProperty resizable = new SimpleBooleanProperty();

    /**
     * Ist nur gesetzt, wenn resizable true ist.
     */
    private ResizeHandler resizeHandler;

    /**
     * Erstellt eine neue GroupPane.
     */
    public GroupPane() {
        title.getStyleClass().add("group-pane-title");
        StackPane.setAlignment(title, Pos.TOP_LEFT);
        this.getStyleClass().add("group-pane");
        this.getChildren().add(title);

        this.resizable.addListener(observable -> {
            if (resizable.get() && resizeHandler == null) {
                resizeHandler = new ResizeHandler();
            } else if (!resizable.get() && resizeHandler != null) {
                resizeHandler.destroy();
            }
        });
    }

    /**
     * @return Der Titel-Text.
     */
    public String getTitle() {
        return title.getText();
    }

    /**
     * @param title Der Titel-Text.
     */
    public void setTitle(String title) {
        this.title.setText(title);
    }

    /**
     * @return Der Titel-Text.
     */
    public StringProperty titleProperty() {
        return this.title.textProperty();
    }

    /**
     * @return Bestimmt, ob die Box (am oberen Ende) vergrößert und verkleinert werden kann.
     */
    public boolean getResizable() {
        return this.resizable.get();
    }

    /**
     * @param resizable Bestimmt, ob die Box (am oberen Ende) vergrößert und verkleinert werden kann.
     */
    public void setResizable(boolean resizable) {
        this.resizable.set(resizable);
    }

    /**
     * @return Bestimmt, ob die Box (am oberen Ende) vergrößert und verkleinert werden kann.
     */
    public BooleanProperty resizableProperty() {
        return this.resizable;
    }

    /**
     * Steuert Vergrößerungen/Verkleinerungen der Box mit der Maus durch Ziehen am oberen Rand. Während des
     * Resize-Vorgangs wird die Größe der Box durch {@link Region#minHeightProperty()} und
     * {@link Region#maxHeightProperty()} beeinflusst. Danach werden diese wiederhergestellt.
     */
    private class ResizeHandler implements EventHandler<MouseEvent> {

        private final ResizeHandle resizeHandle = new ResizeHandle();
        private double originalMinHeight;
        private double originalMaxHeight;
        private boolean dragging;
        private double startY;
        private double startHeight;

        /**
         * Initialisiert den ResizeHandler.
         */
        public ResizeHandler() {
            addEventHandler(MouseEvent.ANY, this);
            getChildren().add(0, resizeHandle);
        }

        /**
         * Deinitialisiert den ResizeHandler.
         */
        public void destroy() {
            getChildren().remove(resizeHandle);
            removeEventHandler(MouseEvent.ANY, this);
            resizeHandler = null;
        }

        @Override
        public void handle(MouseEvent event) {
            if (event.getEventType() == MouseEvent.MOUSE_ENTERED || event.getEventType() == MouseEvent.MOUSE_MOVED) {
                // Pfeil anzeigen, wenn der Nutzer über den oberen Bereich fährt.
                if (isInDraggableZone(event) || dragging) {
                    setCursor(Cursor.S_RESIZE);
                } else {
                    setCursor(Cursor.DEFAULT);
                }

            } else if (event.getEventType() == MouseEvent.MOUSE_PRESSED && isInDraggableZone(event) && !dragging) {
                // Resize starten
                dragging = true;
                originalMinHeight = getMinHeight();
                originalMaxHeight = getMaxHeight();
                startHeight = getHeight();
                setMinHeight(startHeight);
                setMaxHeight(startHeight);
                startY = event.getSceneY();

            } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED && dragging) {
                // Resize ausführen
                double newHeight = startHeight + startY - event.getSceneY();
                if (newHeight < originalMinHeight) {
                    newHeight = originalMinHeight;
                } else if (originalMaxHeight > 0 && newHeight > originalMaxHeight) {
                    newHeight = originalMaxHeight;
                } else if (newHeight < 0) {
                    newHeight = 0;
                }
                setMinHeight(newHeight);
                setMaxHeight(newHeight);

            } else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
                if (dragging) {
                    double newHeight = startHeight + startY - event.getSceneY();
                    if (newHeight < originalMinHeight) {
                        newHeight = originalMinHeight;
                    } else if (originalMaxHeight > 0 && newHeight > originalMaxHeight) {
                        newHeight = originalMaxHeight;
                    } else if (newHeight < 0) {
                        newHeight = 0;
                    }
                    setMinHeight(originalMinHeight);
                    setMaxHeight(originalMaxHeight);
                    setHeight(newHeight);
                    setPrefHeight(newHeight);
                }
                dragging = false;
                setCursor(Cursor.DEFAULT);

            } else if (event.getEventType() == MouseEvent.MOUSE_EXITED && !dragging) {
                setCursor(Cursor.DEFAULT);
            }
        }

        /**
         * @param event Das Ereignis der Mausbewegung.
         * @return True, wenn die Mausbewegung über dem Rand der Box stattfindet.
         */
        private boolean isInDraggableZone(MouseEvent event) {
            return event.getY() >= 0 && event.getY() < getPadding().getTop();
        }

    }

    /**
     * Drei graue Punkte nebeneinander, um anzuzeigen, dass das Vergrößern/Verkleinern möglich ist.
     */
    private static final class ResizeHandle extends HBox {

        /**
         * Erstellt einen neuen ResizableIndicator.
         */
        public ResizeHandle() {
            getStyleClass().add("resize-handle");
            for (int i = 0; i < 3; i++) {
                getChildren().add(new Circle(2.0, Paint.valueOf("#333333")));
            }
            setSpacing(2.5);
            setAlignment(Pos.TOP_CENTER);
            setTranslateY(-18.0);
            setOpacity(0.15);
        }

    }
}
