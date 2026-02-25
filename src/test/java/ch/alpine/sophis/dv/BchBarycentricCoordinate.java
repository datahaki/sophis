// code by jph
package ch.alpine.sophis.dv;

import java.io.Serializable;

import ch.alpine.sophis.api.Genesis;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorBinaryOperator;

/** @param bch non-null
 * @param genesis for instance InversePowerVariogram.of(2) */
record BchBarycentricCoordinate(TensorBinaryOperator bch, Genesis genesis) //
    implements BarycentricCoordinate, Serializable {
  @Override // from BarycentricCoordinate
  public Tensor weights(Tensor sequence, Tensor point) {
    Tensor p_inv = point.negate();
    return genesis.origin(Tensor.of(sequence.stream().map(q -> bch.apply(p_inv, q))));
  }
}
