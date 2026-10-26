// code by jph
package ch.alpine.sophis.hull.d2;

import ch.alpine.sophis.crv.d2.Det2D;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.PackageTestAccess;

enum StaticHelper {
  ;
  /** Three points are a counter-clockwise turn if ccw > 0, clockwise if
   * ccw < 0, and co-linear if ccw = 0 because ccw is a determinant that
   * gives twice the signed area of the triangle formed by p1, p2 and p3.
   * (from Wikipedia)
   * 
   * @param p1
   * @param p2
   * @param p3
   * @return Det2D[p2 - p1, p3 - p1] */
  @PackageTestAccess
  static Scalar ccw(Tensor p1, Tensor p2, Tensor p3) {
    return Det2D.of(p2.subtract(p1), p3.subtract(p1));
  }
}
