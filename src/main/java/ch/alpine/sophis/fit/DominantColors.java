// code by jph
package ch.alpine.sophis.fit;

import ch.alpine.sophis.dv.Biinvariant;
import ch.alpine.sophis.dv.Biinvariants;
import ch.alpine.sophus.bm.CenterMean;
import ch.alpine.sophus.lie.rn.RGroup;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Flatten;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/DominantColors.html">DominantColors</a> */
public enum DominantColors {
  ;
  public static Tensor of(Tensor image, int k) {
    Biinvariant biinvariant = Biinvariants.METRIC.ofSafe(RGroup.INSTANCE);
    Tensor sequence = Flatten.of(image, 1);
    KMeans kMeans = new KMeans(biinvariant.relative_distances(sequence), new CenterMean(RGroup.INSTANCE.biinvariantMean()), sequence);
    kMeans.setSeeds(k);
    int complete = kMeans.complete();
    IO.println(complete);
    return kMeans.seeds();
  }
}
