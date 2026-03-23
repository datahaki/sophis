package ch.alpine.sophis.ref.d2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import ch.alpine.sophis.srf.IntDirectedEdge;
import ch.alpine.sophis.srf.SurfaceMesh;
import ch.alpine.sophis.win.UniformWindowSampler;
import ch.alpine.sophus.bm.BiinvariantMean;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.sca.win.DirichletWindow;

public abstract class LinearRefinement implements SurfaceMeshRefinement, Serializable {
  protected static final Function<Integer, Tensor> WEIGHTS = UniformWindowSampler.of(DirichletWindow.FUNCTION);
  protected final BiinvariantMean biinvariantMean;

  protected LinearRefinement(BiinvariantMean biinvariantMean) {
    this.biinvariantMean = biinvariantMean;
  }

  @Override // from SurfaceMeshRefinement
  public final SurfaceMesh refine(SurfaceMesh surfaceMesh) {
    SurfaceMesh out = new SurfaceMesh();
    // insert all vertices into output mesh
    out.vrt = surfaceMesh.vrt.copy(); // interpolation
    // insert all face-midpoints into output mesh
    Map<IntDirectedEdge, Integer> intDirectedEdges = new HashMap<>();
    for (int[] face : surfaceMesh.faces()) {
      int n = face.length;
      List<Integer> list = new ArrayList<>(); // index of edge midpoints
      for (int index = 0; index < n; ++index) {
        IntDirectedEdge intDirectedEdge = new IntDirectedEdge( //
            face[index], //
            face[(index + 1) % n]);
        if (intDirectedEdges.containsKey(intDirectedEdge)) // edge already was subdivided
          list.add(intDirectedEdges.get(intDirectedEdge));
        else {
          Tensor sequence = surfaceMesh.polygon_face(intDirectedEdge.array());
          Tensor midpoint = biinvariantMean.mean(sequence, WEIGHTS.apply(sequence.length()));
          int v_index = out.addVert(midpoint);
          intDirectedEdges.put(intDirectedEdge.reverse(), v_index);
          list.add(v_index);
        }
      }
      Integers.requireEquals(list.size(), n);
      handle(surfaceMesh, face, list, out);
    }
    return out;
  }

  protected abstract void handle(SurfaceMesh surfaceMesh, int[] face, List<Integer> list, SurfaceMesh out);
}
