package schulscheduler.solver.binary;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Eine Bedingung der Form SUM(variables) == sumValue
 */
public class SumEq extends SumOp {
    public SumEq(@Nonnull String name, @Nonnull List<BinaryVariable> lhsVariables, int rhsValue) {
        super(name, lhsVariables, rhsValue);
    }
}
