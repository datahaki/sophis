// code by jph
package ch.alpine.sophis.itp;

import ch.alpine.sophus.math.api.TensorMetric;
import ch.alpine.tensor.alg.AdjacentReduce;
import ch.alpine.tensor.alg.Differences;
import ch.alpine.tensor.api.TensorUnaryOperator;

/** implementation taken from {@link Differences} */
public enum AdjacentDistances {
  ;
  public static TensorUnaryOperator of(TensorMetric tensorMetric) {
    return new AdjacentReduce(tensorMetric::distance);
  }
}
