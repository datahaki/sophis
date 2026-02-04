// code by jph
package ch.alpine.sophis.ref.d2;

import ch.alpine.sophis.srf.SurfaceMesh;

@FunctionalInterface
public interface SurfaceMeshRefinement {
  /** @param surfaceMesh
   * @return */
  SurfaceMesh refine(SurfaceMesh surfaceMesh);
}
