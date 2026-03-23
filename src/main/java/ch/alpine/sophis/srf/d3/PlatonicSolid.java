// code by jph
package ch.alpine.sophis.srf.d3;

import ch.alpine.sophis.hull.d3.ConvexHull3D;
import ch.alpine.sophis.srf.SurfaceMesh;
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
  private final Tensor vertices = Import.of("ch/alpine/sophis/srf/d3/" + name().toLowerCase() + ".csv").unmodifiable();

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
  public SurfaceMesh surfaceMesh() {
    return new SurfaceMesh(vertices(), ConvexHull3D.of(vertices));
  }
}
