package schulscheduler.i18n;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.StringConverter;

/**
 * Konvertiert einen Enum-Wert mit Hilfe der enums.properties-Datei in einen String. Der Rückweg vom String zum Enum
 * wird jedoch nicht unterstützt.
 * 
 * @param <T> Der Typ des Enums.
 */
public class MessagesConverter<T extends Enum<T>> extends StringConverter<T> {

    private final StringProperty prefix = new SimpleStringProperty();

    @Override
    public String toString(T value) {
        String key = value.name();
        if (prefix.get() != null) {
            key = prefix.get() + key;
        }
        return EnumMessages.getInstance().getString(key);
    }

    @Override
    public T fromString(String value) {
        throw new UnsupportedOperationException();
    }

    /**
     * @return Der Prefix, der jedem Enum-Namen vorangestellt wird.
     */
    public String getPrefix() {
        return this.prefix.get();
    }

    /**
     * @param prefix Der Prefix, der jedem Enum-Namen vorangestellt wird.
     */
    public void setPrefix(String prefix) {
        this.prefix.set(prefix);
    }

    /**
     * @return Der Prefix, der jedem Enum-Namen vorangestellt wird.
     */
    public StringProperty prefixProperty() {
        return this.prefix;
    }

}
