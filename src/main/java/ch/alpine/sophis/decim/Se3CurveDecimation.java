// code by jph
package ch.alpine.sophis.decim;

import ch.alpine.sophus.lie.LieGroup;
import ch.alpine.sophus.lie.se.SeNGroup;
import ch.alpine.tensor.Scalar;

public enum Se3CurveDecimation {
  ;
  private static final LieGroup LIE_GROUP = new SeNGroup(3);

  /** @param epsilon
   * @return */
  public static CurveDecimation of(Scalar epsilon) {
    return CurveDecimation.of(LIE_GROUP, epsilon);
  }
}
