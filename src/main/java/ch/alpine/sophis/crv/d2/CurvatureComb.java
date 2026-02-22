// code by jph
package ch.alpine.sophis.crv.d2;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.red.Times;

/** .
 * G0 - Position, tangent of curve is not continuous, example: polygons
 * G1 - Tangent, curvature is discontinuous, example: Dubins path
 * G2 - Curvature, curvature is continuous but not regular, cubic B-spline
 * G3 - Curvature is regular
 *
 * source:
 * <a href="http://www.aliasworkbench.com/theoryBuilders/images/CombPlot4.jpg">CombPlot4</a>
 *
 * @see Curvature2D */
public enum CurvatureComb {
  ;
  /** @param tensor with dimensions n x 2 with points of curve
   * @param scaling
   * @param cyclic
   * @return tensor + normal * curvature * scaling */
  public static Tensor of(Tensor tensor, Scalar scaling, boolean cyclic) {
    if (Tensors.isEmpty(tensor))
      return Tensors.empty();
    Tensor normal = Times.of(Curvature2D.INSTANCE.auto(tensor, cyclic), Normal2D.INSTANCE.auto(tensor, cyclic));
    return tensor.add(normal.multiply(scaling));
  }
}
