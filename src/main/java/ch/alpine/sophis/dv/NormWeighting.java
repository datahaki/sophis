// code by jph
package ch.alpine.sophis.dv;

import java.util.Objects;

import ch.alpine.sophus.math.Genesis;
import ch.alpine.sophus.math.api.TensorNorm;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.nrm.NormalizeTotal;

/** ONLY FOR TESTING */
public record NormWeighting(TensorNorm tensorNorm, ScalarUnaryOperator variogram) implements Genesis {
  /** @param tensorNorm non-null
   * @param */
  public static Genesis of(TensorNorm tensorNorm, ScalarUnaryOperator variogram) {
    return new NormWeighting(Objects.requireNonNull(tensorNorm), Objects.requireNonNull(variogram));
  }

  @Override
  public Tensor origin(Tensor tensor) {
    return NormalizeTotal.FUNCTION.apply(Tensor.of(tensor.stream() //
        .map(tensorNorm::norm) //
        .map(variogram)));
  }
}
