// code by jph
package ch.alpine.sophis.ref.d2;

import java.util.function.Function;

import ch.alpine.sophus.bm.BiinvariantMean;

public enum SurfaceMeshRefinements {
  LINEAR(TriQuadLinearRefinement::new),
  DOO_SABIN(DooSabinRefinement::new),
  CATMULL_CLARK(CatmullClarkRefinement::new);

  private final Function<BiinvariantMean, SurfaceMeshRefinement> function;

  SurfaceMeshRefinements(Function<BiinvariantMean, SurfaceMeshRefinement> function) {
    this.function = function;
  }

  public SurfaceMeshRefinement operator(BiinvariantMean biinvariantMean) {
    return function.apply(biinvariantMean);
  }
}
