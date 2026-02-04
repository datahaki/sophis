// code by jph
package ch.alpine.sophis.dv;

import java.util.ArrayList;
import java.util.List;

import ch.alpine.sophus.hs.Exponential;
import ch.alpine.sophus.hs.Manifold;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Int;
import ch.alpine.tensor.mat.gr.Mahalanobis;

/** The evaluation of garden distances for a fixed set of landmarks is very efficient,
 * since the {@link Mahalanobis} form at the landmarks can be precomputed.
 * 
 * <p>Reference:
 * "Biinvariant Distance Vectors"
 * by Jan Hakenberg, 2020
 * 
 * @see HarborBiinvariant */
/* package */ class GardenDistanceVector implements Sedarim {
  private final List<Exponential> tangentSpaces;
  private final List<Mahalanobis> array;

  /** @param manifold
   * @param sequence */
  public GardenDistanceVector(Manifold manifold, Tensor sequence) {
    tangentSpaces = new ArrayList<>(sequence.length());
    array = new ArrayList<>(sequence.length());
    for (Tensor point : sequence) {
      Exponential exponential = manifold.exponential(point);
      tangentSpaces.add(exponential);
      array.add(new Mahalanobis(Tensor.of(sequence.stream().map(exponential::log))));
    }
  }

  @Override // from Sedarim
  public Tensor sunder(Tensor point) {
    Int i = new Int();
    return Tensor.of(array.stream() //
        .map(mahalanobis -> mahalanobis.norm(tangentSpaces.get(i.getAndIncrement()).log(point))));
  }
}
