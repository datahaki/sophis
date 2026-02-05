// code by jph
package ch.alpine.sophis.crv.clt;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

import ch.alpine.sophis.crv.clt.ClothoidSolutions.Search;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.Clips;

public record PriorityClothoid(Comparator<Clothoid> comparator) implements ClothoidBuilder, Serializable {
  private static final ClothoidSolutions CLOTHOID_SOLUTIONS = ClothoidSolutions.of(Clips.absolute(15.0), 101);

  /** @param comparator
   * @return */
  public static ClothoidBuilder of(Comparator<Clothoid> comparator) {
    return new PriorityClothoid(Objects.requireNonNull(comparator));
  }

  @Override
  public Clothoid curve(Tensor p, Tensor q) {
    ClothoidContext clothoidContext = new ClothoidContext(p, q);
    Search search = CLOTHOID_SOLUTIONS.new Search(clothoidContext.s1(), clothoidContext.s2());
    return ClothoidEmit.stream(clothoidContext, search.lambdas()) //
        .min(comparator) //
        .orElseThrow();
  }
}
