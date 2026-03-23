/* Copyright John E. Lloyd, 2003. All rights reserved. Permission
 * to use, copy, and modify, without fee, is granted for non-commercial
 * and research purposes, provided that this copyright notice appears
 * in all copies.
 *
 * This software is distributed "as is", without any warranty, including
 * any implied warranty of merchantability or fitness for a particular
 * use. The authors assume no responsibility for, and shall not be liable
 * for, any special, indirect, or consequential damages, or any damages
 * whatsoever, arising out of or in connection with the use of this
 * software. */
package ch.alpine.sophis.hull.d3;

import java.util.ArrayList;
import java.util.List;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.lie.rot.Cross;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.sca.Abs;

/** Basic triangular face used to form the hull.
 *
 * <p>The information stored for each face consists of a planar
 * normal, a planar offset, and a doubly-linked list of three <a
 * href=HalfEdge>HalfEdges</a> which surround the face in a
 * counter-clockwise direction.
 *
 * @author John E. Lloyd, Fall 2004 */
class Face {
  static final int VISIBLE = 1;
  static final int NON_CONVEX = 2;
  static final int DELETED = 3;

  /** Constructs a triangule Face from vertices v0, v1, and v2.
   *
   * @param v0 first vertex
   * @param v1 second vertex
   * @param v2 third vertex */
  public static Face createTriangle(Vertex v0, Vertex v1, Vertex v2) {
    Face face = new Face();
    HalfEdge he0 = new HalfEdge(v0, face);
    HalfEdge he1 = new HalfEdge(v1, face);
    HalfEdge he2 = new HalfEdge(v2, face);
    he0.prev(he2);
    he0.next(he1);
    he1.prev(he0);
    he1.next(he2);
    he2.prev(he1);
    he2.next(he0);
    face.he0 = he0;
    // compute the normal and offset
    face.computeNormalAndCentroid();
    return face;
  }

  // ---
  private Tensor normal;
  private Tensor centroid;
  HalfEdge he0;
  Scalar area = RealScalar.ZERO;
  private Scalar planeOffset = RealScalar.ZERO;
  private int numVerts;
  Face next;
  int mark = VISIBLE;
  Vertex outside;

  private void computeNormalAndCentroid() {
    computeNormal();
    computeCentroid();
    planeOffset = (Scalar) normal.dot(centroid);
  }

  private void computeCentroid() {
    centroid = Tensors.vector(0, 0, 0);
    HalfEdge he = he0;
    do {
      centroid = centroid.add(he.head().pnt);
      he = he.next();
    } while (he != he0);
    centroid = centroid.multiply(RealScalar.of(numVerts).reciprocal());
  }

  private void computeNormal() {
    HalfEdge he1 = he0.next();
    HalfEdge he2 = he1.next();
    Tensor p0 = he0.head().pnt.copy();
    Tensor p2 = he1.head().pnt.copy();
    Tensor d2 = p2.subtract(p0);
    normal = Tensors.vector(0, 0, 0);
    numVerts = 2;
    while (he2 != he0) {
      Tensor d1 = d2.copy();
      p2 = he2.head().pnt;
      d2 = p2.subtract(p0);
      normal = normal.add(Cross.of(d1, d2));
      he2 = he2.next();
      ++numVerts;
    }
    area = Vector2Norm.of(normal);
    normal = normal.multiply(area.reciprocal());
  }

  private void updateNormalAndCentroid() {
    computeNormalAndCentroid();
    int numv = 0;
    HalfEdge he = he0;
    do {
      numv++;
      he = he.next();
    } while (he != he0);
    if (numv != numVerts)
      throw new RuntimeException("face " + getVertexString() + " numVerts=" + numVerts + " should be " + numv);
  }

  /** Gets the i-th half-edge associated with the face.
   * 
   * @param i the half-edge index
   * @return the half-edge */
  public HalfEdge getEdge(int i) {
    HalfEdge he = he0;
    while (i > 0) {
      he = he.next();
      i--;
    }
    while (i < 0) {
      he = he.prev();
      i++;
    }
    return he;
  }

  /** Computes the distance from a point p to the plane of
   * this face.
   *
   * @param p the point
   * @return distance from the point to the plane */
  public Scalar distanceToPlane(Tensor p) {
    return (Scalar) normal.dot(p).subtract(planeOffset);
  }

  public Tensor centroid() {
    return centroid.copy();
  }

  private int numVertices() {
    return numVerts;
  }

  public String getVertexString() {
    String s = null;
    HalfEdge he = he0;
    do {
      if (s == null) {
        s = "" + he.head().index;
      } else {
        s += " " + he.head().index;
      }
      he = he.next();
    } while (he != he0);
    return s;
  }

  private Face connectHalfEdges(HalfEdge hedgePrev, HalfEdge hedge) {
    Face discardedFace = null;
    if (hedgePrev.oppositeFace() == hedge.oppositeFace()) { // then there is a redundant edge that we can get rid off
      Face oppFace = hedge.oppositeFace();
      HalfEdge hedgeOpp;
      if (hedgePrev == he0) {
        he0 = hedge;
      }
      if (oppFace.numVertices() == 3) { // then we can get rid of the opposite face altogether
        hedgeOpp = hedge.getOpposite().prev().getOpposite();
        oppFace.mark = DELETED;
        discardedFace = oppFace;
      } else {
        hedgeOpp = hedge.getOpposite().next();
        if (oppFace.he0 == hedgeOpp.prev()) {
          oppFace.he0 = hedgeOpp;
        }
        hedgeOpp.prev(hedgeOpp.prev().prev());
        hedgeOpp.prev().next(hedgeOpp);
      }
      hedge.prev(hedgePrev.prev());
      hedge.prev().next(hedge);
      hedge.opposite = hedgeOpp;
      hedgeOpp.opposite = hedge;
      // oppFace was modified, so need to recompute
      oppFace.updateNormalAndCentroid();
    } else {
      hedgePrev.next(hedge);
      hedge.prev(hedgePrev);
    }
    return discardedFace;
  }

  /** sanity check on the face */
  void checkConsistency() {
    HalfEdge hedge = he0;
    Scalar maxd = RealScalar.ZERO;
    int numv = 0;
    if (numVerts < 3)
      throw new RuntimeException("degenerate face: " + getVertexString());
    do {
      HalfEdge hedgeOpp = hedge.getOpposite();
      if (hedgeOpp == null)
        throw new RuntimeException("face " + getVertexString() + ": " + "unreflected half edge " + hedge.getVertexString());
      else //
      if (hedgeOpp.getOpposite() != hedge)
        throw new RuntimeException("face " + getVertexString() + ": " + "opposite half edge " + hedgeOpp.getVertexString() + " has opposite "
            + hedgeOpp.getOpposite().getVertexString());
      if (hedgeOpp.head() != hedge.tail() || hedge.head() != hedgeOpp.tail())
        // jan experienced this exception for cuboid
        throw new RuntimeException("face " + getVertexString() + ": " + "half edge " + hedge.getVertexString() + " reflected by " + hedgeOpp.getVertexString());
      Face oppFace = hedgeOpp.face;
      if (oppFace == null)
        throw new RuntimeException("face " + getVertexString() + ": " + "no face on half edge " + hedgeOpp.getVertexString());
      else //
      if (oppFace.mark == DELETED)
        throw new RuntimeException("face " + getVertexString() + ": " + "opposite face " + oppFace.getVertexString() + " not on hull");
      Scalar d = Abs.FUNCTION.apply(distanceToPlane(hedge.head().pnt));
      if (Scalars.lessThan(maxd, d))
        maxd = d;
      numv++;
      hedge = hedge.next();
    } while (hedge != he0);
    if (numv != numVerts)
      throw new RuntimeException("face " + getVertexString() + " numVerts=" + numVerts + " should be " + numv);
  }

  public List<Face> mergeAdjacentFace(HalfEdge hedgeAdj) {
    List<Face> discarded = new ArrayList<>(3);
    Face oppFace = hedgeAdj.oppositeFace();
    discarded.add(oppFace);
    oppFace.mark = DELETED;
    HalfEdge hedgeOpp = hedgeAdj.getOpposite();
    HalfEdge hedgeAdjPrev = hedgeAdj.prev();
    HalfEdge hedgeAdjNext = hedgeAdj.next();
    HalfEdge hedgeOppPrev = hedgeOpp.prev();
    HalfEdge hedgeOppNext = hedgeOpp.next();
    while (hedgeAdjPrev.oppositeFace() == oppFace) {
      hedgeAdjPrev = hedgeAdjPrev.prev();
      hedgeOppNext = hedgeOppNext.next();
    }
    while (hedgeAdjNext.oppositeFace() == oppFace) {
      hedgeOppPrev = hedgeOppPrev.prev();
      hedgeAdjNext = hedgeAdjNext.next();
    }
    HalfEdge hedge;
    for (hedge = hedgeOppNext; hedge != hedgeOppPrev.next(); hedge = hedge.next()) {
      hedge.face = this;
    }
    if (hedgeAdj == he0) {
      he0 = hedgeAdjNext;
    }
    // handle the half edges at the head
    Face discardedFace;
    discardedFace = connectHalfEdges(hedgeOppPrev, hedgeAdjNext);
    if (discardedFace != null)
      discarded.add(discardedFace);
    // handle the half edges at the tail
    discardedFace = connectHalfEdges(hedgeAdjPrev, hedgeOppNext);
    if (discardedFace != null)
      discarded.add(discardedFace);
    updateNormalAndCentroid();
    checkConsistency();
    return discarded;
  }

  int[] getIndices() {
    int[] indices = new int[numVertices()];
    HalfEdge hedge = he0;
    int k = 0;
    do {
      indices[k++] = hedge.head().index;
      hedge = hedge.next();
    } while (hedge != he0);
    return indices;
  }
}
