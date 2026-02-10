// code by jph
package ch.alpine.sophis.dv;

import java.io.Serializable;

import ch.alpine.sophus.hs.Manifold;
import ch.alpine.sophus.math.AffineQ;
import ch.alpine.sophus.math.Genesis;
import ch.alpine.tensor.Tensor;

/** Examples:
 * <pre>
 * HsCoordinates.of(RnManifold.INSTANCE, ThreePointCoordinate.of(Barycenter.MEAN_VALUE))
 * HsCoordinates.of(SnManifold.INSTANCE, ThreePointCoordinate.of(Barycenter.MEAN_VALUE))
 * </pre> */
public record HsCoordinates(Manifold manifold, Genesis genesis) implements BarycentricCoordinate, Serializable {
  @Override // from BarycentricCoordinate
  public Tensor weights(Tensor sequence, Tensor point) {
    Tensor weights = genesis.origin(manifold.exponential(point).log().slash(sequence));
    return AffineQ.INSTANCE.requireMember(weights);
  }
}
