// code by John E. Lloyd
package ch.alpine.sophis.hull.d3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.RepeatedTest;

import ch.alpine.tensor.Tensor;

/** some test cases furnished by Mariano Zelke, Berlin */
class QuickHull3DTest {
  @RepeatedTest(10)
  void testRandom() {
    for (int n = 20; n < 200; n += 10) {
      QuickHull3DHelper tester = new QuickHull3DHelper();
      tester.test(TestHelper.randomPoints(n, 1.0));
    }
  }

  @RepeatedTest(10)
  void testSpherical() {
    for (int n = 20; n < 200; n += 10) {
      Tensor coords = TestHelper.randomSphericalPoints(n, 1.0);
      QuickHull3DHelper tester = new QuickHull3DHelper();
      tester.test(coords);
    }
  }

  @RepeatedTest(10)
  void testCubed() {
    for (int n = 20; n < 200; n += 10) {
      Tensor coords = TestHelper.randomCubedPoints(n, 1.0, 0.5);
      QuickHull3DHelper tester = new QuickHull3DHelper();
      tester.test(coords);
    }
  }

  @RepeatedTest(10)
  void testGrid() {
    for (int n = 2; n <= 10; n++) {
      Tensor coords = TestHelper.randomGridPoints(n, 4.0);
      QuickHull3DHelper tester = new QuickHull3DHelper();
      tester.test(coords);
    }
  }

  @RepeatedTest(10)
  void testFails0() {
    Tensor _coords = TestHelper.randomDegeneratePoints(10, 0);
    QuickHull3D hull = new QuickHull3D(_coords);
    Exception exception = assertThrows(Exception.class, hull::buildHull);
    // jan: from time to time the exception will have a different message with "colinear"
    assertEquals(exception.getMessage(), "Input points appear to be coincident");
  }

  @RepeatedTest(10)
  void testFails1() {
    Tensor _coords = TestHelper.randomDegeneratePoints(10, 1);
    QuickHull3D hull = new QuickHull3D(_coords);
    Exception exception = assertThrows(Exception.class, hull::buildHull);
    assertEquals(exception.getMessage(), "Input points appear to be colinear");
  }

  @RepeatedTest(10)
  void testFails2() {
    Tensor _coords = TestHelper.randomDegeneratePoints(10, 2);
    QuickHull3D hull = new QuickHull3D(_coords);
    Exception exception = assertThrows(Exception.class, hull::buildHull);
    assertEquals(exception.getMessage(), "Input points appear to be coplanar");
  }
}
