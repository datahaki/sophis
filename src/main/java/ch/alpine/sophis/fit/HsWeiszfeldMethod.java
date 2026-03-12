// code by jph
package ch.alpine.sophis.fit;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;

import ch.alpine.sophis.dv.Sedarim;
import ch.alpine.sophus.bm.BiinvariantMean;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.nrm.AveragingWeights;
import ch.alpine.tensor.nrm.NormalizeTotal;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.Chop;

/** iterative method to find solution to Fermat-Weber Problem
 * iteration based on Endre Vaszonyi Weiszfeld
 * 
 * <p>implementation based on
 * "Weiszfeld’s Method: Old and New Results"
 * by Amir Beck, Shoham Sabach
 * 
 * @param biinvariantMean
 * @param sedarim for instance InverseDistanceWeighting
 * @param chop
 * 
 * @see WeiszfeldMethod */
public record HsWeiszfeldMethod(BiinvariantMean biinvariantMean, Sedarim sedarim, Chop chop) //
    implements SpatialMedian, Serializable {
  private static final int MAX_ITERATIONS = 512;

  @Override // from SpatialMedian
  public Optional<Tensor> uniform(Tensor sequence) {
    return minimum(sequence, t -> t);
  }

  @Override // from SpatialMedian
  public Optional<Tensor> weighted(Tensor sequence, Tensor weights) {
    return minimum(sequence, Times.operator(weights));
  }

  private Optional<Tensor> minimum(Tensor sequence, UnaryOperator<Tensor> unaryOperator) {
    Tensor prev = null;
    Tensor weights = AveragingWeights.of(sequence.length());
    for (int iteration = 0; iteration < MAX_ITERATIONS; ++iteration) {
      Optional<Tensor> optional = biinvariantMean.optional(sequence, NormalizeTotal.FUNCTION.apply(unaryOperator.apply(weights)));
      if (optional.isEmpty())
        return optional;
      Tensor next = optional.orElseThrow();
      if (Objects.nonNull(prev) && chop.isClose(prev, next))
        return optional;
      prev = next;
      weights = sedarim.sunder(next);
    }
    return Optional.empty();
  }
}
