// code by jph
package ch.alpine.sophis.ref.d2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.sophis.ref.d2.DooSabinRefinement;
import ch.alpine.sophis.ref.d2.SurfaceMeshRefinement;
import ch.alpine.sophis.srf.SurfaceMesh;
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
    SurfaceMesh surfaceMesh = PlyFormat.parse(ResourceData.lines("/ch/alpine/sophus/mesh/unitcube.ply"));
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
}
