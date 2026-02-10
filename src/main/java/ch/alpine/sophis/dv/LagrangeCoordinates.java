// code by jph
package ch.alpine.sophis.dv;

import ch.alpine.sophus.math.AffineQ;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.jet.AppendOne;
import ch.alpine.tensor.mat.pi.LagrangeMultiplier;
import ch.alpine.tensor.nrm.NormalizeTotal;
import ch.alpine.tensor.sca.Chop;

/* package */ enum LagrangeCoordinates {
  ;
  private static final AffineQ AFFINE_Q_APPROX = new AffineQ(Chop._08);

  /** @param levers
   * @param target
   * @return */
  public static Tensor of(Tensor levers, Tensor target) {
    Tensor eqs = AppendOne.FUNCTION.slash(levers);
    int d = Unprotect.dimension1Hint(levers) + 1;
    Tensor rhs = UnitVector.of(d, d - 1);
    /* least squares is required if eqs do not have max rank, which is the case
     * when the tangent space parameterization is not 1 to 1 */
    Tensor weights = LagrangeMultiplier.id_t(eqs).solve(target, rhs);
    AFFINE_Q_APPROX.requireMember(weights); // conceptual check
    return NormalizeTotal.FUNCTION.apply(weights); // improve accuracy
  }
}
