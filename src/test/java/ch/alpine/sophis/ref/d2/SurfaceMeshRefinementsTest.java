// code by jph
package ch.alpine.sophis.ref.d2;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import ch.alpine.sophis.SurfaceMeshExamples;
import ch.alpine.sophis.srf.SurfaceMesh;
import ch.alpine.sophus.bm.LinearBiinvariantMean;

class SurfaceMeshRefinementsTest {
  @ParameterizedTest
  @EnumSource
  void testMixed7(SurfaceMeshRefinements smr) {
    SurfaceMeshRefinement refinement = smr.operator(LinearBiinvariantMean.INSTANCE);
    SurfaceMesh refine = refinement.refine(SurfaceMeshExamples.mixed7());
    refinement.refine(refine);
  }

  @ParameterizedTest
  @EnumSource
  void testMixed11(SurfaceMeshRefinements smr) {
    SurfaceMeshRefinement refinement = smr.operator(LinearBiinvariantMean.INSTANCE);
    SurfaceMesh refine = refinement.refine(SurfaceMeshExamples.mixed11());
    refinement.refine(refine);
  }
}
