// code by jph
package ch.alpine.sophis.ref.d2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import ch.alpine.sophis.hull.d3.ConvexHull3D;
import ch.alpine.sophis.srf.SurfaceMesh;
import ch.alpine.sophis.srf.d3.PlatonicSolid;
import ch.alpine.sophis.srf.io.PlyFormat;
import ch.alpine.sophus.bm.LinearBiinvariantMean;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Flatten;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.ext.ResourceData;
import ch.alpine.tensor.red.Mean;

class DooSabinRefinementTest {
  @Test
  void testCube() {
    SurfaceMesh surfaceMesh = PlyFormat.parse(ResourceData.lines("ch/alpine/sophis/mesh/unitcube.ply"));
    SurfaceMeshRefinement surfaceMeshRefinement = new DooSabinRefinement(LinearBiinvariantMean.INSTANCE);
    SurfaceMesh refine1 = surfaceMeshRefinement.refine(surfaceMesh);
    assertEquals(Flatten.scalars(refine1.vrt).distinct().count(), 4); // 0 1/4 3/4 1
    assertEquals(refine1.vrt.length(), 24);
    assertEquals(Mean.of(refine1.vrt), Tensors.vector(0.5, 0.5, 0.5));
    ExactTensorQ.require(refine1.vrt);
    assertEquals(refine1.faces().size(), 6 + 12 + 8);
    // ---
    SurfaceMesh refine2 = surfaceMeshRefinement.refine(refine1);
    assertEquals(Mean.of(refine2.vrt), Tensors.vector(0.5, 0.5, 0.5));
  }

  @ParameterizedTest
  @EnumSource
  void testPlatonic(PlatonicSolid platonicSolid) {
    SurfaceMeshRefinement surfaceMeshRefinement = new DooSabinRefinement(LinearBiinvariantMean.INSTANCE);
    SurfaceMesh surfaceMesh = platonicSolid.surfaceMesh();
    SurfaceMesh refinedMesh = surfaceMeshRefinement.refine(surfaceMesh);
    List<int[]> list = ConvexHull3D.of(refinedMesh.vrt);
    List<Integer> list2 = list.stream().flatMap(face -> IntStream.of(face).boxed()).distinct().toList();
    assertEquals(list2.size(), refinedMesh.vrt.length());
  }
}
