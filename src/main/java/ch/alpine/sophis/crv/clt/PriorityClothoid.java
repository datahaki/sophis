// code by jph
package ch.alpine.sophis.crv.clt;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.Clips;

public record PriorityClothoid(Comparator<Clothoid> comparator) implements ClothoidBuilder, Serializable {
  /** @param comparator
   * @return */
  public static ClothoidBuilder of(Comparator<Clothoid> comparator) {
    return new PriorityClothoid(Objects.requireNonNull(comparator));
  }

  @Override
  public Clothoid curve(Tensor p, Tensor q) {
    ClothoidContext clothoidContext = new ClothoidContext(p, q);
    ClothoidSolutions clothoidSolutions = new ClothoidSolutions(ClothoidTangentDefect.of(clothoidContext), Clips.absolute(15.0));
    return ClothoidEmit.stream(clothoidContext, clothoidSolutions.lambdas()) //
        .min(comparator) //
        .orElseThrow();
  }
}
