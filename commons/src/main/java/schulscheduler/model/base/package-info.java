/**
 * Dieses Package definiert Basisklassen für das Datenmodell, die nicht wirklich mit Schulen/Stundenplänen zusammenhängen.
 * <p>
 * Diese Datei dient nur dazu, den {@link schulscheduler.xml.IDAdapter} für dieses Package zu registrieren.
 */
@javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters({
        @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(
                value = schulscheduler.xml.IDAdapter.class,
                type = Integer.class
        )
})
package schulscheduler.model.base;
