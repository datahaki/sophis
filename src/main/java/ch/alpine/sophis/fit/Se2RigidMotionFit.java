// code by jph
package ch.alpine.sophis.fit;

import ch.alpine.sophus.lie.so2.ArcTan2D;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.VectorQ;

public enum Se2RigidMotionFit {
  ;
  /** @param points with dimensions N x 2
   * @param target with dimensions N x 2
   * @return */
  public static Tensor of(Tensor points, Tensor target) {
    if (Unprotect.dimension1Hint(points) == 2)
      return of(RigidMotionFit.of(points, target));
    throw new Throw(points);
  }

  /** @param points with dimensions N x 2
   * @param target with dimensions N x 2
   * @param weights with dimensions N with entries that sum up to 1
   * @return */
  public static Tensor of(Tensor points, Tensor target, Tensor weights) {
    return of(RigidMotionFit.of(points, target, weights));
  }

  /** @param rigidMotionFit
   * @return vector of length 3 */
  private static Tensor of(RigidMotionFit rigidMotionFit) {
    Tensor rotation = rigidMotionFit.rotation(); // 2 x 2
    Scalar angle = ArcTan2D.of(rotation.get(Tensor.ALL, 0));
    return VectorQ.requireLength(rigidMotionFit.translation(), 2).append(angle);
  }
}
