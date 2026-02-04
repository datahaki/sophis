// code by ynager
package ch.alpine.owl.glc.adapter;

import java.util.Collection;
import java.util.List;

import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.Tensor;

/** combines multiple PlannerConstraints
 * 
 * @see GoalAdapter */
public record MultiConstraintAdapter(Collection<PlannerConstraint> plannerConstraints) implements PlannerConstraint {
  /** @param plannerConstraints non-null
   * @return */
  @Override // from PlannerConstraint
  public boolean isSatisfied(GlcNode glcNode, List<StateTime> trajectory, Tensor flow) {
    return plannerConstraints.stream() //
        .allMatch(constraint -> constraint.isSatisfied(glcNode, trajectory, flow));
  }
}
