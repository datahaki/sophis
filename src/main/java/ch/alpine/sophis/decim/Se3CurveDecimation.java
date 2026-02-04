// code by jph
package ch.alpine.sophis.decim;

import ch.alpine.sophus.lie.se.Se3Group;
import ch.alpine.tensor.Scalar;

public enum Se3CurveDecimation {
  ;
  /** @param epsilon
   * @return */
  public static CurveDecimation of(Scalar epsilon) {
    return CurveDecimation.of(Se3Group.INSTANCE, epsilon);
  }
}
