// code by jph
package ch.alpine.sophis.itp;

import ch.alpine.sophis.dv.Biinvariant;
import ch.alpine.sophis.dv.Biinvariants;
import ch.alpine.sophis.dv.Sedarim;
import ch.alpine.sophus.lie.rn.RGroup;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.api.ScalarUnaryOperator;

/** for comparison with {@link BarycentricRationalInterpolation} */
// TODO SOPHUS review entire class and document
public record BarycentricMetricInterpolation(Sedarim sedarim) implements ScalarTensorFunction {
  /** @param knots
   * @param variogram
   * @return */
  public static ScalarTensorFunction of(Tensor knots, ScalarUnaryOperator variogram) {
    Biinvariant biinvariant = Biinvariants.METRIC.ofSafe(RGroup.INSTANCE);
    return new BarycentricMetricInterpolation(biinvariant.coordinate(variogram, knots.maps(Tensors::of)));
  }

  public static ScalarTensorFunction la(Tensor knots, ScalarUnaryOperator variogram) {
    Biinvariant biinvariant = Biinvariants.METRIC.ofSafe(RGroup.INSTANCE);
    return new BarycentricMetricInterpolation(biinvariant.lagrainate(variogram, knots.maps(Tensors::of)));
  }

  @Override
  public Tensor apply(Scalar scalar) {
    return sedarim.sunder(Tensors.of(scalar));
  }
}
