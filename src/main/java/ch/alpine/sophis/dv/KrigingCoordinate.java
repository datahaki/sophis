// code by jph
package ch.alpine.sophis.dv;

import java.util.Objects;

import ch.alpine.sophus.hs.HsDesign;
import ch.alpine.tensor.Tensor;

public class KrigingCoordinate implements Sedarim {
  private final HsDesign hsDesign;
  private final Kriging kriging;
  private final Tensor sequence;

  /** @param sedarim
   * @param hsDesign
   * @param sequence */
  public KrigingCoordinate(HsDesign hsDesign, Sedarim sedarim, Tensor sequence) {
    this.hsDesign = Objects.requireNonNull(hsDesign);
    this.kriging = Kriging.barycentric(sedarim, sequence);
    this.sequence = sequence;
  }

  @Override
  public Tensor sunder(Tensor point) {
    return InfluenceKernel.of(hsDesign.matrix(sequence, point)).apply( //
        kriging.estimate(point));
  }
}
