// code by ynager
package ch.alpine.owl.glc.adapter;

import java.io.Serializable;
import java.util.List;

import ch.alpine.owl.glc.core.CostFunction;
import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

/** Transforms the given planner constraint to a cost function by counting
 * constraint violations. A violation incurs a predefined unit cost.
 * 
 * @param plannerConstraint
 * @param unit cost
 * @return */
public record ConstraintViolationCost(PlannerConstraint plannerConstraint, Scalar unit) implements CostFunction, Serializable {
  @Override // from CostIncrementFunction
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Tensor flow) {
    return plannerConstraint.isSatisfied(glcNode, trajectory, flow) //
        ? unit.zero()
        : unit;
  }

  @Override // from HeuristicFunction
  public Scalar minCostToGoal(Tensor x) {
    return unit.zero();
  }
}
