package schulscheduler.model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class EnumTagTest {

	@Test
	public void testTagNames() {
		assertThat(EnumTag.MONTAG.getLongForm(), is(equalTo("Montag")));
	}
	
}
