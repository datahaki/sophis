// code by John E. Lloyd
package ch.alpine.sophis.hull.d3;

import java.util.List;
import java.util.Random;
import java.util.random.RandomGenerator;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Partition;
import ch.alpine.tensor.io.Primitives;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.sca.Clips;

enum TestHelper {
  ;
  private static final RandomGenerator RANDOM = new Random(); // random number generator
  private static final double epsScale = 2.0;

  /** Returns the coordinates for <code>num</code> points whose x, y, and
   * z values are randomly chosen within a given range.
   *
   * @param num number of points to produce
   * @param range coordinate values will lie between -range and range
   * @return array of coordinate values */
  public static Tensor randomPoints(int num, double range) {
    Distribution distribution = UniformDistribution.of(Clips.absolute(range));
    return RandomVariate.of(distribution, num, 3);
  }

  private static Tensor randomlyPerturb(Tensor pnt, Scalar tol) {
    Distribution distribution = UniformDistribution.of(Clips.absolute(tol.multiply(Rational.HALF)));
    Tensor delta = RandomVariate.of(distribution, 3);
    return pnt.add(delta);
  }

  /** Returns the coordinates for <code>num</code> randomly
   * chosen points which are degenerate which respect
   * to the specified dimensionality.
   *
   * @param num number of points to produce
   * @param dimen dimensionality of degeneracy: 0 = coincident,
   * 1 = colinear, 2 = coplaner.
   * @return array of coordinate values */
  public static Tensor randomDegeneratePoints(int num, int dimen) {
    Tensor coords = Tensors.empty();
    Tensor pnt = Tensors.vector(0, 0, 0);
    Tensor base = TestHelper.setRandom(-1, 1, RANDOM);
    Scalar tol = QuickHull3D.DOUBLE_PREC;
    if (dimen == 0) {
      for (int i = 0; i < num; i++) {
        pnt = randomlyPerturb(base, tol);
        coords.append(pnt);
      }
    } else if (dimen == 1) {
      Tensor u = TestHelper.setRandom(-1, 1, RANDOM);
      u = Vector2Norm.NORMALIZE.apply(u);
      for (int i = 0; i < num; i++) {
        Scalar a = RealScalar.of(2 * (RANDOM.nextDouble() - 0.5));
        pnt = u.multiply(a);
        pnt = randomlyPerturb(pnt.add(base), tol);
        coords.append(pnt);
      }
    } else // dimen == 2
    {
      Tensor nrm = TestHelper.setRandom(-1, 1, RANDOM);
      nrm = Vector2Norm.NORMALIZE.apply(nrm);
      for (int i = 0; i < num; i++) { // compute a random point and project it to the plane
        Tensor perp = Tensors.vector(0, 0, 0);
        pnt = TestHelper.setRandom(-1, 1, RANDOM);
        perp = nrm.multiply((Scalar) pnt.dot(nrm));
        pnt = pnt.subtract(perp);
        pnt = randomlyPerturb(pnt.add(base), tol);
        coords.append(pnt);
      }
    }
    return coords;
  }

  /** Returns the coordinates for <code>num</code> points whose x, y, and
   * z values are randomly chosen to lie within a sphere.
   *
   * @param num number of points to produce
   * @param radius radius of the sphere
   * @return array of coordinate values */
  public static Tensor randomSphericalPoints(int num, double radius) {
    Tensor coords = Tensors.empty();
    for (int i = 0; i < num;) {
      Tensor pnt = TestHelper.setRandom(-radius, radius, RANDOM);
      if (Vector2Norm.of(pnt).number().doubleValue() <= radius) {
        coords.append(pnt);
        i++;
      }
    }
    return coords;
  }

  /** Returns the coordinates for <code>num</code> points whose x, y, and
   * z values are each randomly chosen to lie within a specified
   * range, and then clipped to a maximum absolute
   * value. This means a large number of points
   * may lie on the surface of cube, which is useful
   * for creating degenerate convex hull situations.
   *
   * @param num number of points to produce
   * @param range coordinate values will lie between -range and
   * range, before clipping
   * @param max maximum absolute value to which the coordinates
   * are clipped
   * @return array of coordinate values */
  public static Tensor randomCubedPoints(int num, double range, double max) {
    Tensor coords = Tensors.empty();
    for (int i = 0; i < num; i++) {
      for (int k = 0; k < 3; k++) {
        double x = 2 * range * (RANDOM.nextDouble() - 0.5);
        if (x > max) {
          x = max;
        } else if (x < -max) {
          x = -max;
        }
        coords.append(RealScalar.of(x));
      }
    }
    return Partition.of(coords, 3);
  }

  private static Tensor shuffleCoords(Tensor coords) {
    return TensorShuffle.of(coords);
  }

  /** Returns randomly shuffled coordinates for points on a
   * three-dimensional grid, with a presecribed width between each point.
   *
   * @param gridSize number of points in each direction,
   * so that the total number of points produced is the cube of
   * gridSize.
   * @param width distance between each point along a particular
   * direction
   * @return array of coordinate values */
  public static Tensor randomGridPoints(int gridSize, double width) {
    // gridSize gives the number of points across a given dimension
    // any given coordinate indexed by i has value
    // (i/(gridSize-1) - 0.5)*width
    int num = gridSize * gridSize * gridSize;
    Tensor coords = Array.zeros(num, 3);
    int idx = 0;
    for (int i = 0; i < gridSize; i++) {
      for (int j = 0; j < gridSize; j++) {
        for (int k = 0; k < gridSize; k++) {
          coords.set(Tensors.vector( //
              (i / (double) (gridSize - 1) - 0.5) * width, //
              (j / (double) (gridSize - 1) - 0.5) * width, //
              (k / (double) (gridSize - 1) - 0.5) * width), idx);
          idx++;
        }
      }
    }
    return shuffleCoords(coords);
  }

  public static Tensor addDegeneracy(int type, Tensor coords, QuickHull3D hull) {
    // int numv = coords.length();
    List<int[]> faces = hull.getFaces();
    Tensor coordsx = coords.copy(); // new double[coords.length + faces.size() * 3];
    // System.arraycopy(coords, 0, coordsx, 0, coords.length);
    double[] lam = new double[3];
    Scalar eps = hull.getDistanceTolerance();
    for (int i = 0; i < faces.size(); i++) {
      // random point on an edge
      lam[0] = RANDOM.nextDouble();
      lam[1] = 1 - lam[0];
      lam[2] = 0.0;
      if (type == QuickHull3DHelper.VERTEX_DEGENERACY && (i % 2 == 0)) {
        lam[0] = 1.0;
        lam[1] = lam[2] = 0;
      }
      for (int j = 0; j < 3; j++) {
        int vtxi = faces.get(i)[j];
        Tensor cl = coords.get(vtxi).multiply(RealScalar.of(lam[j]));
        Tensor rnd = Tensors.vector(_ -> RealScalar.of(epsScale * eps.number().doubleValue() * (RANDOM.nextDouble() - 0.5)), 3);
        coordsx.append(cl.add(rnd));
        // for (int k = 0; k < 3; k++) {
        // coordsx[numv * 3 + k] += lam[j] * coords[vtxi * 3 + k] + epsScale * eps.number().doubleValue() * (RANDOM.nextDouble() - 0.5);
        // }
      }
      // numv++;
    }
    return shuffleCoords(coordsx);
  }

  public static Tensor rotateCoords(Tensor coords, double roll, double pitch, double yaw) {
    double sroll = Math.sin(roll);
    double croll = Math.cos(roll);
    double spitch = Math.sin(pitch);
    double cpitch = Math.cos(pitch);
    double syaw = Math.sin(yaw);
    double cyaw = Math.cos(yaw);
    double m00 = croll * cpitch;
    double m10 = sroll * cpitch;
    double m20 = -spitch;
    double m01 = croll * spitch * syaw - sroll * cyaw;
    double m11 = sroll * spitch * syaw + croll * cyaw;
    double m21 = cpitch * syaw;
    double m02 = croll * spitch * cyaw + sroll * syaw;
    double m12 = sroll * spitch * cyaw - croll * syaw;
    double m22 = cpitch * cyaw;
    Tensor res = Tensors.empty();
    for (Tensor _xyz : coords) {
      double[] xyz = Primitives.toDoubleArray(_xyz);
      res.append(Tensors.vectorDouble( //
          m00 * xyz[0] + m01 * xyz[1] + m02 * xyz[2], m10 * xyz[0] + m11 * xyz[1] + m12 * xyz[2], m20 * xyz[0] + m21 * xyz[1] + m22 * xyz[2]));
    }
    return res;
  }

  /** Sets the elements of this vector to uniformly distributed
   * random values in a specified range, using a supplied
   * random number generator.
   *
   * @param lower lower random value (inclusive)
   * @param upper upper random value (exclusive)
   * @param generator random number generator */
  private static Tensor setRandom(double lower, double upper, RandomGenerator generator) {
    Distribution distribution = UniformDistribution.of(lower, upper);
    return RandomVariate.of(distribution, 3);
  }
}
