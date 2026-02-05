// code by jph
package ch.alpine.sophis.crv.clt.par;

import ch.alpine.sophis.crv.clt.LagrangeQuadratic;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.sca.Chop;

/* package */ enum AnalyticClothoidPartial {
  ;
  static final Chop CHOP = Chop._10;

  /** @param lagrangeQuadratic
   * @return */
  public static ClothoidPartial of(LagrangeQuadratic lagrangeQuadratic) {
    return of( //
        lagrangeQuadratic.c(0), //
        lagrangeQuadratic.c(1), //
        lagrangeQuadratic.c(2));
  }

  /** @param c0
   * @param c1
   * @param c2
   * @return */
  public static ClothoidPartial of(Scalar c0, Scalar c1, Scalar c2) {
    if (CHOP.isZero(c2))
      return CHOP.isZero(c1) //
          ? ClothoidPartials.INSTANCE.new Degree0(c0)
          : ClothoidPartials.INSTANCE.new Degree1(c0, c1);
    return ClothoidPartials.INSTANCE.new Degree2(c0, c1, c2);
  }

  public static ClothoidPartial of(Number c0, Number c1, Number c2) {
    return of(RealScalar.of(c0), RealScalar.of(c1), RealScalar.of(c2));
  }
}
