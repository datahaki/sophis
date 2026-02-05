// code by jph
package ch.alpine.sophis.dv;

import java.util.Objects;

import ch.alpine.sophus.hs.Manifold;
import ch.alpine.tensor.Tensor;

public class KrigingCoordinate implements Sedarim {
  private final Manifold manifold;
  private final Kriging kriging;
  private final Tensor sequence;

  /** @param sedarim
   * @param hsDesign
   * @param sequence */
  public KrigingCoordinate(Manifold manifold, Sedarim sedarim, Tensor sequence) {
    this.manifold = Objects.requireNonNull(manifold);
    this.kriging = Kriging.barycentric(sedarim, sequence);
    this.sequence = sequence;
  }

  @Override
  public Tensor sunder(Tensor point) {
    Tensor design = manifold.exponential(point).log().slash(sequence);
    return InfluenceKernel.of(design).apply( //
        kriging.estimate(point));
  }
}
