// code by jph
package ch.alpine.sophis.dv;

import ch.alpine.sophus.api.Manifold;
import ch.alpine.tensor.Slash;
import ch.alpine.tensor.Tensor;

public record KrigingCoordinate(Manifold manifold, Kriging kriging, Tensor sequence) implements Sedarim {
  public static Sedarim barycentric(Manifold manifold, Sedarim sedarim, Tensor sequence) {
    return new KrigingCoordinate(manifold, Kriging.barycentric(sedarim, sequence), sequence);
  }

  @Override
  public Tensor sunder(Tensor point) {
    Tensor levers = Slash.of(sequence, manifold.tangentSpace(point).log());
    return WeightingToCoordinate.of(levers).apply(kriging.estimate(point));
  }
}
