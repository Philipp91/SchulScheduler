package schulscheduler.solver.binary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Eine binäre Variable, der im Rahmen der Problemlösung ein Wert (0 oder 1) zugewiesen wird.
 */
public class BinaryVariable {

    /**
     * Name der Variablen, zum Debuggen.
     */
    private final String name;

    /**
     * Multiplikator der Variablen in der Zielfunktion. Ein höherer Wert bedeutet, dass die Variable bevorzugt auf 1
     * gesetzt wird.
     */
    private double objectiveFactor = 0.0;

    /**
     * Der zugewiesene Wert in der Problemlösung. Bleibt null solange das Problem nicht gelöst ist.
     */
    private Boolean solution;

    BinaryVariable(@Nonnull String name) {
        this.name = name;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    public double getObjectiveFactor() {
        return objectiveFactor;
    }

    public void addObjectiveFactor(double add) {
        objectiveFactor += add;
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
