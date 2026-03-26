// code by jph
package ch.alpine.sophis.ref.d2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import ch.alpine.sophis.SurfaceMeshExamples;
import ch.alpine.sophis.hull.d3.ConvexHull3D;
import ch.alpine.sophis.srf.SurfaceMesh;
import ch.alpine.sophis.srf.d3.PlatonicSolid;
import ch.alpine.sophis.srf.io.PlyFormat;
import ch.alpine.sophus.bm.LinearBiinvariantMean;
import ch.alpine.sophus.lie.se2.Se2CoveringGroup;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Flatten;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.ext.ResourceData;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.red.Mean;

class CatmullClarkRefinementTest {
  @Test
  void testSimple() throws ClassNotFoundException, IOException {
    SurfaceMeshRefinement surfaceMeshRefinement = //
        Serialization.copy(new CatmullClarkRefinement(Se2CoveringGroup.INSTANCE.biinvariantMean()));
    SurfaceMesh surfaceMesh = surfaceMeshRefinement.refine(SurfaceMeshExamples.quads6());
    assertEquals(surfaceMesh.faces().size(), 24);
    assertEquals(surfaceMesh.vrt.length(), 35);
  }

  @Test
  void testCube() {
    SurfaceMesh surfaceMesh = PlyFormat.parse(ResourceData.lines("ch/alpine/sophis/mesh/unitcube.ply"));
    assertTrue(surfaceMesh.boundary().isEmpty());
    SurfaceMeshRefinement surfaceMeshRefinement = new CatmullClarkRefinement(LinearBiinvariantMean.INSTANCE);
    SurfaceMesh refine1 = surfaceMeshRefinement.refine(surfaceMesh);
    assertEquals(Flatten.scalars(refine1.vrt).distinct().count(), 7);
    assertEquals(refine1.vrt.length(), 26);
    assertEquals(Mean.of(refine1.vrt), Tensors.vector(0.5, 0.5, 0.5));
    ExactTensorQ.require(refine1.vrt);
    assertEquals(refine1.faces().size(), 24);
    // ---
    SurfaceMesh refine2 = surfaceMeshRefinement.refine(refine1);
    assertEquals(Mean.of(refine2.vrt), Tensors.vector(0.5, 0.5, 0.5));
    ExactTensorQ.require(refine2.vrt);
  }

  @ParameterizedTest
  @EnumSource
  void testPlatonic(PlatonicSolid platonicSolid) {
    SurfaceMeshRefinement surfaceMeshRefinement = new CatmullClarkRefinement(LinearBiinvariantMean.INSTANCE);
    SurfaceMesh surfaceMesh = platonicSolid.surfaceMesh();
    SurfaceMesh refinedMesh = surfaceMeshRefinement.refine(surfaceMesh);
    List<int[]> list = ConvexHull3D.of(refinedMesh.vrt);
    List<Integer> list2 = list.stream().flatMap(face -> IntStream.of(face).boxed()).distinct().toList();
    assertEquals(list2.size(), refinedMesh.vrt.length());
  }
}
