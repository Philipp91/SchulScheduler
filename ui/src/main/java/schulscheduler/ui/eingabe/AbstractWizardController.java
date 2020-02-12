package schulscheduler.ui.eingabe;

import javafx.fxml.FXML;
import schulscheduler.i18n.Messages;

import java.util.MissingResourceException;

/**
 * JavaFX-Controller für alle Tabs, die (am oberen Rand) den Wizard verwenden.
 */
public abstract class AbstractWizardController {

    @FXML
    InnerWizardController wizardController;

    /**
     * Wird automatisch vom Namen der Kind-Klasse abgeschnitten, um den Key in messages.properties zu finden.
     */
    private static final String AUTO_REMOVE_SUFFIX = "Controller";

    /**
     * Wird von JavaFX aufgerufen, wenn die UI initialisiert wurde. Dient zum Anbinden der Komponenten an den
     * Controller.
     */
    public void initialize() {
        updateWizardText();
        bindComponents();
    }

    /**
     * Bindet die Komponenten an den Controller und an das Datenmodell.
     */
    protected abstract void bindComponents();

    /**
     * Aktualisiert den Text, der oben im Wizard angezeigt wird. Dazu wird {@link #getWizardText()} aufgerufen.
     */
    protected void updateWizardText() {
        wizardController.setWizardText(getWizardText());
    }

    /**
     * Die Default-Implementierung ruft bspw. für "StundenController" den Key "wizard_Stunden" aus der
     * messages.properties ab.
     *
     * @return Der Text, der oben im Wizard angezeigt werden soll.
     */
    protected String getWizardText() {
        try {
            String className = getClass().getSimpleName();
            if (className.endsWith(AUTO_REMOVE_SUFFIX)) {
                className = className.substring(0, className.length() - AUTO_REMOVE_SUFFIX.length());
            }
            return Messages.getInstance().getString("wizard_" + className);
        } catch (MissingResourceException ex) {
            throw new RuntimeException("Möglicherweise sollte " + getClass() + " die Methode getWizardText() überschreiben?", ex);
        }
    }
}
