// code by jph, gjoel
package ch.alpine.sophis.ts;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

import ch.alpine.sophis.crv.dub.DubinsPath;
import ch.alpine.sophis.crv.dub.FixedRadiusDubins;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.Sign;

/** @param radius positive
 * @param comparator */
public record DubinsTransitionSpace(Scalar radius, Comparator<DubinsPath> comparator) implements TransitionSpace, Serializable {
  public DubinsTransitionSpace {
    Sign.requirePositive(radius);
    Objects.requireNonNull(comparator);
  }

  @Override // from TransitionSpace
  public DubinsTransition connect(Tensor head, Tensor tail) {
    return new DubinsTransition(head, tail, dubinsPath(head, tail));
  }

  private DubinsPath dubinsPath(Tensor start, Tensor end) {
    return FixedRadiusDubins.of(start, end, radius).stream().min(comparator).orElseThrow();
  }
}
