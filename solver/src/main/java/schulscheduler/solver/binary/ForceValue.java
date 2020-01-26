package schulscheduler.solver.binary;

import javax.annotation.Nonnull;

/**
 * Eine Bedingung der Form variable = forcedValue
 */
public class ForceValue extends Constraint {
    private final BinaryVariable variable;
    private final boolean forcedValue;

    public ForceValue(@Nonnull String name, BinaryVariable variable, boolean forcedValue) {
        super(name);
        this.variable = variable;
        this.forcedValue = forcedValue;
    }

    public BinaryVariable getVariable() {
        return variable;
    }

    public boolean isForcedValue() {
        return forcedValue;
    }
}
