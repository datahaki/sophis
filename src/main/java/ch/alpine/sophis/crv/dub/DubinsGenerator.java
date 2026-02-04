// code by jph
package ch.alpine.sophis.crv.dub;

import ch.alpine.sophus.lie.BasicLieIntegrator;
import ch.alpine.sophus.lie.LieDifferences;
import ch.alpine.sophus.lie.LieIntegrator;
import ch.alpine.sophus.lie.se2.Se2CoveringGroup;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Last;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.api.TensorUnaryOperator;

/** SE(2) control point utility functions */
public enum DubinsGenerator {
  ;
  private static final LieIntegrator lieIntegrator = new BasicLieIntegrator(Se2CoveringGroup.INSTANCE);

  /** @param init vector of length 3
   * @param moves matrix of the form {{vx_1, 0, omega_1}, {vx_2, 0, omega_2}, ... }
   * @return */
  public static Tensor of(Tensor init, Tensor moves) {
    Tensor tensor = Tensors.of(VectorQ.requireLength(init, 3));
    for (Tensor x : moves)
      tensor.append(lieIntegrator.spin(Last.of(tensor), x));
    return tensor;
  }

  /** @param tensor of poses in SE(2)
   * @return */
  public static Tensor project(Tensor tensor) {
    TensorUnaryOperator INSTANCE = LieDifferences.of(Se2CoveringGroup.INSTANCE);
    Tensor differences = INSTANCE.apply(tensor);
    differences.set(Scalar::zero, Tensor.ALL, 1); // project vy (side slip) to zero
    return of(tensor.get(0), differences);
  }
}
