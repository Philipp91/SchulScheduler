package schulscheduler.xml;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Wird von JAXB während der (De-)Serialisierung benutzt, um Integer zu String und umgekehrt zu konvertieren.
 *
 * @author Sven
 */
public class IDAdapter extends XmlAdapter<String, Integer> {

    @Override
    public String marshal(Integer v) {
        return Integer.toString(v);
    }

    @Override
    public Integer unmarshal(String v) {
        return Integer.parseInt(v);
    }

}
