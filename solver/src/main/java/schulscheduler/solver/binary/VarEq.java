package schulscheduler.solver.binary;

import javax.annotation.Nonnull;

/**
 * Eine Bedingung der Form variable1 == variable2
 */
public class VarEq extends Constraint {
    private final BinaryVariable variable1;
    private final BinaryVariable variable2;

    public VarEq(@Nonnull String name, @Nonnull BinaryVariable variable1, @Nonnull BinaryVariable variable2) {
        super(name);
        this.variable1 = variable1;
        this.variable2 = variable2;
    }

    public BinaryVariable getVariable1() {
        return variable1;
    }

    public BinaryVariable getVariable2() {
        return variable2;
    }
}
