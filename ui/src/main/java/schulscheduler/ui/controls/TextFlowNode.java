package schulscheduler.ui.controls;

import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.text.*;

/**
 * Diese Komponente parst Text, in dem Teile durch <tt>**</tt> oder <tt>//</tt> hervorgehoben werden, z.B.<br>
 *
 * <pre>
 * Hiermit ist es möglich, Text **fett** oder //kursiv// darzustellen, oder sogar //**beides**{@literal /}/.
 * </pre>
 */
public class TextFlowNode extends TextFlow {

    private static final String STYLE_CLASS = "text-flow-node";
    private static final String BOLD_DELIMITER = "**";
    private static final String ITALIC_DELIMITER = "//";

    private static final Font DEFAULT_FONT = Font.getDefault();
    private static final Font BOLD_FONT = Font.font(DEFAULT_FONT.getFamily(), FontWeight.BOLD, FontPosture.REGULAR, DEFAULT_FONT.getSize());
    private static final Font ITALIC_FONT = Font.font(DEFAULT_FONT.getFamily(), FontWeight.NORMAL, FontPosture.ITALIC, DEFAULT_FONT.getSize());
    private static final Font BOLD_ITALIC_FONT = Font.font(DEFAULT_FONT.getFamily(), FontWeight.BOLD, FontPosture.ITALIC, DEFAULT_FONT.getSize());

    private final SimpleStringProperty text = new SimpleStringProperty(this, "text");

    /**
     * Erstellt eine neue TextFlowNode.
     */
    public TextFlowNode() {
        getStyleClass().add(STYLE_CLASS);
        InvalidationListener onTextChanged = observable -> parseText();
        text.addListener(onTextChanged);
    }

    /**
     * Aktualisiert die Anzeige anhand des aktuellen Texts.
     */
    protected void parseText() {
        getChildren().clear();
        addContents(text.get(), false, false);
    }

    /**
     * Fügt den gegebenen Inhalt hinzu. Wenn dieser die Trennzeichen {@link #BOLD_DELIMITER} und/oder
     * {@link #ITALIC_DELIMITER} enthält, wird er entsprechend aufgespaltet. Dazu ruft sich die Methode selbst rekursiv
     * auf und ändert entsprechend die Werte der Parameter <tt>bold</tt> und <tt>italic</tt>.
     *
     * @param content Der Inhalt.
     * @param bold True, wenn der Text derzeit fett gedruckt wird.
     * @param italic True, wenn der Text derzeit kursiv gedruckt wird.
     */
    private void addContents(String content, boolean bold, boolean italic) {

        int boldIndex = content.indexOf(BOLD_DELIMITER);
        int italicIndex = content.indexOf(ITALIC_DELIMITER);

        if (boldIndex == -1 && italicIndex == -1) {
            // Nichts besonderes mehr, einfach den Rest hinzufügen.
            addSingleContent(content, bold, italic);
        } else if (italicIndex == -1 || (boldIndex != -1 && boldIndex < italicIndex)) {
            // Der nächste ist der Bold-Delimiter.
            addSingleContent(content.substring(0, boldIndex), bold, italic);
            addContents(content.substring(boldIndex + BOLD_DELIMITER.length()), !bold, italic);
        } else {
            // Der nächste ist der Italic-Delimiter.
            addSingleContent(content.substring(0, italicIndex), bold, italic);
            addContents(content.substring(italicIndex + ITALIC_DELIMITER.length()), bold, !italic);
        }

    }

    /**
     * Fügt einen {@link Text} zur Anzeige des gegebenen Inhalts hinzu.
     *
     * @param content Der hinzuzufügende Textabschnitt (wird nicht weiter auf Trennzeichen untersucht).
     * @param bold Ob der Textabschnitt fett gedruckt werden soll, oder nicht.
     * @param italic Ob der Textabschnitt kursiv gedruckt werden soll, oder nicht.
     */
    private void addSingleContent(String content, boolean bold, boolean italic) {
        if (content.isEmpty()) {
            return;
        }

        Text text = new Text(content);
        if (bold) {
            if (italic) {
                text.setFont(BOLD_ITALIC_FONT);
            } else {
                text.setFont(BOLD_FONT);
            }
        } else if (italic) {
            text.setFont(ITALIC_FONT);
        } else {
            text.setFont(DEFAULT_FONT);
        }
        getChildren().add(text);
    }

    /**
     * @return Der anzuzeigende Text.
     */
    public String getText() {
        return this.text.get();
    }

    /**
     * @param text Der anzuzeigende Text.
     */
    public void setText(String text) {
        this.text.set(text);
    }

    /**
     * @return Der anzuzeigende Text.
     */
    public StringProperty textProperty() {
        return this.text;
    }

}
