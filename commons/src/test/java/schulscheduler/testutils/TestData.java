package schulscheduler.testutils;

import schulscheduler.model.eingabe.Eingabedaten;
import schulscheduler.model.ergebnis.Ergebnisdaten;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 * Stellt Testdaten zur Verfügung.
 */
public abstract class TestData {

    private TestData() {
    }

    private static final String TESTDATASET_FILENAME = "testdataset.eingabe.schulscheduler";
    private static final String TESTRESULTS_FILENAME = "testresults.ergebnis.schulscheduler";
    private static final String SMALL_TESTDATASET_FILENAME = "testdataset_small.eingabe.schulscheduler";
    private static final String SMALL_TESTRESULTS_FILENAME = "testresults_small.ergebnis.schulscheduler";
    private final static String EMPTY_TESTDATASET_FILENAME = "testdataset_empty.eingabe.schulscheduler";
    private final static String UNLOESBAR_TESTDATASET_FILENAME = "testdataset_unloesbar.eingabe.schulscheduler";

    /**
     * Liest einen Testdatensatz ein und deserialisiert ihn.
     *
     * @param fileName Dateiname des Testdatensatzes.
     * @param expectedType Der gewünschte Typ.
     * @return Der gelesene und deserialiserte Testdatensatz.
     */
    private static <T> T readData(String fileName, Class<T> expectedType) {
        try {
            JAXBContext inContext = JAXBContext.newInstance(expectedType);
            Unmarshaller mIn = inContext.createUnmarshaller();
            return expectedType.cast(mIn.unmarshal(TestData.class.getResource(fileName)));
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gibt einen Datensatz zu Testen mit etwa folgenden Größen zurück:
     * <ul>
     * <li>10 Stunden</li>
     * <li>15 Klassen</li>
     * <li>45 Lehrer</li>
     * <li>20 Fächer</li>
     * <li>225 Zuweisungen</li>
     * <li>20 Kopplungen</li>
     * </ul>
     *
     * @return Ein Datensatz zum Testen
     */
    public static Eingabedaten readTestdataset() {
        return readData(TESTDATASET_FILENAME, Eingabedaten.class);
    }

    /**
     * Gibt einen Datensatz zu Testen mit etwa folgenden Größen zurück:
     * <ul>
     * <li>3 Stunden</li>
     * <li>3 Klassen</li>
     * <li>4 Lehrer</li>
     * <li>5 Fächer</li>
     * <li>15 Zuweisungen</li>
     * <li>4 Kopplungen</li>
     * </ul>
     *
     * @return Ein Datensatz zum Testen
     */
    public static Eingabedaten readSmallTestdataset() {
        return readData(SMALL_TESTDATASET_FILENAME, Eingabedaten.class);
    }

    /**
     * Gibt einen Datensatz zurück der, abgesehen von 10 Stunden und den entsprechenden Zeitslots, keine Daten enthält.
     *
     * @return Ein Datensatz zum Testen
     */
    public static Eingabedaten readEmptyTestdataset() {
        return readData(EMPTY_TESTDATASET_FILENAME, Eingabedaten.class);
    }

    /**
     * Gibt einen Ergebnis-Datensatz aus einer schnellen Berechnung mit den Eingabedaten {@link #readSmallTestdataset()}
     * zurück.
     *
     * @return Ein Ergebnis-Datensatz zum Testen
     */
    public static Ergebnisdaten readSmallTestresults() {
        return readData(SMALL_TESTRESULTS_FILENAME, Ergebnisdaten.class);
    }

    /**
     * Gibt einen Ergebnis-Datensatz aus einer schnellen Berechnung mit den Eingabedaten {@link #readTestdataset()}
     * zurück.
     *
     * @return Ein Ergebnis-Datensatz zum Testen
     */
    public static Ergebnisdaten readTestresults() {
        return readData(TESTRESULTS_FILENAME, Ergebnisdaten.class);
    }

    /**
     * @return Ein unlösbarer Testdatensatz.
     */
    public static Eingabedaten readUnloesbar() {
        return readData(UNLOESBAR_TESTDATASET_FILENAME, Eingabedaten.class);
    }

}
