// code by jph
package ch.alpine.sophis.crv.d2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Random;
import java.util.random.RandomGenerator;

import org.junit.jupiter.api.Test;

import ch.alpine.sophis.crv.d2.ConvexHull2D;
import ch.alpine.sophis.crv.d2.PolygonArea;
import ch.alpine.sophis.crv.d2.PolygonCentroid;
import ch.alpine.sophis.crv.d2.PolygonClip;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.lie.rot.CirclePoints;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.sca.Chop;

class PolygonCentroidTest {
  @Test
  void testSimple() {
    for (int n = 2; n < 10; ++n) {
      Tensor centroid = PolygonCentroid.of(CirclePoints.of(n));
      Tolerance.CHOP.requireAllZero(centroid);
    }
  }

  @Test
  void testSingle() {
    Tensor centroid = PolygonCentroid.of(Tensors.fromString("{{2, 3}}"));
    assertEquals(centroid, Tensors.vector(2, 3));
  }

  @Test
  void testRandom() {
    RandomGenerator randomGenerator = new Random(3);
    for (int count = 0; count < 100; ++count) {
      Tensor poly1 = ConvexHull2D.of(RandomVariate.of(NormalDistribution.standard(), randomGenerator, 3 + randomGenerator.nextInt(3), 2));
      Tensor poly2 = ConvexHull2D.of(RandomVariate.of(NormalDistribution.standard(), randomGenerator, 3 + randomGenerator.nextInt(3), 2));
      Tensor r12 = PolygonClip.of(poly1).apply(poly2);
      Tensor r21 = PolygonClip.of(poly2).apply(poly1);
      if (r12.length() != r21.length()) {
        System.out.println(poly1);
        System.out.println(poly2);
        fail();
      }
      Chop._10.requireClose(PolygonArea.of(r12), PolygonArea.of(r21));
      if (0 < r12.length())
        Chop._10.requireClose(PolygonCentroid.of(r12), PolygonCentroid.of(r21));
    }
  }

  @Test
  void testTranslated() {
    for (int n = 3; n < 10; ++n) {
      Tensor shift = RandomVariate.of(UniformDistribution.unit(), 2);
      Tensor centroid = PolygonCentroid.of(Tensor.of(CirclePoints.of(n).stream().map(shift::add)));
      Tolerance.CHOP.requireClose(centroid, shift);
    }
  }

  @Test
  void testSingleFail() {
    assertThrows(Exception.class, () -> PolygonCentroid.of(Tensors.fromString("{{2, 3, 4}}")));
  }
}
