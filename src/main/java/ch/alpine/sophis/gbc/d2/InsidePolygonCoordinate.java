// code by jph
package ch.alpine.sophis.gbc.d2;

import ch.alpine.sophis.api.Genesis;
import ch.alpine.sophis.crv.d2.alg.OriginEnclosureQ;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.ConstantArray;

/** @see InsideConvexHullCoordinate
 * 
 * @param genesis that evaluates polygon coordinates at zero (0, 0) */
public record InsidePolygonCoordinate(Genesis genesis) implements Genesis {
  @Override // from BarycentricCoordinate
  public Tensor origin(Tensor levers) {
    return OriginEnclosureQ.INSTANCE.test(levers) //
        ? genesis.origin(levers)
        : ConstantArray.of(DoubleScalar.INDETERMINATE, levers.length());
  }
}
