package schulscheduler.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;
import schulscheduler.model.ergebnis.Unterricht;
import schulscheduler.model.schule.Fach;
import schulscheduler.model.schule.Lehrer;
import schulscheduler.model.unterricht.Klasse;

public class ToStringTest {
    @Test
    public void testDynamicToString() {
        Unterricht unterricht = new Unterricht();
        unterricht.getKlassen().add(new Klasse("5a"));
        unterricht.getLehrer().add(new Lehrer("Meier", "Me"));
        Fach ek = new Fach("Erdkunde", "EK", false);
        unterricht.getFaecher().add(ek);
        assertThat(unterricht.toShortString(), is("Unterricht 5a-EK-Me"));

        unterricht.getFaecher().add(new Fach("Mathe", "M", false));
        assertThat(unterricht.toShortString(), is("Unterricht 5a-EK-M-Me"));

        ek.setKuerzel("EK2");
        assertThat(unterricht.toShortString(), is("Unterricht 5a-EK2-M-Me"));
    }
}
