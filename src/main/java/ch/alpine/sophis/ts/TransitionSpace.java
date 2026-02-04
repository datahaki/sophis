// code by jph
package ch.alpine.sophis.ts;

import ch.alpine.tensor.Tensor;

/** TransitionSpace is a factory for {@link Transition}s
 * An instance of TransitionSpace is immutable.
 * 
 * @see RnTransitionSpace */
@FunctionalInterface
public interface TransitionSpace {
  /** @param head state
   * @param tail state
   * @return transition that represents the (unique) connection between the start and end state */
  Transition connect(Tensor head, Tensor tail);
}
