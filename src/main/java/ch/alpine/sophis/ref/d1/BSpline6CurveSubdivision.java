// code by jph
package ch.alpine.sophis.ref.d1;

import ch.alpine.sophus.api.GeodesicSpace;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.Scalar;

/** cubic B-spline */
public enum BSpline6CurveSubdivision {
  ;
  private static final Scalar _5_6 = Rational.of(5, 6);
  private static final Scalar _1_22 = Rational.of(1, 22);
  private static final Scalar _11_32 = Rational.of(11, 32);

  public static CurveSubdivision of(GeodesicSpace geodesicSpace) {
    return new Dual4PointCurveSubdivision(geodesicSpace, _5_6, _1_22, _11_32);
  }
}
