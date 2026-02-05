// code by jph
package ch.alpine.sophis.crv.clt;

import java.io.Serializable;

import ch.alpine.sophis.crv.clt.mid.ClothoidQuadratic;
import ch.alpine.sophis.crv.clt.par.ClothoidIntegral;
import ch.alpine.sophis.crv.clt.par.ClothoidIntegration;
import ch.alpine.tensor.Tensor;

/** Reference: U. Reif slides
 * 
 * maps to SE(2) or SE(2) Covering */
public record ClothoidBuilderImpl( //
    ClothoidQuadratic clothoidQuadratic, //
    ClothoidIntegration clothoidIntegration) implements ClothoidBuilder, Serializable {
  /** @param clothoidContext
   * @return */
  public Clothoid from(ClothoidContext clothoidContext) {
    LagrangeQuadratic lagrangeQuadratic = //
        clothoidQuadratic.lagrangeQuadratic(clothoidContext.b0(), clothoidContext.b1());
    ClothoidIntegral clothoidIntegral = clothoidIntegration.clothoidIntegral(lagrangeQuadratic);
    return new ClothoidImpl( //
        clothoidContext.p(), //
        clothoidIntegral, //
        clothoidContext.diff());
  }

  @Override // from ClothoidBuilder
  public Clothoid curve(Tensor p, Tensor q) {
    return from(new ClothoidContext(p, q));
  }
}
