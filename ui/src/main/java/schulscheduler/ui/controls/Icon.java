package schulscheduler.ui.controls;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import schulscheduler.ui.AwesomeIcon;

/**
 * Zeigt ein Icon an. Es stehen die folgenden zur Auswahl: {@link AwesomeIcon}. Beispiel:<br>
 *
 * <pre>
 * &lt;Icon icon="GLASS" /&gt;
 * </pre>
 */
public class Icon extends Label {

    /**
     * Die Schriftart zur Anzeige der Icons.
     */
    private static final Font AWESOME_FONT = Font.loadFont(Icon.class.getResourceAsStream("/fonts/fontawesome.ttf"), 14);

    /**
     * Das angezeigte Icon.
     */
    private final ObjectProperty<AwesomeIcon> icon = new SimpleObjectProperty<>();

    /**
     * Die Größe des Icons (Standardwert ist Schriftgröße 14).
     */
    private final DoubleProperty size = new SimpleDoubleProperty(14);

    /**
     * Erstellt ein neues Icon.
     */
    public Icon() {
        getStyleClass().add("icon");
        setFont(AWESOME_FONT);
        icon.addListener(observable -> setText(getIcon() == null ? null : Character.toString(getIcon().getChar())));
        size.addListener(observable -> setFont(getSize() == 14 ? AWESOME_FONT : Font.loadFont(Icon.class.getResourceAsStream("/fonts/fontawesome.ttf"), getSize())));
    }

    /**
     * Erstellt ein neues Icon.
     *
     * @param icon Das anzuzeigende Icon.
     */
    public Icon(AwesomeIcon icon) {
        this();
        setIcon(icon);
    }

    /**
     * @return Das angezeigte Icon.
     */
    public AwesomeIcon getIcon() {
        return this.icon.get();
    }

    /**
     * @param icon Das angezeigte Icon.
     */
    public void setIcon(AwesomeIcon icon) {
        this.icon.set(icon);
    }

    /**
     * @return Das angezeigte Icon.
     */
    public ObjectProperty<AwesomeIcon> iconProperty() {
        return this.icon;
    }

    /**
     * @return Die Größe des Icons.
     */
    public double getSize() {
        return this.size.get();
    }

    /**
     * @param size Die Größe des Icons.
     */
    public void setSize(double size) {
        this.size.set(size);
    }

    /**
     * @return Die Größe des Icons.
     */
    public DoubleProperty sizeProperty() {
        return this.size;
    }

}
