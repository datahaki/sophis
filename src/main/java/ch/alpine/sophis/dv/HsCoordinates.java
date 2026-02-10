// code by jph
package ch.alpine.sophis.dv;

import java.io.Serializable;
import java.util.Objects;

import ch.alpine.sophus.hs.Manifold;
import ch.alpine.sophus.math.AffineQ;
import ch.alpine.sophus.math.Genesis;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.chq.DeterminateTensorQ;

/** Examples:
 * <pre>
 * HsCoordinates.of(RnManifold.INSTANCE, ThreePointCoordinate.of(Barycenter.MEAN_VALUE))
 * HsCoordinates.of(SnManifold.INSTANCE, ThreePointCoordinate.of(Barycenter.MEAN_VALUE))
 * </pre> */
public record HsCoordinates(Manifold manifold, Genesis genesis) implements BarycentricCoordinate, Serializable {
  /** @param hsDesign
   * @param genesis
   * @param sequence non-null
   * @return */
  public static Sedarim wrap(Manifold manifold, Genesis genesis, Tensor sequence) {
    BarycentricCoordinate barycentricCoordinate = new HsCoordinates(manifold, genesis);
    Objects.requireNonNull(sequence);
    return point -> barycentricCoordinate.weights(sequence, point);
  }

  @Override // from BarycentricCoordinate
  public Tensor weights(Tensor sequence, Tensor point) {
    Tensor weights = genesis.origin(manifold.exponential(point).log().slash(sequence));
    return DeterminateTensorQ.of(weights) //
        ? AffineQ.INSTANCE.requireMember(weights)
        : weights;
  }
}
