package schulscheduler.main;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Verwaltet die Programmeinstellungen.
 */
@Singleton
public class Settings {

    /**
     * Der Dateiname für die Datei, in der die Einstellungen gespeichert werden sollen. Diese Datei wird im
     * Workspace-Ordner gespeichert.
     */
    public static final String SETTINGS_FILENAME = "settings.properties";

    /**
     * Enthält die momentanen Werte der Einstellungen, sofern bekannt.
     */
    private final Map<String, String> settingsValues = new HashMap<>();

//    @Inject
//    private WorkspaceManager workspaceManager;

    /**
     * Lädt die Werte der Einstellungen, die in der Einstellungsdatei gespeichert sind. Bereits im Programm existierende
     * Werte werden nicht gelöscht, höchstens überschrieben.
     */
    public void loadSettings() {
        File settingsFile = new File(/*workspaceManager.getWorkspaceFolder(), */SETTINGS_FILENAME);

        if (settingsFile.exists()) {

            try {
                BufferedReader in = new BufferedReader(new FileReader(settingsFile));
                String line;

                while ((line = in.readLine()) != null && line.contains("=")) {
                    String[] keyAndValue = line.split("=");
                    String key = keyAndValue[0];

                    if (keyAndValue.length == 1 || keyAndValue[1].isEmpty()) {
                        settingsValues.put(key, null);
                    } else {
                        settingsValues.put(key, keyAndValue[1]);
                    }
                }

                in.close();
            } catch (IOException e) {
                throw new RuntimeException("Error while reading settings file", e);
            }
        }
    }

    /**
     * Speichert alle vorhandenen Einstellungen in die Einstellungsdatei.
     */
    public void saveSettings() {
        File settingsFile = new File(/*workspaceManager.getWorkspaceFolder(),*/ SETTINGS_FILENAME);
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(settingsFile, false));
            for (String key : settingsValues.keySet()) {
                String value = settingsValues.get(key);
                out.write(key + "=" + (value == null ? "" : value.toString()));
                out.newLine();
            }
            out.close();
        } catch (IOException e) {
            throw new RuntimeException("Error while writing settings file", e);
        }
    }

    /**
     * @param key Der Key der gewünschten Einstellung.
     * @return Der Wert der Einstellung (kann null sein, wenn die Einstellung bewusst auf leer gesetzt wurde).
     */
    public String getSetting(String key) {
        if (settingsValues.containsKey(key)) {
            return settingsValues.get(key);
        } else {
            return getDefaultValue(key);
        }
    }

    /**
     * @param key Der Key der gewünschten Einstellung.
     * @return Der Wert der Einstellung (kann null sein, wenn die Einstellung bewusst auf leer gesetzt wurde).
     */
    public Integer getSettingAsInteger(String key) {
        String value = getSetting(key);
        if (value == null) {
            return null;
        } else {
            return Integer.parseInt(value);
        }
    }

    /**
     * @param key Der Schlüssel der Einstellung.
     * @return Der Standardwert der Einstellung.
     */
    public String getDefaultValue(String key) {
        switch (key) {
            case "serverAddress":
                return "";
            case "serverPort":
                return "11337";
            case "emailAddress":
                return "";
            default:
                throw new RuntimeException("The setting " + key + " does not exist!");
        }
    }

    /**
     * Setzt den Wert einer Einstellung.
     *
     * @param key Der Key der zu setzenden Einstellung.
     * @param value Der neue Wert für die Einstellung.
     */
    public void setSetting(String key, String value) {
        if (value != null) {
            value = value.trim();
            if (value.isEmpty()) {
                value = null;
            }
        }
        settingsValues.put(key, value);
    }

    /**
     * Setzt den Wert einer Einstellung.
     *
     * @param key Der Key der zu setzenden Einstellung.
     * @param value Der neue Wert für die Einstellung.
     */
    public void setSetting(String key, Integer value) {
        setSetting(key, value == null ? null : Integer.toString(value));
    }

}
