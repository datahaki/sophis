// code by jph
package ch.alpine.sophis.math.api;

import ch.alpine.tensor.Tensor;

/** functionality used in {@code TrajectoryPlanner} to map state coordinates to
 * the coordinates that imply domain keys */
public interface CoordinateWrap {
  /** @param x
   * @return coordinate transform of x before obtaining domain key */
  Tensor represent(Tensor x);

  /** @param p
   * @param q
   * @return action to get from p to q, for instance log(p^-1.q), or q-p */
  Tensor difference(Tensor p, Tensor q);
}
