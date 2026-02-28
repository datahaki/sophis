// code by jph
package ch.alpine.sophis.dv;

import ch.alpine.sophus.api.Manifold;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.io.MathematicaFormat;

/** bi-invariant
 * results in a symmetric distance matrix -> can use for kriging */
/* package */ class GardenBiinvariant extends BiinvariantBase {
  public GardenBiinvariant(Manifold manifold) {
    super(manifold);
  }

  @Override // from Biinvariant
  public Sedarim relative_distances(Tensor sequence) {
    return GardenDistanceVector.of(manifold(), sequence);
  }

  @Override // from Biinvariant
  public Sedarim coordinate(ScalarUnaryOperator variogram, Tensor sequence) {
    return new GardenCoordinate(manifold(), variogram, sequence);
  }

  @Override
  public String toString() {
    return MathematicaFormat.concise("Garden", manifold());
  }
}
