// code by jph
package ch.alpine.sophis.dv;

import java.util.Objects;

import ch.alpine.sophus.api.Manifold;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarUnaryOperator;

/** Reference:
 * "Biinvariant Distance Vectors"
 * by Jan Hakenberg, 2020 */
/* package */ class GardenCoordinate implements Sedarim {
  private final Manifold manifold;
  private final ScalarUnaryOperator variogram;
  private final Sedarim sedarim; // distances
  private final Tensor sequence;

  /** @param manifold
   * @param variogram
   * @param sequence */
  public GardenCoordinate(Manifold manifold, ScalarUnaryOperator variogram, Tensor sequence) {
    this.manifold = manifold;
    this.variogram = Objects.requireNonNull(variogram);
    sedarim = GardenDistanceVector.of(manifold, sequence);
    this.sequence = sequence;
  }

  @Override
  public Tensor sunder(Tensor point) {
    // building influence matrix at point is warranted since the mahalanobis forms
    // exist only at sequence points
    Tensor levers = manifold.exponential(point).log().slash(sequence);
    return WeightingToCoordinate.of(levers).apply(sedarim.sunder(point).maps(variogram)); // point as input to target
  }
}
