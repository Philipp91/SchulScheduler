package schulscheduler.solver.binary;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Eine Bedingung der Form lhsVariable -> OR(rhsVariables), d.h. wenn die lhsVariable wahr ist, muss auch mindestens
 * eine der anderen wahr sein.
 */
public class VarImpliesOr extends Constraint {
    private final BinaryVariable lhsVariable;
    private final List<BinaryVariable> rhsVariables;

    public VarImpliesOr(@Nonnull String name, @Nonnull BinaryVariable lhsVariable, @Nonnull List<BinaryVariable> rhsVariables) {
        super(name);
        this.lhsVariable = lhsVariable;
        this.rhsVariables = rhsVariables;
    }

    public BinaryVariable getLhsVariable() {
        return lhsVariable;
    }

    public List<BinaryVariable> getRhsVariables() {
        return rhsVariables;
    }
}
