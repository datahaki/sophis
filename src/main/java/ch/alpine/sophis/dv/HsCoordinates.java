// code by jph
package ch.alpine.sophis.dv;

import java.io.Serializable;

import ch.alpine.sophus.hs.Manifold;
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
    // if the affineQ fails then fix in genesis but not here !!!
    // return AffineQ.INSTANCE.requireMember(genesis.origin(manifold.exponential(point).log().slash(sequence)));
    // FIXME SOPHIS Hs Coordinates is apparently misused !!! since weights should add up to one !!!
    return genesis.origin(manifold.exponential(point).log().slash(sequence));
  }
}
