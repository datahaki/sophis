// code by jph
package ch.alpine.owl.glc.adapter;

import java.util.List;

import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.owl.math.state.StateIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;

public record DiscreteIntegrator(StateSpaceModel stateSpaceModel) implements StateIntegrator {
  @Override // from StateIntegrator
  public List<StateTime> trajectory(StateTime stateTime, Tensor u) {
    Tensor xn = stateSpaceModel.f(stateTime.state(), u);
    return List.of(new StateTime(xn, stateTime.time().add(RealScalar.ONE)));
  }
}
