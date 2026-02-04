// code by jph
package ch.alpine.sophis.dv;

import java.io.Serializable;

import ch.alpine.tensor.Tensor;

@FunctionalInterface
public interface Sedarim extends Serializable {
  /** @param point
   * @return vector of coefficients that indicates how the given point
   * relates to a discrete set of points */
  Tensor sunder(Tensor point);
}
