// code by jph
package ch.alpine.sophis.decim;

import ch.alpine.sophus.lie.se2.Se2Group;
import ch.alpine.tensor.Scalar;

public enum Se2CurveDecimation {
  ;
  /** @param epsilon non-negative
   * @return */
  public static CurveDecimation of(Scalar epsilon) {
    return CurveDecimation.of(Se2Group.INSTANCE, epsilon);
  }
}
