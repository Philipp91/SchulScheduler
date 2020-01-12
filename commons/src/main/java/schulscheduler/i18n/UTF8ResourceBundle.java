package schulscheduler.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * Dieser Wrapper um die Klasse ResourceBundle sorgt dafür, dass die Texte in UTF-8 geladen werden.
 */
public abstract class UTF8ResourceBundle extends ResourceBundle {

    /**
     * Steuert die Erstellung des Bundles mit UTF-8-Encoding.
     */
    private static final Control UTF8_CONTROL = new UTF8Control();

    /**
     * Lädt die zugrundeliegende .properties-Datei mit UTF-8-Encoding.
     * 
     * @param baseBundle Der Name der .properties-Datei ohne Datei-Endung.
     */
    public UTF8ResourceBundle(String baseBundle) {
        setParent(ResourceBundle.getBundle(baseBundle, UTF8_CONTROL));
    }

    @Override
    public Enumeration<String> getKeys() {
        return parent.getKeys();
    }

    @Override
    protected Object handleGetObject(String key) {
        return parent.getObject(key);
    }

    /**
     * Steuert die Erstellung des Bundles mit UTF-8-Encoding.
     */
    protected static class UTF8Control extends Control {

        /**
         * {@inheritDoc}
         */
        public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException, IOException {
            // The below code is copied from default Control#newBundle() implementation.
            // Only the PropertyResourceBundle line is changed to read the file as UTF-8.
            String bundleName = toBundleName(baseName, locale);
            String resourceName = toResourceName(bundleName, "properties");
            ResourceBundle bundle = null;
            InputStream stream = null;
            if (reload) {
                URL url = loader.getResource(resourceName);
                if (url != null) {
                    URLConnection connection = url.openConnection();
                    if (connection != null) {
                        connection.setUseCaches(false);
                        stream = connection.getInputStream();
                    }
                }
            } else {
                stream = loader.getResourceAsStream(resourceName);
            }
            if (stream != null) {
                try {
                    bundle = new PropertyResourceBundle(new InputStreamReader(stream, "UTF-8"));
                } finally {
                    stream.close();
                }
            }
            return bundle;
        }
    }

}
