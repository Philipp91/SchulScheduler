package schulscheduler.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;
import schulscheduler.model.schule.Fach;
import schulscheduler.model.unterricht.Kopplung;
import schulscheduler.model.unterricht.KopplungsFach;

public class KopplungTest {

    @Test
    public void testHartProperty() {
        Kopplung kopplung = new Kopplung();
        assertThat(kopplung.isHart(), is(false));

        Fach fach1 = new Fach();
        Fach fach2 = new Fach();
        KopplungsFach kopplungsFach1 = new KopplungsFach(fach1);
        KopplungsFach kopplungsFach2 = new KopplungsFach(fach2);
        kopplung.getFaecher().addAll(kopplungsFach1, kopplungsFach2);
        assertThat(kopplungsFach1.isHart(), is(false));
        assertThat(kopplungsFach2.isHart(), is(false));
        assertThat(kopplung.isHart(), is(false));

        fach1.setHart(true);
        assertThat(kopplungsFach1.isHart(), is(true));
        assertThat(kopplungsFach2.isHart(), is(false));
        assertThat(kopplung.isHart(), is(true));

        fach2.setHart(true);
        assertThat(kopplungsFach1.isHart(), is(true));
        assertThat(kopplungsFach2.isHart(), is(true));
        assertThat(kopplung.isHart(), is(true));

        fach1.setHart(false);
        assertThat(kopplungsFach1.isHart(), is(false));
        assertThat(kopplungsFach2.isHart(), is(true));
        assertThat(kopplung.isHart(), is(true));

        kopplung.getFaecher().remove(kopplungsFach2);
        assertThat(kopplung.isHart(), is(false));

        kopplung.getFaecher().add(kopplungsFach2);
        assertThat(kopplung.isHart(), is(true));
    }

}
