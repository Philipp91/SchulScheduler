package schulscheduler.solver.binary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Eine binäre Variable, der im Rahmen der Problemlösung ein Wert (0 oder 1) zugewiesen wird.
 */
public class BinaryVariable {

    private final String name;
    private Boolean solution;

    BinaryVariable(@Nonnull String name) {
        this.name = name;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nullable
    public Boolean getSolution() {
        return solution;
    }

    public void setSolution(boolean solution) {
        this.solution = solution;
    }

    public boolean requireSolution() {
        if (solution == null) throw new IllegalStateException("Variable " + name + " has no solution");
        return solution;
    }
}
