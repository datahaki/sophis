// code by jph
package ch.alpine.sophis.flt.ga;

import java.util.function.Supplier;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorUnaryOperator;

/** a filter has an internal state.
 * this construction asserts that a filter starts fresh for new data */
public record CausalFilter(Supplier<TensorUnaryOperator> supplier) implements TensorUnaryOperator {
  @Override
  public Tensor apply(Tensor tensor) {
    return supplier.get().slash(tensor);
  }
}
