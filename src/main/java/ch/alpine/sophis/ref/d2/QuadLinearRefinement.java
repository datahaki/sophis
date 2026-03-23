// code by jph
package ch.alpine.sophis.ref.d2;

import java.util.List;

import ch.alpine.sophis.srf.SurfaceMesh;
import ch.alpine.sophus.bm.BiinvariantMean;

/** each n-face is refined into n quads using an inserted face midpoint and edge midpoints
 * 
 * In particular a triangle is refined into 3 quads. therefore, this method is different
 * from {@link TriQuadLinearRefinement} */
public class QuadLinearRefinement extends LinearRefinement {
  public QuadLinearRefinement(BiinvariantMean biinvariantMean) {
    super(biinvariantMean);
  }

  @Override
  protected void handle(SurfaceMesh surfaceMesh, int[] face, List<Integer> list, SurfaceMesh out) {
    int n = face.length;
    // here, list contains face.length entries
    if (2 < n) {
      // add quad consisting of old face vertex, two edge midpoints, and the face midpoint
      int nV = out.addVert(biinvariantMean.mean(surfaceMesh.polygon_face(face), WEIGHTS.apply(n)));
      for (int index = 0; index < n; ++index)
        out.addFace( //
            face[index], //
            list.get(index), //
            nV, //
            list.get(Math.floorMod(index - 1, n)));
    }
  }
}
