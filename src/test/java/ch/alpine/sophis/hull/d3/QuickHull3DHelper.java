/** Copyright John E. Lloyd, 2004. All rights reserved. Permission to use,
 * copy, modify and redistribute is granted, provided that this copyright
 * notice is retained and the author is given credit whenever appropriate.
 *
 * This software is distributed "as is", without any warranty, including
 * any implied warranty of merchantability or fitness for a particular
 * use. The author assumes no responsibility for, and shall not be liable
 * for, any special, indirect, or consequential damages, or any damages
 * whatsoever, arising out of or in connection with the use of this
 * software. */
package ch.alpine.sophis.hull.d3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.random.RandomGenerator;

import ch.alpine.tensor.Tensor;

/** Testing class for QuickHull3D. Running the command
 * <pre>
 * java quickhull3d.QuickHull3DTest
 * </pre>
 * will cause QuickHull3D to be tested on a number of randomly
 * choosen input sets, with degenerate points added near
 * the edges and vertics of the convex hull.
 *
 * <p>The command
 * <pre>
 * java quickhull3d.QuickHull3DTest -timing
 * </pre>
 * will cause timing information to be produced instead.
 *
 * @author John E. Lloyd, Fall 2004 */
public class QuickHull3DHelper {
  static final int NO_DEGENERACY = 0;
  static final int EDGE_DEGENERACY = 1;
  static final int VERTEX_DEGENERACY = 2;
  static final int degeneracyTest = VERTEX_DEGENERACY;
  // ---
  boolean debugEnable = false;

  /** Returns true if two face index sets are equal,
   * modulo a cyclical permuation.
   *
   * @param indices1 index set for first face
   * @param indices2 index set for second face
   * @return true if the index sets are equivalent */
  public static boolean faceIndicesEqual(int[] indices1, int[] indices2) {
    if (indices1.length != indices2.length) {
      return false;
    }
    int len = indices1.length;
    int j;
    for (j = 0; j < len; j++) {
      if (indices1[0] == indices2[j]) {
        break;
      }
    }
    if (j == len) {
      return false;
    }
    for (int i = 1; i < len; i++) {
      if (indices1[i] != indices2[(j + i) % len]) {
        return false;
      }
    }
    return true;
  }

  int cnt = 0;

  void singleTest(Tensor coords) {
    QuickHull3D hull = new QuickHull3D(coords);
    hull.setDebug(debugEnable);
    hull.buildHull();
    if (!hull.check(System.out))
      throw new RuntimeException();
    if (degeneracyTest != NO_DEGENERACY) {
      degenerateTest(hull, coords);
    }
  }

  void degenerateTest(QuickHull3D hull, Tensor coords) {
    Tensor _coords = TestHelper.addDegeneracy(degeneracyTest, coords, hull);
    QuickHull3D xhull = new QuickHull3D(_coords);
    xhull.setDebug(debugEnable);
    try {
      xhull.buildHull();
    } catch (Exception e) {
      for (int i = 0; i < _coords.length(); i++) {
        System.out.println(_coords.get(i));
      }
    }
    if (!xhull.check(System.out))
      throw new RuntimeException();
  }

  void test(Tensor coords) {
    singleTest(coords);
    {
      List<double[]> rpyList = new ArrayList<>();
      rpyList.add(new double[] { 0, 0, 0 });
      rpyList.add(new double[] { 10, 20, 30 });
      rpyList.add(new double[] { -45, 60, 91 });
      rpyList.add(new double[] { 125, 67, 81 });
      RandomGenerator random = new Random();
      rpyList.add(new double[] { random.nextInt(180) - 90, random.nextInt(180) - 90, random.nextInt(180) - 90 });
      for (double[] rpy : rpyList) {
        Tensor xcoords = TestHelper.rotateCoords(coords, Math.toRadians(rpy[0]), Math.toRadians(rpy[1]), Math.toRadians(rpy[2]));
        singleTest(xcoords);
      }
    }
  }

  /** Runs timing tests on QuickHull3D, and prints
   * the results to System.out. */
  public void timingTests() {
    long t0, t1;
    int n = 10;
    System.out.println("warming up ... ");
    for (int i = 0; i < 2; i++) {
      Tensor _coords = TestHelper.randomSphericalPoints(10000, 1.0);
      QuickHull3D hull = new QuickHull3D(_coords);
      hull.buildHull();
    }
    int cnt = 10;
    for (int i = 0; i < 4; i++) {
      n *= 10;
      t0 = System.currentTimeMillis();
      for (int k = 0; k < cnt; k++) {
        Tensor _coords = TestHelper.randomSphericalPoints(n, 1.0);
        QuickHull3D hull = new QuickHull3D(_coords);
        hull.buildHull();
      }
      t1 = System.currentTimeMillis();
      System.out.println(n + " points: " + (t1 - t0) / (double) cnt + " msec");
    }
  }
}
