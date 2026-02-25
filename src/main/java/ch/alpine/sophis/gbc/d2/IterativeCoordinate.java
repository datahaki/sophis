// code by jph
package ch.alpine.sophis.gbc.d2;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

import ch.alpine.sophis.api.Genesis;
import ch.alpine.sophis.dv.AffineCoordinate;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.chq.FiniteTensorQ;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.nrm.NormalizeTotal;
import ch.alpine.tensor.red.Times;

/** References:
 * "Iterative coordinates"
 * by Chongyang Deng, Qingjun Chang, Kai Hormann, 2020
 * 
 * @see InsidePolygonCoordinate
 * 
 * @param genesis for instance {@link ThreePointCoordinate}, or {@link AffineCoordinate}
 * @param k non-negative */
public record IterativeCoordinate(Genesis genesis, int k) implements Genesis {
  public IterativeCoordinate {
    Objects.requireNonNull(genesis);
    Integers.requirePositiveOrZero(k);
  }

  @Override // from Genesis
  public Tensor origin(Tensor levers) {
    Tensor scaling = InverseNorm.INSTANCE.origin(levers);
    return NormalizeTotal.FUNCTION.apply(FiniteTensorQ.of(scaling) //
        ? Times.of(scaling, iterate(Times.of(scaling, levers)))
        : scaling);
  }

  /** @param normalized points on circle
   * @return homogeneous coordinates */
  private Tensor iterate(Tensor normalized) {
    Deque<Tensor> deque = new ArrayDeque<>(k);
    for (int depth = 0; depth < k; ++depth) {
      Tensor midpoints = Adds.forward(normalized);
      Tensor scaling = InverseNorm.INSTANCE.origin(midpoints);
      normalized = Times.of(scaling, midpoints);
      deque.push(scaling);
    }
    Tensor weights = genesis.origin(normalized);
    while (!deque.isEmpty())
      weights = Adds.reverse(Times.of(deque.pop(), weights));
    return weights;
  }
}
