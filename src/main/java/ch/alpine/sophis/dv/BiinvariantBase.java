// code by jph
package ch.alpine.sophis.dv;

import java.io.Serializable;
import java.util.Objects;

import ch.alpine.sophus.math.api.Manifold;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.nrm.NormalizeTotal;

public abstract class BiinvariantBase implements Biinvariant, Serializable {
  protected final Manifold manifold;

  protected BiinvariantBase(Manifold manifold) {
    this.manifold = Objects.requireNonNull(manifold);
  }

  @Override // from Biinvariant
  public final Manifold manifold() {
    return manifold;
  }

  @Override // from Biinvariant
  public final Sedarim var_dist(ScalarUnaryOperator variogram, Tensor sequence) {
    Sedarim sedarim = relative_distances(sequence);
    Objects.requireNonNull(variogram);
    return point -> sedarim.sunder(point).maps(variogram);
  }

  @Override // from Biinvariant
  public final Sedarim weighting(ScalarUnaryOperator variogram, Tensor sequence) {
    Sedarim sedarim = var_dist(variogram, sequence);
    return point -> NormalizeTotal.FUNCTION.apply(sedarim.sunder(point));
  }

  @Override // from Biinvariant
  public Sedarim lagrainate(ScalarUnaryOperator variogram, Tensor sequence) {
    Sedarim sedarim = weighting(variogram, sequence);
    return point -> LagrangeCoordinates.of( //
        manifold.exponential(point).vectorLog().slash(sequence), // TODO SOPHUS ALG levers are computed twice
        sedarim.sunder(point)); // target
  }
}
