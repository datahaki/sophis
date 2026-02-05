// code by jph
package ch.alpine.sophis.crv.clt;

@FunctionalInterface
public interface ClothoidIntegration {
  /** @param lagrangeQuadratic
   * @return */
  ClothoidIntegral clothoidIntegral(LagrangeQuadratic lagrangeQuadratic);
}
