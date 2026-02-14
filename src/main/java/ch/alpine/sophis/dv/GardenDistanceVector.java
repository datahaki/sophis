// code by jph
package ch.alpine.sophis.dv;

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
/* package */ record GardenDistanceVector(List<Exponential> exponentials, List<Mahalanobis> array) implements Sedarim {
  /** @param manifold
   * @param sequence */
  public static GardenDistanceVector of(Manifold manifold, Tensor sequence) {
    List<Exponential> exponentials = sequence.stream().map(manifold::exponential).toList();
    return new GardenDistanceVector(exponentials, exponentials.stream() //
        .map(exponential -> exponential.vectorLog().slash(sequence)) //
        .map(Mahalanobis::new) //
        .toList());
  }

  @Override // from Sedarim
  public Tensor sunder(Tensor point) {
    Int i = new Int();
    return Tensor.of(array.stream() //
        .map(mahalanobis -> mahalanobis.norm(exponentials.get(i.getAndIncrement()).vectorLog().apply(point))));
  }
}
