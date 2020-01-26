package schulscheduler.model;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;
import schulscheduler.model.eingabe.Eingabedaten;
import schulscheduler.model.schule.EnumWochentag;
import schulscheduler.model.schule.Stunde;
import schulscheduler.model.schule.Zeitslot;

public class ZeitslotTest {

    @Test
    public void testZeitslotSort() {
        Eingabedaten daten = new Eingabedaten();
        Stunde stunde1 = new Stunde(1);
        Stunde stunde2 = new Stunde(2);
        daten.getStunden().addAll(stunde1, stunde2);

        Zeitslot mo1 = new Zeitslot(stunde1, EnumWochentag.MONTAG);
        Zeitslot mo2 = new Zeitslot(stunde2, EnumWochentag.MONTAG);
        Zeitslot fr1 = new Zeitslot(stunde1, EnumWochentag.FREITAG);
        Zeitslot fr2 = new Zeitslot(stunde2, EnumWochentag.FREITAG);

        daten.getZeitslots().addAll(mo2, fr2, fr1, mo1);
        daten.getZeitslots().sort(null);
        assertThat(daten.getZeitslots(), contains(mo1, mo2, fr1, fr2));
    }

}
