// code by jph
package ch.alpine.sophis.ref.d1;

import java.io.Serializable;
import java.util.Objects;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.itp.BinaryAverage;

/** linear B-spline
 * 
 * the scheme interpolates the control points
 * 
 * Dyn/Sharon 2014 p.14 show that the contractivity factor is mu = 1/2 */
public class BSpline1CurveSubdivision extends AbstractBSpline1CurveSubdivision implements Serializable {
  private final BinaryAverage geodesicSpace;

  /** @param geodesicSpace non-null
   * @throws Exception if given geodesicSpace is null */
  public BSpline1CurveSubdivision(BinaryAverage geodesicSpace) {
    this.geodesicSpace = Objects.requireNonNull(geodesicSpace);
  }

  @Override // from AbstractBSpline1CurveSubdivision
  public final Tensor midpoint(Tensor p, Tensor q) {
    return geodesicSpace.midpoint(p, q);
  }
}
