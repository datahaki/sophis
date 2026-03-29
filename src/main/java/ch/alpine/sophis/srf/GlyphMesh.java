// code by jph
package ch.alpine.sophis.srf;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;

import ch.alpine.tensor.Tensors;

public enum GlyphMesh {
  ;
  /** @param shape
   * @return */
  public static SurfaceMesh of(Shape shape) {
    Integer prev = null;
    Integer next = null;
    Integer stol = null;
    Integer last = null;
    PathIterator pathIterator = shape.getPathIterator(new AffineTransform(1, 0, 0, -1, 0, 0));
    SurfaceMesh surfaceMesh = new SurfaceMesh();
    double[] coords = new double[6];
    while (!pathIterator.isDone()) {
      switch (pathIterator.currentSegment(coords)) {
      case PathIterator.SEG_MOVETO:
        prev = surfaceMesh.addVert(Tensors.vectorDouble(coords[0], coords[1]));
        break;
      case PathIterator.SEG_LINETO: {
        next = surfaceMesh.addVert(Tensors.vectorDouble(coords[0], coords[1]));
        surfaceMesh.addFace(prev, next);
        prev = next;
        break;
      }
      case PathIterator.SEG_QUADTO: {
        next = surfaceMesh.addVert(Tensors.vectorDouble(coords[0], coords[1]));
        last = surfaceMesh.addVert(Tensors.vectorDouble(coords[2], coords[3]));
        surfaceMesh.addFace(prev, next, last);
        prev = last;
        break;
      }
      case PathIterator.SEG_CUBICTO: {
        next = surfaceMesh.addVert(Tensors.vectorDouble(coords[0], coords[1]));
        stol = surfaceMesh.addVert(Tensors.vectorDouble(coords[2], coords[3]));
        last = surfaceMesh.addVert(Tensors.vectorDouble(coords[4], coords[5]));
        surfaceMesh.addFace(prev, next, stol, last);
        prev = last;
        break;
      }
      case PathIterator.SEG_CLOSE:
        prev = null;
        break;
      }
      pathIterator.next();
    }
    return surfaceMesh;
  }
}
