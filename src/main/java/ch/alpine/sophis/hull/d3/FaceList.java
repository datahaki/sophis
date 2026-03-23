// code by John E. Lloyd
package ch.alpine.sophis.hull.d3;

/** Maintains a single-linked list of faces for use by QuickHull3D */
class FaceList {
  private Face head;
  private Face tail;

  /** Adds a vertex to the end of this list. */
  public void add(Face vtx) {
    if (head == null)
      head = vtx;
    else
      tail.next = vtx;
    vtx.next = null;
    tail = vtx;
  }

  public Face head() {
    return head;
  }
}
