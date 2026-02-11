// code by jph
package ch.alpine.sophis.dv;

import ch.alpine.sophus.math.Genesis;
import ch.alpine.sophus.math.api.TensorNorm;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.nrm.NormalizeTotal;

/** ONLY FOR TESTING */
record NormWeighting(TensorNorm tensorNorm, ScalarUnaryOperator variogram) implements Genesis {
  @Override
  public Tensor origin(Tensor tensor) {
    return NormalizeTotal.FUNCTION.apply(Tensor.of(tensor.stream() //
        .map(tensorNorm::norm) //
        .map(variogram)));
  }
}
