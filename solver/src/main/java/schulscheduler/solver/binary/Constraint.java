package schulscheduler.solver.binary;

import javax.annotation.Nonnull;

/**
 * Eine harte Bedingung formuliert über den binären Variablen, die jede Lösung einhalten muss.
 */
public abstract class Constraint {

    /**
     * A description for this constraint.
     */
    private final String name;

    protected Constraint(@Nonnull String name) {
        this.name = name;
    }

    @Nonnull
    public String getName() {
        return name;
    }
}
