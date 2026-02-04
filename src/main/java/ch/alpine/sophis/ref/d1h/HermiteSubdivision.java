// code by jph
package ch.alpine.sophis.ref.d1h;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

/** assumes uniform sampling */
public interface HermiteSubdivision {
  /** @param delta between two samples in control points
   * @param control
   * @return */
  TensorIteration string(Scalar delta, Tensor control);

  /** @param delta between two samples in control points
   * @param control
   * @return */
  TensorIteration cyclic(Scalar delta, Tensor control);
}
