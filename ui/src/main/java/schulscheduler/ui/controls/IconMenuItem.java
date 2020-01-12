package schulscheduler.ui.controls;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.MenuItem;
import schulscheduler.ui.AwesomeIcon;

public class IconMenuItem extends MenuItem {

    /**
     * Das angezeigte Icon.
     */
    private final ObjectProperty<AwesomeIcon> icon = new SimpleObjectProperty<>();

    /**
     * Erstellt ein neues AttachedMenuItem.
     */
    public IconMenuItem() {
        icon.addListener(observable -> {
            if (getIcon() == null) {
                setGraphic(null);
            } else {
                if (getGraphic() instanceof Icon) {
                    ((Icon) getGraphic()).setIcon(getIcon());
                } else {
                    setGraphic(new Icon(getIcon()));
                }
            }
        });
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

}
