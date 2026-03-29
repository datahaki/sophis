// code by jph
package ch.alpine.sophis.srf;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.mat.IdentityMatrix;

class ReduceMeshTest {
  @Test
  void test() {
    RandomGenerator rg = ThreadLocalRandom.current();
    Tensor p = UnitVector.of(2, 0);
    Tensor q = UnitVector.of(2, 1);
    SurfaceMesh surfaceMesh = new SurfaceMesh();
    surfaceMesh.addVert(p);
    surfaceMesh.addVert(q);
    for (int i = 0; i < 100; ++i)
      surfaceMesh.addVert(rg.nextBoolean() ? p : q);
    ReduceMesh reduceMesh = new ReduceMesh(RealScalar.of(0.2));
    SurfaceMesh result = reduceMesh.of(surfaceMesh);
    assertEquals(result.vrt, IdentityMatrix.of(2));
    assertEquals(surfaceMesh.vrt.length(), 2 + 100);
  }
}
