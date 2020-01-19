package schulscheduler.solver;

import jscip.SCIP_Vartype;
import jscip.Scip;
import jscip.Solution;
import jscip.Variable;
import schulscheduler.model.eingabe.Eingabedaten;
import schulscheduler.model.ergebnis.Ergebnisdaten;
import schulscheduler.solver.binary.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * A solver that uses SCIP based on a {@link BinaryLP} through the JSCIPOpt wrapper.
 */
public class ScipSolver implements Solver {

    final Scip scip = new Scip();
    final Map<BinaryVariable, Variable> varMap = new IdentityHashMap<>();

    public ScipSolver() {
        System.loadLibrary("jscip");
    }

    @Override
    @Nullable
    public Ergebnisdaten solve(@Nonnull Eingabedaten eingabe) {
        if (!varMap.isEmpty()) throw new IllegalStateException("There can only be one concurrent computation");
        final BinaryLP binaryLP = new BinaryLP(eingabe);
        scip.create("SchulScheduler");
        try {
            for (BinaryVariable variable : binaryLP.getVariables()) {
                varMap.put(
                        variable,
                        scip.createVar(variable.getName(), /*lb=*/0.0, /*ub=*/1.0, /*obj=*/0, SCIP_Vartype.SCIP_VARTYPE_BINARY)
                );
            }
            try {
                for (Constraint constraint : binaryLP.getConstraints()) {
                    if (constraint instanceof SumOp) {
                        this.addSumOpConstraint((SumOp) constraint);
                    } else {
                        throw new UnsupportedOperationException("Unsupported constraint type " + constraint.getClass().getName());
                    }
                }

                // This is the main, long blocking call.
                scip.solve();

                Solution solution = scip.getBestSol();
                if (solution == null) return null;
                for (var varEntry : varMap.entrySet()) {
                    varEntry.getKey().setSolution(scip.getSolVal(solution, varEntry.getValue()) > 0);
                }
                return binaryLP.createErgebnis();
            } finally {
                varMap.values().forEach(scip::releaseVar);
            }
        } finally {
            scip.free();
            varMap.clear();
        }
    }

    /**
     * Adds lhs <= SUM_i(vars_i) <= rhs
     */
    private void addLinearConstraint(@Nonnull String name, Collection<BinaryVariable> vars, double lhs, double rhs) {
        jscip.Constraint cons = scip.createConsLinear(name,
                vars.stream().map(varMap::get).toArray(Variable[]::new), vars.stream().mapToDouble(v -> 1.0).toArray(),
                lhs, rhs);
        scip.addCons(cons);
        scip.releaseCons(cons);
    }

    private void addSumOpConstraint(@Nonnull SumOp constraint) {
        addLinearConstraint(
                constraint.getName(),
                constraint.getLhsVariables(),
                constraint instanceof SumLeq ? -scip.infinity() : constraint.getRhsValue(),
                constraint.getRhsValue()
        );
    }
}
