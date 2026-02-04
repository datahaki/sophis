// code by jph
package ch.alpine.sophis.ref.d1h;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

public interface HermiteFilter {
  /** @param delta between two samples in control points
   * @param control
   * @return */
  TensorIteration string(Scalar delta, Tensor control);
}
