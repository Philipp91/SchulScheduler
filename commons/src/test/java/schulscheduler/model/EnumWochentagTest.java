package schulscheduler.model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;
import schulscheduler.model.schule.EnumWochentag;

public class EnumWochentagTest {

    @Test
    public void testTagNames() {
        assertThat(EnumWochentag.MONTAG.getLongForm(), is(equalTo("Montag")));
    }

}
