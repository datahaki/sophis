// code by jph
package ch.alpine.sophis.crv.clt.par;

import ch.alpine.sophis.crv.clt.LagrangeQuadratic;

@FunctionalInterface
public interface ClothoidIntegration {
  /** @param lagrangeQuadratic
   * @return */
  ClothoidIntegral clothoidIntegral(LagrangeQuadratic lagrangeQuadratic);
}
