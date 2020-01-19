package schulscheduler.solver.binary;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Eine Bedingung der Form SUM(variables) <=> sumValue, also Variablen auf der einen Seite, ein fixer Wert auf der
 * anderen Seite und ein Operator dazwischen.
 */
public abstract class SumOp extends Constraint {

    private final List<BinaryVariable> lhsVariables;
    private final int sumValue;

    public SumOp(@Nonnull String name, @Nonnull List<BinaryVariable> lhsVariables, int rhsValue) {
        super(name);
        this.lhsVariables = lhsVariables;
        this.sumValue = rhsValue;
    }

    public List<BinaryVariable> getLhsVariables() {
        return lhsVariables;
    }

    public int getRhsValue() {
        return sumValue;
    }
}
