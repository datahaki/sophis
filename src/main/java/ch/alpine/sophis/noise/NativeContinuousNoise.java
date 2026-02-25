// code by jph
package ch.alpine.sophis.noise;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.api.TensorScalarFunction;

/** interface that declares bivariate and trivariate noise functions
 * with input of double type
 * maps given tensor to a scalar noise value
 * result should depend continuously on input */
public interface NativeContinuousNoise extends TensorScalarFunction {
  /** @param x
   * @return value in the interval [-1, 1] that varies smoothly with x, y */
  double at(double x);

  /** @param x
   * @param y
   * @return value in the interval [-1, 1] that varies smoothly with x, y */
  double at(double x, double y);

  /** @param x
   * @param y
   * @param z
   * @return value in the interval [-1, 1] that varies smoothly with x, y, z */
  double at(double x, double y, double z);

  /** 4D simplex noise, better simplex rank ordering method 2012-03-09
   * 
   * @param x
   * @param y
   * @param z
   * @param w
   * @return value in the interval [-1, 1] */
  double at(double x, double y, double z, double w);

  @Override
  default Scalar apply(Tensor vector) {
    return switch (vector.length()) {
    case 1 -> DoubleScalar.of(at( //
        vector.Get(0).number().doubleValue()));
    case 2 -> DoubleScalar.of(at( //
        vector.Get(0).number().doubleValue(), //
        vector.Get(1).number().doubleValue()));
    case 3 -> DoubleScalar.of(at( //
        vector.Get(0).number().doubleValue(), //
        vector.Get(1).number().doubleValue(), //
        vector.Get(2).number().doubleValue()));
    case 4 -> DoubleScalar.of(at( //
        vector.Get(0).number().doubleValue(), //
        vector.Get(1).number().doubleValue(), //
        vector.Get(2).number().doubleValue(), //
        vector.Get(3).number().doubleValue()));
    default -> throw new Throw(vector);
    };
  }
}
