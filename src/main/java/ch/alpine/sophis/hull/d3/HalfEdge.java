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

/** Represents the half-edges that surround each
 * face in a counter-clockwise direction.
 *
 * @author John E. Lloyd, Fall 2004 */
class HalfEdge {
  /** The vertex associated with the head of this half-edge. */
  private final Vertex vertex;
  /** Next half-edge in the triangle. */
  private HalfEdge next;
  /** Previous half-edge in the triangle. */
  private HalfEdge prev;
  /** Triangular face associated with this half-edge. */
  Face face;
  /** Half-edge associated with the opposite triangle
   * adjacent to this edge. */
  HalfEdge opposite;

  /** Constructs a HalfEdge with head vertex <code>v</code> and
   * left-hand triangular face <code>f</code>.
   *
   * @param v head vertex
   * @param f left-hand triangular face */
  public HalfEdge(Vertex v, Face f) {
    vertex = v;
    face = f;
  }

  /** Gets the value of the next edge adjacent
   * (counter-clockwise) to this one within the triangle.
   *
   * @return next adjacent edge */
  public HalfEdge next() {
    return next;
  }

  public void next(HalfEdge next) {
    this.next = next;
  }

  /** Gets the value of the previous edge adjacent (clockwise) to
   * this one within the triangle.
   *
   * @return previous adjacent edge */
  public HalfEdge prev() {
    return prev;
  }

  public void prev(HalfEdge prev) {
    this.prev = prev;
  }

  /** Returns the triangular face located to the left of this
   * half-edge.
   *
   * @return left-hand triangular face */
  public Face getFace() {
    return face;
  }

  /** Returns the half-edge opposite to this half-edge.
   *
   * @return opposite half-edge */
  public HalfEdge getOpposite() {
    return opposite;
  }

  /** Sets the half-edge opposite to this half-edge.
   *
   * @param edge opposite half-edge */
  public void setOpposite(HalfEdge edge) {
    opposite = edge;
    edge.opposite = this;
  }

  /** Returns the head vertex associated with this half-edge.
   *
   * @return head vertex */
  public Vertex head() {
    return vertex;
  }

  /** Returns the tail vertex associated with this half-edge.
   *
   * @return tail vertex */
  public Vertex tail() {
    return prev.vertex;
  }

  /** Returns the opposite triangular face associated with this
   * half-edge.
   *
   * @return opposite triangular face */
  public Face oppositeFace() {
    return opposite.face;
  }

  /** Produces a string identifying this half-edge by the point
   * index values of its tail and head vertices.
   *
   * @return identifying string */
  public String getVertexString() {
    if (tail() != null)
      return "" + tail().index + "-" + head().index;
    return "?-" + head().index;
  }
}
