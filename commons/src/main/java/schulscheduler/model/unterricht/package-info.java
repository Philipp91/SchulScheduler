/**
 * Dieses Package definiert den Teil des Datenmodells, der spezifisch für die jeweilige Schule und das Jahr bzw. den
 * Zeitraum ist, für den der Stundenplan erstellt wird.
 * <p>
 * Diese Datei dient nur dazu, den {@link schulscheduler.xml.IDAdapter} für dieses Package zu registrieren.
 */
@javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters({
        @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(
                value = schulscheduler.xml.IDAdapter.class,
                type = Integer.class
        )
})
package schulscheduler.model.unterricht;
