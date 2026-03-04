// code by jph
package ch.alpine.sophis.crv.d2;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.pow.Sqrt;

public enum PolygonNormalize {
  ;
  /** @param polygon
   * @param area positive
   * @return */
  public static Tensor of(Tensor polygon, Scalar area) {
    Sign.requirePositive(area);
    area = area.divide(PolygonArea.of(polygon));
    if (Sign.isNegative(area)) {
      polygon = Reverse.of(polygon);
      area = area.negate();
    }
    Scalar factor = Sqrt.FUNCTION.apply(area);
    Tensor shift = PolygonCentroid.of(polygon).negate();
    return Tensor.of(polygon.stream().map(shift::add)).multiply(factor);
  }
}
