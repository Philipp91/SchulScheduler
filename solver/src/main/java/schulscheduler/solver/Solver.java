package schulscheduler.solver;

import schulscheduler.model.eingabe.Eingabedaten;
import schulscheduler.model.ergebnis.Ergebnisdaten;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A solver computes a solution to the scheduling problem, i.e. it maps Eingabedaten to Ergebnisdaten.
 * Each solver instance may only be used once.
 */
public interface Solver {

    /**
     * Computes a solution, may block for a long time until complete.
     *
     * @param eingabe Problem statement.
     * @return A solution to the problem or null if none could be found.
     * @throws InterruptedException In case the thread was interrupted.
     */
    @Nullable
    Ergebnisdaten solve(@Nonnull Eingabedaten eingabe) throws InterruptedException;

}
