// code by jph
package ch.alpine.sophis.ref.d2;

import java.util.List;

import ch.alpine.sophis.srf.SurfaceMesh;
import ch.alpine.sophus.bm.BiinvariantMean;

public class TriQuadLinearRefinement extends LinearRefinement {
  TriQuadLinearRefinement(BiinvariantMean biinvariantMean) {
    super(biinvariantMean);
  }

  @Override
  protected void handle(SurfaceMesh surfaceMesh, int[] face, List<Integer> list, SurfaceMesh out) {
    int n = face.length;
    if (3 == n) {
      for (int index = 0; index < n; ++index)
        out.addFace( //
            face[index], //
            list.get(index), //
            list.get(Math.floorMod(index - 1, n)));
      out.addFace( //
          list.get(0), list.get(1), list.get(2));
    } else //
    if (3 < n) {
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
