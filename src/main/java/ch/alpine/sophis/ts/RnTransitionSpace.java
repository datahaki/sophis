// code by jph, gjoel
package ch.alpine.sophis.ts;

import ch.alpine.tensor.Tensor;

public enum RnTransitionSpace implements TransitionSpace {
  INSTANCE;

  @Override // from TransitionSpace
  public RnTransition connect(Tensor start, Tensor end) {
    return new RnTransition(start, end);
  }
}
