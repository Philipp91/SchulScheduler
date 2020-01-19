package schulscheduler.solver.binary;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Eine Bedingung der Form SUM(variables) <= sumValue
 */
public class SumLeq extends SumOp {
    public SumLeq(@Nonnull String name, @Nonnull List<BinaryVariable> lhsVariables, int sumValue) {
        super(name, lhsVariables, sumValue);
    }
}
