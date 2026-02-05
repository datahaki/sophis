// code by jph
package ch.alpine.sophis.dv;

import ch.alpine.sophus.hs.Manifold;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.SymmetricMatrixQ;
import ch.alpine.tensor.mat.re.Inverse;

public class InverseCoordinate implements Sedarim {
  private final Manifold manifold;
  private final Sedarim sedarim;
  private final Tensor weights;
  private final Tensor sequence;

  /** @param hsDesign
   * @param sedarim
   * @param sequence */
  public InverseCoordinate(Manifold manifold, Sedarim sedarim, Tensor sequence) {
    this.manifold = manifold;
    this.sedarim = sedarim;
    Tensor vardst = SymmetricMatrixQ.INSTANCE.requireMember(sedarim.sunder().slash(sequence));
    weights = Inverse.of(vardst);
    this.sequence = sequence;
  }

  @Override
  public Tensor sunder(Tensor point) {
    Tensor design = manifold.exponential(point).log().slash(sequence);
    return InfluenceKernel.of(design).apply( //
        sedarim.sunder(point).dot(weights));
  }
}
