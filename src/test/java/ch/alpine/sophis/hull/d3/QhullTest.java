// code by John E. Lloyd
package ch.alpine.sophis.hull.d3;

import org.junit.jupiter.api.RepeatedTest;

import ch.alpine.tensor.Tensor;

class QhullTest {
  @RepeatedTest(100)
  void testSimple() {
    Tensor coords = TestHelper.randomCubedPoints(100, 1.0, 0.5);
    QuickHull3D hull = new QuickHull3D(coords);
    // hull.buildHull();
    // double[] pnts = Primitives.toDoubleArray(Flatten.of(coords));
    // Tensor _pnts =
    TestHelper.addDegeneracy(QuickHull3DHelper.VERTEX_DEGENERACY, coords, hull);
    if (!hull.check(System.out)) {
      System.out.println("failed for qhull triangulated");
    }
    if (!hull.check(System.out)) {
      System.out.println("failed for qhull regular");
    }
    // hull = new QuickHull3D ();
    hull.buildHull();
    // hull.triangulate();
    if (!hull.check(System.out)) {
      System.out.println("failed for QuickHull3D triangulated");
    }
    // hull = new QuickHull3D ();
    hull.buildHull();
    if (!hull.check(System.out)) {
      System.out.println("failed for QuickHull3D regular");
    }
  }
}
