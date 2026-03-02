// code by jph
package ch.alpine.sophis.flow;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

/** integrator of time-invariant differential constraint
 * 
 * Numerical ODE Solver
 * DSolve */
@FunctionalInterface
public interface Integrator {
  /** @param flow
   * @param x
   * @param h
   * @return moves x along flow for time duration h, i.e. "x + flow(x) * h" */
  Tensor step(StateSpaceModel stateSpaceModel, Tensor x, Tensor u, Scalar h);
}
