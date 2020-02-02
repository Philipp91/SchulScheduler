package schulscheduler.solver;

import jscip.SCIP_Vartype;
import jscip.Scip;
import jscip.Solution;
import jscip.Variable;
import schulscheduler.model.eingabe.Eingabedaten;
import schulscheduler.model.ergebnis.Ergebnisdaten;
import schulscheduler.solver.binary.BinaryLP;
import schulscheduler.solver.binary.BinaryVariable;
import schulscheduler.solver.binary.Constraint;
import schulscheduler.solver.binary.ForceValue;
import schulscheduler.solver.binary.SumGeq;
import schulscheduler.solver.binary.SumLeq;
import schulscheduler.solver.binary.SumOp;
import schulscheduler.solver.binary.VarEq;
import schulscheduler.solver.binary.VarImpliesOr;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

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
        scip.maximization();
        try {
            for (BinaryVariable variable : binaryLP.getVariables()) {
                varMap.put(
                        variable,
                        scip.createVar(variable.getName(), /*lb=*/0.0, /*ub=*/1.0,
                                /*obj=*/variable.getObjectiveFactor(), SCIP_Vartype.SCIP_VARTYPE_BINARY)
                );
            }
            try {
                for (Constraint constraint : binaryLP.getConstraints()) {
                    if (constraint instanceof ForceValue) {
                        addForceValueConstraint((ForceValue) constraint);
                    } else if (constraint instanceof VarEq) {
                        addVarEqConstraint((VarEq) constraint);
                    } else if (constraint instanceof SumOp) {
                        this.addSumOpConstraint((SumOp) constraint);
                    } else if (constraint instanceof VarImpliesOr) {
                        this.addVarImpliesOrConstraint((VarImpliesOr) constraint);
                    } else {
                        throw new UnsupportedOperationException("Unsupported constraint type " + constraint.getClass().getName());
                    }
                }

                // This is the main, long blocking call.
                scip.setRealParam("limits/time", 600); // In seconds
                scip.solve();
                scip.printStatistics();

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
        addLinearConstraint(name, vars, vars.stream().mapToDouble(v -> 1.0).toArray(), lhs, rhs);
    }

    /**
     * Adds lhs <= SUM_i(vars_i * weights_i) <= rhs
     */
    private void addLinearConstraint(@Nonnull String name, Collection<BinaryVariable> vars, double[] weights,
                                     double lhs, double rhs) {
        jscip.Constraint cons = scip.createConsLinear(name,
                vars.stream().map(varMap::get).toArray(Variable[]::new), weights,
                lhs, rhs);
        scip.addCons(cons);
        scip.releaseCons(cons);
    }

    private void addForceValueConstraint(@Nonnull ForceValue constraint) {
        double value = constraint.isForcedValue() ? 1.0 : 0.0;
        addLinearConstraint(constraint.getName(), Collections.singletonList(constraint.getVariable()), value, value);
    }

    private void addVarEqConstraint(@Nonnull VarEq constraint) {
        // variable1 == variable2  <==>  0 == variable1 - variable2  <==>  0 <= variable1 - variable2 <= 0
        addLinearConstraint(constraint.getName(), Arrays.asList(constraint.getVariable1(), constraint.getVariable2()),
                new double[]{1.0, -1.0}, 0.0, 0.0);
    }

    private void addSumOpConstraint(@Nonnull SumOp constraint) {
        addLinearConstraint(
                constraint.getName(),
                constraint.getLhsVariables(),
                constraint instanceof SumLeq ? -scip.infinity() : constraint.getRhsValue(),
                constraint instanceof SumGeq ? scip.infinity() : constraint.getRhsValue()
        );
    }

    private void addVarImpliesOrConstraint(@Nonnull VarImpliesOr constraint) {
        // lhs -> OR(rhs)  <==>  lhs <= SUM(rhs)  <==>  0 <= SUM(-lhs, rhs)
        addLinearConstraint(constraint.getName(),
                Stream.concat(Stream.of(constraint.getLhsVariable()), constraint.getRhsVariables().stream()).collect(Collectors.toList()),
                DoubleStream.concat(DoubleStream.of(-1.0), constraint.getRhsVariables().stream().mapToDouble(v -> 1.0)).toArray(),
                /*lhs=*/ 0, /*rhs=*/ scip.infinity());
    }
}
