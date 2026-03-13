// code by jph
package ch.alpine.sophis.dv;

import ch.alpine.sophis.api.Genesis;
import ch.alpine.sophis.var.InversePowerVariogram;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.mat.gr.InfluenceMatrix;
import ch.alpine.tensor.mat.gr.Mahalanobis;

/** target coordinate is the preferred way to evaluate
 * inverse leverage coordinates.
 * 
 * <p>References:
 * Reference:
 * "Biinvariant Generalized Barycentric Coordinates on Lie Groups"
 * by Jan Hakenberg, 2020
 * 
 * "Biinvariant Distance Vectors"
 * by Jan Hakenberg, 2020
 * 
 * @see UsanceCoordinate
 * 
 * @param variogram for instance InversePowerVariogram */
public record UsanceGenesis(ScalarUnaryOperator variogram) implements Genesis {
  public static final Genesis DEFAULT = new UsanceGenesis(InversePowerVariogram.of(2));

  @Override // from Genesis
  public Tensor origin(Tensor levers) {
    InfluenceMatrix influenceMatrix = new Mahalanobis(levers);
    return new WeightingToCoordinate(influenceMatrix).apply(influenceMatrix.leverages_sqrt().maps(variogram));
  }
}
