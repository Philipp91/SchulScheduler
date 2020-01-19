package schulscheduler.solver;

import org.junit.jupiter.api.Test;
import schulscheduler.model.eingabe.Eingabedaten;
import schulscheduler.model.ergebnis.Ergebnisdaten;
import schulscheduler.testutils.TestData;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class ScipSolverTest {
    @Test
    public void testScipSolver() {

        ScipSolver solver = new ScipSolver();
        Eingabedaten eingabe = TestData.readTestdataset();

        Ergebnisdaten ergebnis = solver.solve(eingabe);
        assertThat(ergebnis, notNullValue());
    }
}
