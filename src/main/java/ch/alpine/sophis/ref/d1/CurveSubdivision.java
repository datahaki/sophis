// code by jph
package ch.alpine.sophis.ref.d1;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorUnaryOperator;

public interface CurveSubdivision {
  /** @param tensor
   * @return one round of subdivision of closed curve defined by given tensor */
  Tensor cyclic(Tensor tensor);

  /** @param tensor
   * @return one round of subdivision of non-closed curve defined by given tensor */
  Tensor string(Tensor tensor);

  /** @param tensor
   * @param isCyclic
   * @return */
  default Tensor auto(Tensor tensor, boolean isCyclic) {
    return isCyclic ? cyclic(tensor) : string(tensor);
  }

  /** @param isCyclic
   * @return */
  default TensorUnaryOperator auto(boolean isCyclic) {
    return isCyclic //
        ? this::cyclic
        : this::string;
  }
}
