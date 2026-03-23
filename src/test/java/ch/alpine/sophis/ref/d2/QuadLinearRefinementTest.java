// code by jph
package ch.alpine.sophis.ref.d2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import ch.alpine.sophis.hull.d3.ConvexHull3D;
import ch.alpine.sophis.srf.SurfaceMesh;
import ch.alpine.sophis.srf.d3.PlatonicSolid;
import ch.alpine.sophus.bm.LinearBiinvariantMean;

class QuadLinearRefinementTest {
  @ParameterizedTest
  @EnumSource
  void testTriQuadSubdiv(PlatonicSolid platonicSolid) {
    QuadLinearRefinement triQuadLinearRefinement = new QuadLinearRefinement(LinearBiinvariantMean.INSTANCE);
    SurfaceMesh surfaceMesh = platonicSolid.surfaceMesh();
    SurfaceMesh refinedMesh = triQuadLinearRefinement.refine(surfaceMesh);
    List<int[]> list = ConvexHull3D.of(refinedMesh.vrt);
    assertEquals(list.size(), surfaceMesh.faces().size());
  }
}
