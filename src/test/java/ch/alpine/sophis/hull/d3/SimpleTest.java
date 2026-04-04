// code by John E. Lloyd
package ch.alpine.sophis.hull.d3;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Partition;

/** Simple example usage of QuickHull3D. Run as the command
 * <pre>
 * java quickhull3d.SimpleExample
 * </pre> */
class SimpleTest {
  /** Run for a simple demonstration of QuickHull3D. */
  @Test
  void testSimple() {
    // x y z coordinates of 6 points
    Tensor _coords = Partition.of(Tensors.vectorDouble( //
        0.0, 0.0, 0.0, //
        1.0, 0.5, 0.0, //
        2.0, 0.0, 0.0, //
        0.5, 0.5, 0.5, //
        0.0, 0.0, 2.0, //
        0.1, 0.2, 0.3, //
        0.0, 2.0, 0.0), 3);
    QuickHull3D hull = new QuickHull3D(_coords);
    hull.buildHull();
    List<int[]> faceIndices = hull.getFaces();
    assertArrayEquals(faceIndices.get(0), new int[] { 2, 4, 0 });
    assertArrayEquals(faceIndices.get(1), new int[] { 6, 2, 0 });
    assertArrayEquals(faceIndices.get(2), new int[] { 6, 0, 4 });
    assertArrayEquals(faceIndices.get(3), new int[] { 6, 4, 2 });
  }

  @Test
  void testSimple1() {
    QuickHull3DHelper tester = new QuickHull3DHelper();
    tester.debugEnable = true;
    Tensor coords = Partition.of(Tensors.vectorInt( //
        21, 0, 0, //
        0, 21, 0, //
        0, 0, 0, //
        18, 2, 6, //
        1, 18, 5, //
        2, 1, 3, //
        14, 3, 10, //
        4, 14, 14, //
        3, 4, 10, //
        10, 6, 12, //
        5, 10, 15), 3);
    tester.test(coords);
  }

  @Test
  void testSimple2() {
    Tensor coords = Partition.of(Tensors.vectorDouble( //
        0.0, 0.0, 0.0, //
        21.0, 0.0, 0.0, //
        0.0, 21.0, 0.0, //
        2.0, 1.0, 2.0, //
        17.0, 2.0, 3.0, //
        1.0, 19.0, 6.0, //
        4.0, 3.0, 5.0, //
        13.0, 4.0, 5.0, //
        3.0, 15.0, 8.0, //
        6.0, 5.0, 6.0, //
        9.0, 6.0, 11.0), 3);
    QuickHull3DHelper tester = new QuickHull3DHelper();
    tester.test(coords);
  }

  @Test
  void testDoublePrec() {
    double nextUp = Math.nextUp(1.0) - 1;
    assertEquals(QuickHull3D.DOUBLE_PREC, RealScalar.of(nextUp));
  }
}
