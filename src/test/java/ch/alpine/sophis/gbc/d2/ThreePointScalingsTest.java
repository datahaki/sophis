// code by jph
package ch.alpine.sophis.gbc.d2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.sophis.dv.BarycentricCoordinate;
import ch.alpine.sophis.dv.HsCoordinates;
import ch.alpine.sophus.lie.rn.RGroup;
import ch.alpine.sophus.math.AffineQ;
import ch.alpine.sophus.math.AveragingWeights;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.chq.FiniteTensorQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.lie.rot.CirclePoints;
import ch.alpine.tensor.sca.Chop;

class ThreePointScalingsTest {
  @Test
  void testSimple() throws ClassNotFoundException, IOException {
    for (ThreePointScalings barycenter : ThreePointScalings.values()) {
      BarycentricCoordinate barycentricCoordinate = Serialization.copy( //
          new HsCoordinates(RGroup.INSTANCE, ThreePointCoordinate.of(barycenter)));
      for (int n = 3; n < 10; ++n) {
        Tensor points = CirclePoints.of(n);
        Tensor w1 = barycentricCoordinate.weights(points, Array.zeros(2));
        Chop._08.requireClose(w1, AveragingWeights.INSTANCE.origin(points));
        AffineQ.INSTANCE.require(w1); // , Chop._08);
        Tensor w2 = barycentricCoordinate.weights(CirclePoints.of(n), Tensors.vector(2, 2));
        assertEquals(w2.length(), n);
        assertTrue(FiniteTensorQ.of(w2));
        AffineQ.INSTANCE.require(w2); // , Chop._08);
      }
    }
  }

  @Test
  void test() {
    assertEquals(ThreePointScalings.values().length, 3);
  }
}
