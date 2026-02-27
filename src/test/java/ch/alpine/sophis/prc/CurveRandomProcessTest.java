// code by jph
package ch.alpine.sophis.prc;

import java.util.Random;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.hs.s.Sphere;
import ch.alpine.sophus.hs.st.StiefelManifold;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

class CurveRandomProcessTest {
  @Test
  void testSphere() {
    Sphere sphere = new Sphere(2);
    CurveRandomProcess.stream(sphere, RealScalar.of(0.1), Tensors.vector(0, 0, 1)).limit(10).toList();
  }

  @Test
  void testStiefel() {
    StiefelManifold stiefelManifold = new StiefelManifold(5, 2);
    Tensor p = stiefelManifold.randomSample(new Random(2));
    CurveRandomProcess.stream(stiefelManifold, RealScalar.of(0.1), p).limit(10).toList();
  }
}
