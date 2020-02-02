package schulscheduler.solver.binary;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Eine Bedingung der Form SUM(variables) >= sumValue
 */
public class SumGeq extends SumOp {
    public SumGeq(@Nonnull String name, @Nonnull List<BinaryVariable> lhsVariables, int sumValue) {
        super(name, lhsVariables, sumValue);
    }
}
