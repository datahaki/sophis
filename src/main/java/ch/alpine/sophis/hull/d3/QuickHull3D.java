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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.lie.rot.Cross;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.nrm.Vector2NormSquared;
import ch.alpine.tensor.red.Entrywise;
import ch.alpine.tensor.red.Total;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.pow.Sqrt;

/** Computes the convex hull of a set of three dimensional points.
 *
 * <p>The algorithm is a three dimensional implementation of Quickhull, as
 * described in Barber, Dobkin, and Huhdanpaa, <a
 * href=http://citeseer.ist.psu.edu/barber96quickhull.html> ``The Quickhull
 * Algorithm for Convex Hulls''</a> (ACM Transactions on Mathematical Software,
 * Vol. 22, No. 4, December 1996), and has a complexity of O(n log(n)) with
 * respect to the number of points. A well-known C implementation of Quickhull
 * that works for arbitrary dimensions is provided by <a
 * href=http://www.qhull.org>qhull</a>.
 *
 * <p>A hull is constructed by providing a set of points
 * to the {@link #buildHull()} method.
 * After the hull is built, its faces can be retrieved
 * using {@link #getFaces()}
 * 
 * <h3><a name=distTol>Robustness</h3> Because this algorithm uses floating
 * point arithmetic, it is potentially vulnerable to errors arising from
 * numerical imprecision. We address this problem in the same way as <a
 * href=http://www.qhull.org>qhull</a>, by merging faces whose edges are not
 * clearly convex. A face is convex if its edges are convex, and an edge is
 * convex if the centroid of each adjacent plane is clearly <i>below</i> the
 * plane of the other face. The centroid is considered below a plane if its
 * distance to the plane is less than the negative of a {@link
 * #getDistanceTolerance() distance tolerance}. This tolerance represents the
 * smallest distance that can be reliably computed within the available numeric
 * precision. It is normally computed automatically from the point data,
 * although an application may {@link #setExplicitDistanceTolerance set this
 * tolerance explicitly}.
 *
 * <p>Numerical problems are more likely to arise in situations where data
 * points lie on or within the faces or edges of the convex hull. We have
 * tested QuickHull3D for such situations by computing the convex hull of a
 * random point set, then adding additional randomly chosen points which lie
 * very close to the hull vertices and edges, and computing the convex
 * hull again. The hull is deemed correct if {@link #check check} returns
 * <code>true</code>. These tests have been successful for a large number of
 * trials and so we are confident that QuickHull3D is reasonably robust.
 *
 * <h3>Merged Faces</h3> The merging of faces means that the faces returned by
 * QuickHull3D may be convex polygons instead of triangles. If triangles are
 * desired, the application may the faces, but
 * it should be noted that this may result in triangles which are very small or
 * thin and hence difficult to perform reliable convexity tests on. In other
 * words, triangulating a merged face is likely to restore the numerical
 * problems which the merging process removed. Hence is it
 * possible that, after triangulation, {@link #check check} will fail (the same
 * behavior is observed with triangulated output from <a
 * href=http://www.qhull.org>qhull</a>).
 *
 * <h3>Degenerate Input</h3>It is assumed that the input points
 * are non-degenerate in that they are not coincident, colinear, or
 * colplanar, and thus the convex hull has a non-zero volume.
 * If the input points are detected to be degenerate within
 * the {@link #getDistanceTolerance() distance tolerance}, an
 * IllegalArgumentException will be thrown.
 *
 * @author John E. Lloyd, Fall 2004 */
class QuickHull3D {
  /** Specifies that the distance tolerance should be
   * computed automatically from the input point data. */
  public static final Scalar AUTOMATIC_TOLERANCE = RealScalar.ONE.negate();
  // ---
  private final Vertex[] pointBuffer;
  private final Vertex[] maxVtxs = new Vertex[3];
  private final Vertex[] minVtxs = new Vertex[3];
  private final List<Face> faces = new ArrayList<>();
  private final VertexList unclaimed = new VertexList();
  private final VertexList claimed = new VertexList();
  private boolean debug = false;
  private Scalar explicitTolerance = AUTOMATIC_TOLERANCE;
  private Scalar tolerance = RealScalar.ZERO;

  /** Constructs the convex hull of a set of points whose
   * coordinates are given by an array of doubles.
   *
   * @param coords x, y, and z coordinates of each input
   * point. The length of this array will be three times
   * the number of input points.
   * @throws IllegalArgumentException the number of input points is less
   * than four, or the points appear to be coincident, colinear, or
   * coplanar. */
  public QuickHull3D(Tensor coords) throws IllegalArgumentException {
    int nump = coords.length();
    if (nump < 4)
      throw new IllegalArgumentException("Less than four input points specified");
    // ---
    pointBuffer = new Vertex[nump];
    for (int index = 0; index < nump; ++index) {
      Vertex vertex = new Vertex(index, coords.get(index));
      pointBuffer[index] = vertex;
    }
  }

  public void buildHull() {
    int cnt = 0;
    computeMaxAndMin();
    createInitialSimplex();
    while (!claimed.isEmpty()) {
      Vertex eyeVtx = nextPointToAdd();
      addPointToHull(eyeVtx);
      cnt++;
      if (debug)
        System.out.println("iteration " + cnt + " done");
    }
    /* remove inactive faces and mark active vertices */
    faces.removeIf(face -> face.mark != Face.VISIBLE);
    if (debug)
      System.out.println("hull done");
  }

  /** Returns true if debugging is enabled.
   *
   * @return true is debugging is enabled
   * @see QuickHull3D#setDebug */
  public boolean getDebug() {
    return debug;
  }

  /** Enables the printing of debugging diagnostics.
   *
   * @param enable if true, enables debugging */
  public void setDebug(boolean enable) {
    debug = enable;
  }

  /** Returns the distance tolerance that was used for the most recently
   * computed hull. The distance tolerance is used to determine when
   * faces are unambiguously convex with respect to each other, and when
   * points are unambiguously above or below a face plane, in the
   * presence of <a href=#distTol>numerical imprecision</a>. Normally,
   * this tolerance is computed automatically for each set of input
   * points, but it can be set explicitly by the application.
   *
   * @return distance tolerance
   * @see QuickHull3D#setExplicitDistanceTolerance */
  public Scalar getDistanceTolerance() {
    return tolerance;
  }

  /** Sets an explicit distance tolerance for convexity tests.
   * If {@link #AUTOMATIC_TOLERANCE AUTOMATIC_TOLERANCE}
   * is specified (the default), then the tolerance will be computed
   * automatically from the point data.
   *
   * @param tol explicit tolerance
   * @see #getDistanceTolerance */
  public void setExplicitDistanceTolerance(Scalar tol) {
    explicitTolerance = tol;
  }

  /** Returns the explicit distance tolerance.
   *
   * @return explicit tolerance
   * @see #setExplicitDistanceTolerance */
  public Scalar getExplicitDistanceTolerance() {
    return explicitTolerance;
  }

  private void addPointToFace(Vertex vtx, Face face) {
    vtx.face = face;
    if (face.outside == null)
      claimed.add(vtx);
    else
      claimed.insertBefore(vtx, face.outside);
    face.outside = vtx;
  }

  private void removePointFromFace(Vertex vtx, Face face) {
    if (vtx == face.outside)
      if (vtx.next != null && vtx.next.face == face)
        face.outside = vtx.next;
      else
        face.outside = null;
    claimed.delete(vtx);
  }

  private Vertex removeAllPointsFromFace(Face face) {
    if (face.outside != null) {
      Vertex end = face.outside;
      while (end.next != null && end.next.face == face)
        end = end.next;
      claimed.delete(face.outside, end);
      end.next = null;
      return face.outside;
    }
    return null;
  }

  private void computeMaxAndMin() {
    // CoordinateBounds.of(null, null);
    for (int i = 0; i < 3; i++)
      maxVtxs[i] = minVtxs[i] = pointBuffer[0];
    Tensor max = pointBuffer[0].pnt.copy(); // TODO obsolete, can use maxVtxs instead
    Tensor min = pointBuffer[0].pnt.copy(); // etc ...
    for (int i = 1; i < numPoints(); i++) {
      Tensor pnt = pointBuffer[i].pnt;
      if (Scalars.lessThan(max.Get(0), pnt.Get(0))) {
        max.set(pnt.Get(0), 0);
        maxVtxs[0] = pointBuffer[i];
      } else if (Scalars.lessThan(pnt.Get(0), min.Get(0))) {
        min.set(pnt.Get(0), 0);
        minVtxs[0] = pointBuffer[i];
      }
      if (Scalars.lessThan(max.Get(1), pnt.Get(1))) {
        max.set(pnt.Get(1), 1);
        maxVtxs[1] = pointBuffer[i];
      } else if (Scalars.lessThan(pnt.Get(1), min.Get(1))) {
        min.set(pnt.Get(1), 1);
        minVtxs[1] = pointBuffer[i];
      }
      if (Scalars.lessThan(max.Get(2), pnt.Get(2))) {
        max.set(pnt.Get(2), 2);
        maxVtxs[2] = pointBuffer[i];
      } else if (Scalars.lessThan(pnt.Get(2), min.Get(2))) {
        min.set(pnt.Get(2), 2);
        minVtxs[2] = pointBuffer[i];
      }
    }
    // this epsilon formula comes from QuickHull, and I'm
    // not about to quibble.
    /** estimated size of the point set */
    // somehow charLength is not used
    // Scalar charLength = (Scalar) max.subtract(min).stream().reduce(Max::of).orElseThrow();
    // Max.of(max.x().subtract(min.x()), max.y().subtract(min.y()));
    // charLength = Math.max(max.z() - min.z(), charLength);
    if (explicitTolerance == AUTOMATIC_TOLERANCE) {
      Tensor mx = max.maps(Abs.FUNCTION);
      Tensor mn = min.maps(Abs.FUNCTION);
      tolerance = Total.ofVector(Entrywise.max().apply(mx, mn)).multiply(StaticHelper._3_PREC);
      // RealScalar.of( //
      // (Max.of(Abs.FUNCTION.apply(max.x()), Abs.FUNCTION.apply(min.x())).number().doubleValue() //
      // + Max.of(Abs.FUNCTION.apply(max.y()), Abs.FUNCTION.apply(min.y())).number().doubleValue() //
      // + Max.of(Abs.FUNCTION.apply(max.z()), Abs.FUNCTION.apply(min.z()))))
    } else {
      tolerance = explicitTolerance;
    }
  }

  /** Creates the initial simplex from which the hull will be built. */
  private void createInitialSimplex() throws IllegalArgumentException {
    Scalar max = RealScalar.ZERO;
    int imax = 0;
    for (int i = 0; i < 3; i++) {
      Scalar diff = maxVtxs[i].pnt.Get(i).subtract(minVtxs[i].pnt.Get(i));
      if (Scalars.lessThan(max, diff)) {
        max = diff;
        imax = i;
      }
    }
    if (Scalars.lessEquals(max, tolerance)) {
      throw new IllegalArgumentException("Input points appear to be coincident");
    }
    Vertex[] vtx = new Vertex[4];
    // set first two vertices to be those with the greatest
    // one dimensional separation
    vtx[0] = maxVtxs[imax];
    vtx[1] = minVtxs[imax];
    // set third vertex to be the vertex farthest from
    // the line between vtx0 and vtx1
    Tensor nrml = Tensors.vector(0, 0, 0);
    Scalar maxSqr = RealScalar.ZERO;
    Tensor u01 = vtx[1].pnt.subtract(vtx[0].pnt);
    u01 = Vector2Norm.NORMALIZE.apply(u01);
    for (int i = 0; i < numPoints(); i++) {
      Tensor diff02 = pointBuffer[i].pnt.subtract(vtx[0].pnt);
      Tensor xprod = Cross.of(u01, diff02); // cross(u01, diff02);
      Scalar lenSqr = Vector2NormSquared.of(xprod);
      if (Scalars.lessThan(maxSqr, lenSqr) && //
          pointBuffer[i] != vtx[0] && // paranoid
          pointBuffer[i] != vtx[1]) {
        maxSqr = lenSqr;
        vtx[2] = pointBuffer[i];
        nrml = xprod.copy();
      }
    }
    if (Scalars.lessEquals(Sqrt.FUNCTION.apply(maxSqr), tolerance.multiply(RealScalar.of(100)))) {
      throw new IllegalArgumentException("Input points appear to be colinear");
    }
    nrml = Vector2Norm.NORMALIZE.apply(nrml);
    // recompute nrml to make sure it is normal to u10 - otherwise could
    // be errors in case vtx[2] is close to u10
    Tensor res = u01.multiply((Scalar) nrml.dot(u01)); // component of nrml along u01
    nrml = nrml.subtract(res);
    nrml = Vector2Norm.NORMALIZE.apply(nrml);
    Scalar maxDist = RealScalar.ZERO;
    Scalar d0 = (Scalar) vtx[2].pnt.dot(nrml);
    for (int i = 0; i < numPoints(); i++) {
      Scalar dist = Abs.FUNCTION.apply((Scalar) pointBuffer[i].pnt.dot(nrml).subtract(d0));
      if (Scalars.lessThan(maxDist, dist) && pointBuffer[i] != vtx[0] && // paranoid
          pointBuffer[i] != vtx[1] && pointBuffer[i] != vtx[2]) {
        maxDist = dist;
        vtx[3] = pointBuffer[i];
      }
    }
    if (Scalars.lessEquals(Abs.FUNCTION.apply(maxDist), tolerance.multiply(RealScalar.of(100))))
      throw new IllegalArgumentException("Input points appear to be coplanar");
    if (debug) {
      System.out.println("initial vertices:");
      System.out.println(vtx[0].index + ": " + vtx[0].pnt);
      System.out.println(vtx[1].index + ": " + vtx[1].pnt);
      System.out.println(vtx[2].index + ": " + vtx[2].pnt);
      System.out.println(vtx[3].index + ": " + vtx[3].pnt);
    }
    Face[] tris = new Face[4];
    if (Scalars.lessThan((Scalar) vtx[3].pnt.dot(nrml), d0)) {
      tris[0] = Face.createTriangle(vtx[0], vtx[1], vtx[2]);
      tris[1] = Face.createTriangle(vtx[3], vtx[1], vtx[0]);
      tris[2] = Face.createTriangle(vtx[3], vtx[2], vtx[1]);
      tris[3] = Face.createTriangle(vtx[3], vtx[0], vtx[2]);
      for (int i = 0; i < 3; i++) {
        int k = (i + 1) % 3;
        tris[i + 1].getEdge(1).setOpposite(tris[k + 1].getEdge(0));
        tris[i + 1].getEdge(2).setOpposite(tris[0].getEdge(k));
      }
    } else {
      tris[0] = Face.createTriangle(vtx[0], vtx[2], vtx[1]);
      tris[1] = Face.createTriangle(vtx[3], vtx[0], vtx[1]);
      tris[2] = Face.createTriangle(vtx[3], vtx[1], vtx[2]);
      tris[3] = Face.createTriangle(vtx[3], vtx[2], vtx[0]);
      for (int i = 0; i < 3; i++) {
        int k = (i + 1) % 3;
        tris[i + 1].getEdge(0).setOpposite(tris[k + 1].getEdge(1));
        tris[i + 1].getEdge(2).setOpposite(tris[0].getEdge((3 - i) % 3));
      }
    }
    faces.addAll(Arrays.asList(tris));
    for (int i = 0; i < numPoints(); i++) {
      Vertex v = pointBuffer[i];
      if (v == vtx[0] || v == vtx[1] || v == vtx[2] || v == vtx[3])
        continue;
      maxDist = tolerance;
      Face maxFace = null;
      for (int k = 0; k < 4; k++) {
        Scalar dist = tris[k].distanceToPlane(v.pnt);
        if (Scalars.lessThan(maxDist, dist)) {
          maxFace = tris[k];
          maxDist = dist;
        }
      }
      if (maxFace != null)
        addPointToFace(v, maxFace);
    }
  }

  /** Returns the faces associated with this hull.
   *
   * <p>Each face is represented by an integer array which gives the indices of
   * the vertices. The indices are numbered with respect to the input points,
   * are zero-based, and are arranged counter-clockwise.
   *
   * @param indexFlags specifies index characteristics (0 results in the default)
   * @return array of integer arrays, giving the vertex indices for each face. */
  public List<int[]> getFaces() {
    return faces.stream().map(Face::getIndices).toList();
  }

  private void resolveUnclaimedPoints(FaceList newFaces) {
    Vertex vtxNext = unclaimed.first();
    for (Vertex vtx = vtxNext; vtx != null; vtx = vtxNext) {
      vtxNext = vtx.next;
      Scalar maxDist = tolerance;
      Face maxFace = null;
      for (Face newFace = newFaces.head(); newFace != null; newFace = newFace.next) {
        if (newFace.mark == Face.VISIBLE) {
          Scalar dist = newFace.distanceToPlane(vtx.pnt);
          if (Scalars.lessThan(maxDist, dist)) {
            maxDist = dist;
            maxFace = newFace;
          }
          if (maxDist.number().doubleValue() > 1000 * tolerance.number().doubleValue())
            break;
        }
      }
      if (maxFace != null) {
        addPointToFace(vtx, maxFace);
        if (debug) {
          System.out.println("CLAIMED BY " + maxFace.getVertexString());
        }
      } else {
        if (debug) {
          System.out.println("DISCARDED");
        }
      }
    }
  }

  private void deleteFacePoints(Face face, Face absorbingFace) {
    Vertex faceVtxs = removeAllPointsFromFace(face);
    if (faceVtxs != null)
      if (absorbingFace == null)
        unclaimed.addAll(faceVtxs);
      else {
        Vertex vtxNext = faceVtxs;
        for (Vertex vtx = vtxNext; vtx != null; vtx = vtxNext) {
          vtxNext = vtx.next;
          Scalar dist = absorbingFace.distanceToPlane(vtx.pnt);
          if (Scalars.lessThan(tolerance, dist))
            addPointToFace(vtx, absorbingFace);
          else
            unclaimed.add(vtx);
        }
      }
  }

  private static double oppFaceDistance(HalfEdge he) {
    return he.face.distanceToPlane(he.opposite.face.centroid()).number().doubleValue();
  }

  private boolean doAdjacentMerge(Face face, MergeType mergeType) {
    HalfEdge hedge = face.he0;
    boolean convex = true;
    do {
      Face oppFace = hedge.oppositeFace();
      boolean merge = false;
      if (mergeType.equals(MergeType.NONCONVEX)) { // then merge faces if they are definitively non-convex
        if (oppFaceDistance(hedge) > -tolerance.number().doubleValue() || oppFaceDistance(hedge.opposite) > -tolerance.number().doubleValue())
          merge = true;
      } else { // NONCONVEX_WRT_LARGER_FACE
        // merge faces if they are parallel or non-convex
        // wrt to the larger face; otherwise, just mark
        // the face non-convex for the second pass.
        if (Scalars.lessThan(oppFace.area, face.area)) {
          if (oppFaceDistance(hedge) > -tolerance.number().doubleValue()) {
            merge = true;
          } else if (oppFaceDistance(hedge.opposite) > -tolerance.number().doubleValue()) {
            convex = false;
          }
        } else {
          if (oppFaceDistance(hedge.opposite) > -tolerance.number().doubleValue()) {
            merge = true;
          } else if (oppFaceDistance(hedge) > -tolerance.number().doubleValue()) {
            convex = false;
          }
        }
      }
      if (merge) {
        if (debug)
          System.out.println("  merging " + face.getVertexString() + "  and  " + oppFace.getVertexString());
        for (Face discardedFace : face.mergeAdjacentFace(hedge))
          deleteFacePoints(discardedFace, face);
        if (debug)
          System.out.println("  result: " + face.getVertexString());
        return true;
      }
      hedge = hedge.next();
    } while (hedge != face.he0);
    if (!convex)
      face.mark = Face.NON_CONVEX;
    return false;
  }

  private void calculateHorizon(Tensor eyePnt, HalfEdge edge0, Face face, List<HalfEdge> horizon) {
    deleteFacePoints(face, null);
    face.mark = Face.DELETED;
    if (debug)
      System.out.println("  visiting face " + face.getVertexString());
    HalfEdge edge;
    if (edge0 == null) {
      edge0 = face.getEdge(0);
      edge = edge0;
    } else
      edge = edge0.next();
    do {
      Face oppFace = edge.oppositeFace();
      if (oppFace.mark == Face.VISIBLE) {
        if (Scalars.lessThan(tolerance, oppFace.distanceToPlane(eyePnt)))
          calculateHorizon(eyePnt, edge.getOpposite(), oppFace, horizon);
        else {
          horizon.add(edge);
          if (debug)
            System.out.println("  adding horizon edge " + edge.getVertexString());
        }
      }
      edge = edge.next();
    } while (edge != edge0);
  }

  private HalfEdge addAdjoiningFace(Vertex eyeVtx, HalfEdge he) {
    Face face = Face.createTriangle(eyeVtx, he.tail(), he.head());
    faces.add(face);
    face.getEdge(-1).setOpposite(he.getOpposite());
    return face.getEdge(0);
  }

  private FaceList addNewFaces(Vertex eyeVtx, List<HalfEdge> horizon) {
    FaceList newFaces = new FaceList();
    HalfEdge hedgeSidePrev = null;
    HalfEdge hedgeSideBegin = null;
    for (HalfEdge horizonHe : horizon) {
      HalfEdge hedgeSide = addAdjoiningFace(eyeVtx, horizonHe);
      if (debug)
        System.out.println("new face: " + hedgeSide.face.getVertexString());
      if (hedgeSidePrev != null)
        hedgeSide.next().setOpposite(hedgeSidePrev);
      else
        hedgeSideBegin = hedgeSide;
      newFaces.add(hedgeSide.getFace());
      hedgeSidePrev = hedgeSide;
    }
    hedgeSideBegin.next().setOpposite(hedgeSidePrev);
    return newFaces;
  }

  private Vertex nextPointToAdd() {
    Face eyeFace = claimed.first().face;
    Vertex eyeVtx = null;
    Scalar maxDist = RealScalar.ZERO;
    for (Vertex vtx = eyeFace.outside; vtx != null && vtx.face == eyeFace; vtx = vtx.next) {
      Scalar dist = eyeFace.distanceToPlane(vtx.pnt);
      if (Scalars.lessThan(maxDist, dist)) {
        maxDist = dist;
        eyeVtx = vtx;
      }
    }
    return eyeVtx;
  }

  private void addPointToHull(Vertex eyeVtx) {
    List<HalfEdge> horizon = new ArrayList<>(16);
    unclaimed.clear();
    if (debug) {
      System.out.println("Adding point: " + eyeVtx.index);
      System.out.println(" which is " + eyeVtx.face.distanceToPlane(eyeVtx.pnt) + " above face " + eyeVtx.face.getVertexString());
    }
    removePointFromFace(eyeVtx, eyeVtx.face);
    calculateHorizon(eyeVtx.pnt, null, eyeVtx.face, horizon);
    FaceList newFaces = addNewFaces(eyeVtx, horizon);
    // first merge pass ... merge faces which are non-convex
    // as determined by the larger face
    for (Face face = newFaces.head(); face != null; face = face.next)
      if (face.mark == Face.VISIBLE)
        while (doAdjacentMerge(face, MergeType.NONCONVEX_WRT_LARGER_FACE)) {
          // ---
        }
    // second merge pass ... merge faces which are non-convex wrt either face
    for (Face face = newFaces.head(); face != null; face = face.next)
      if (face.mark == Face.NON_CONVEX) {
        face.mark = Face.VISIBLE;
        while (doAdjacentMerge(face, MergeType.NONCONVEX)) {
          // ---
        }
      }
    resolveUnclaimedPoints(newFaces);
  }

  private int numPoints() {
    return pointBuffer.length;
  }

  private static boolean checkFaceConvexity(Face face, Scalar tol, PrintStream ps) {
    double dist;
    HalfEdge he = face.he0;
    do {
      face.checkConsistency();
      // make sure edge is convex
      dist = oppFaceDistance(he);
      if (dist > tol.number().doubleValue()) {
        if (ps != null)
          ps.println("Edge " + he.getVertexString() + " non-convex by " + dist);
        return false;
      }
      dist = oppFaceDistance(he.opposite);
      if (dist > tol.number().doubleValue()) {
        if (ps != null)
          ps.println("Opposite edge " + he.opposite.getVertexString() + " non-convex by " + dist);
        return false;
      }
      if (he.next().oppositeFace() == he.oppositeFace()) {
        if (ps != null)
          ps.println("Redundant vertex " + he.head().index + " in face " + face.getVertexString());
        return false;
      }
      he = he.next();
    } while (he != face.he0);
    return true;
  }

  private boolean checkFaces(Scalar tol, PrintStream ps) {
    for (Face face : faces)
      if (face.mark == Face.VISIBLE)
        if (!checkFaceConvexity(face, tol, ps))
          return false;
    return true;
  }

  /** Checks the correctness of the hull using the distance tolerance
   * returned by {@link QuickHull3D#getDistanceTolerance
   * getDistanceTolerance}; see
   * check
   * check(PrintStream,double)} for details.
   *
   * @param ps print stream for diagnostic messages; may be
   * set to <code>null</code> if no messages are desired.
   * @return true if the hull is valid
   * @see QuickHull3D#check(PrintStream,double) */
  public boolean check(PrintStream ps) {
    Scalar tol = getDistanceTolerance();
    // check to make sure all edges are fully connected
    // and that the edges are convex
    Scalar dist;
    Scalar pointTol = tol.multiply(RealScalar.of(10));
    if (!checkFaces(tolerance, ps))
      return false;
    // check point inclusion
    for (Vertex vertex : pointBuffer)
      for (Face face : faces)
        if (face.mark == Face.VISIBLE) {
          dist = face.distanceToPlane(vertex.pnt);
          if (Scalars.lessThan(pointTol, dist)) {
            if (ps != null)
              ps.println("Point " + vertex.pnt + " " + dist + " above face " + face.getVertexString());
            return false;
          }
        }
    return true;
  }
}
