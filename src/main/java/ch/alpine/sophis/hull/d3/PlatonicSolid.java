// code by jph
package ch.alpine.sophis.hull.d3;

import java.util.List;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.io.Import;

/** https://mathworld.wolfram.com/PlatonicSolid.html */
public enum PlatonicSolid {
  TETRAHEDRON(4, 3),
  CUBE(6, 4),
  OCTAHEDRON(8, 3),
  DODECAHEDRON(12, 5),
  ICOSAHEDRON(20, 3);

  private final int faceCount;
  private final int faceShape;
  private final Tensor vertices = Import.of("ch/alpine/qhull3/platonic/" + name().toLowerCase() + ".csv").unmodifiable();

  PlatonicSolid(int faceCount, int faceShape) {
    this.faceCount = faceCount;
    this.faceShape = faceShape;
  }

  int faceSize() {
    return faceCount;
  }

  public int faceShape() {
    return faceShape;
  }

  public Tensor vertices() {
    return vertices.copy();
  }

  /** @return faceSize x faceShape */
  public List<int[]> faces() {
    return ConvexHull3D.of(vertices);
  }
}
