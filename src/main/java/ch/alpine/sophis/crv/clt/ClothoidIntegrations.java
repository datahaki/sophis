// code by jph
package ch.alpine.sophis.crv.clt;

public enum ClothoidIntegrations implements ClothoidIntegration {
  /** slower but more precise */
  ANALYTIC {
    @Override
    public ClothoidIntegral clothoidIntegral(LagrangeQuadratic lagrangeQuadratic) {
      return new ClothoidIntegralAnalytic(lagrangeQuadratic);
    }
  },
  LEGENDRE {
    @Override
    public ClothoidIntegral clothoidIntegral(LagrangeQuadratic lagrangeQuadratic) {
      return new ClothoidIntegralLegendre(lagrangeQuadratic);
    }
  };
}
