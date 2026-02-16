// code by jph
package ch.alpine.sophis.gbc.d2;

import ch.alpine.sophis.math.Genesis;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.sca.var.InversePowerVariogram;

/* package */ enum InverseNorm implements Genesis {
  INSTANCE;

  private static final ScalarUnaryOperator VARIOGRAM = InversePowerVariogram.of(1);

  @Override // from Genesis
  public Tensor origin(Tensor levers) {
    return Tensor.of(levers.stream() //
        .map(Vector2Norm::of) //
        .map(VARIOGRAM));
  }
}
