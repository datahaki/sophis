// code by jph
package ch.alpine.sophis.ref.d2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import ch.alpine.sophis.SurfaceMeshExamples;
import ch.alpine.sophis.hull.d3.ConvexHull3D;
import ch.alpine.sophis.srf.SurfaceMesh;
import ch.alpine.sophis.srf.d3.PlatonicSolid;
import ch.alpine.sophus.bm.LinearBiinvariantMean;
import ch.alpine.sophus.lie.se2.Se2CoveringGroup;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.red.FirstPosition;

class TriQuadLinearRefinementTest {
  @Test
  void testSe2CSimple() throws ClassNotFoundException, IOException {
    SurfaceMeshRefinement surfaceMeshRefinement = //
        Serialization.copy(new TriQuadLinearRefinement(Se2CoveringGroup.INSTANCE.biinvariantMean()));
    SurfaceMesh surfaceMesh = surfaceMeshRefinement.refine(SurfaceMeshExamples.quads6());
    assertEquals(surfaceMesh.faces().size(), 24);
    assertEquals(surfaceMesh.vrt.length(), 35);
  }

  @Test
  void testRnSimple() throws ClassNotFoundException, IOException {
    SurfaceMeshRefinement surfaceMeshRefinement = //
        Serialization.copy(new TriQuadLinearRefinement(LinearBiinvariantMean.INSTANCE));
    SurfaceMesh surfaceMesh = surfaceMeshRefinement.refine(SurfaceMeshExamples.quads5());
    assertEquals(surfaceMesh.faces().size(), 20);
    assertEquals(surfaceMesh.vrt.length(), 31);
    ExactTensorQ.require(surfaceMesh.vrt);
  }

  @Test
  void testR3Simple() throws ClassNotFoundException, IOException {
    SurfaceMeshRefinement surfaceMeshRefinement = //
        Serialization.copy(new TriQuadLinearRefinement(LinearBiinvariantMean.INSTANCE));
    SurfaceMesh surfaceMesh = surfaceMeshRefinement.refine(SurfaceMeshExamples.unitQuad());
    assertEquals(surfaceMesh.faces().size(), 4);
    assertEquals(surfaceMesh.vrt.length(), 9);
    assertTrue(FirstPosition.of(surfaceMesh.vrt, Array.zeros(3)).isPresent());
    ExactTensorQ.require(surfaceMesh.vrt);
  }

  @ParameterizedTest
  @EnumSource
  void testTriQuadSubdiv(PlatonicSolid platonicSolid) {
    TriQuadLinearRefinement triQuadLinearRefinement = new TriQuadLinearRefinement(LinearBiinvariantMean.INSTANCE);
    SurfaceMesh surfaceMesh = platonicSolid.surfaceMesh();
    SurfaceMesh refinedMesh = triQuadLinearRefinement.refine(surfaceMesh);
    List<int[]> list = ConvexHull3D.of(refinedMesh.vrt);
    assertEquals(list.size(), surfaceMesh.faces().size());
  }
}
