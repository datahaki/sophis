// code by John E. Lloyd
package ch.alpine.sophis.hull.d3;

import ch.alpine.tensor.Tensor;

/** Represents vertices of the hull, as well as the points from
 * which it is formed.
 *
 * @author John E. Lloyd, Fall 2004 */
class Vertex {
  /** Spatial point associated with this vertex. */
  final Tensor pnt;
  /** Back index into an array. */
  final int index;
  /** List forward link. */
  Vertex prev;
  /** List backward link. */
  Vertex next;
  /** Current face that this vertex is outside of. */
  Face face;

  public Vertex(int index, Tensor pnt) {
    this.index = index;
    this.pnt = pnt;
  }
}
