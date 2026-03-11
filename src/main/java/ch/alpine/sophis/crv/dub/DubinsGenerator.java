// code by jph
package ch.alpine.sophis.crv.dub;

import ch.alpine.sophus.lie.LieDifferences;
import ch.alpine.sophus.lie.se2.Se2CoveringGroup;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.FoldList;
import ch.alpine.tensor.api.TensorUnaryOperator;

/** SE(2) control point utility functions */
public enum DubinsGenerator {
  ;
  private static final TensorUnaryOperator DIFFERENCES = //
      LieDifferences.of(Se2CoveringGroup.INSTANCE);

  /** @param tensor of poses in SE(2)
   * @return */
  public static Tensor project(Tensor tensor) {
    Tensor differences = DIFFERENCES.apply(tensor);
    differences.set(Scalar::zero, Tensor.ALL, 1); // project vy (side slip) to zero
    return of(tensor.get(0), differences);
  }

  /** @param init vector of length 3
   * @param moves matrix of the form {{vx_1, 0, omega_1}, {vx_2, 0, omega_2}, ... }
   * @return */
  public static Tensor of(Tensor init, Tensor moves) {
    return FoldList.of(Se2CoveringGroup.INSTANCE::spin, init, moves);
  }
}
