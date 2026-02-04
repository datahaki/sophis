// code by jph
package ch.alpine.sophis.srf;

import java.io.Serializable;
import java.util.stream.Stream;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/DirectedEdge.html">DirectedEdge</a> */
public record IntDirectedEdge(int i, int j) implements Serializable {
  /** @param tensor
   * @return tensor.Get(i, j) */
  public Scalar Get(Tensor tensor) {
    return tensor.Get(i, j);
  }

  public IntDirectedEdge reverse() {
    return new IntDirectedEdge(j, i);
  }

  public Stream<Integer> stream() {
    return Stream.of(i, j);
  }

  public int[] array() {
    return new int[] { i, j };
  }
}
