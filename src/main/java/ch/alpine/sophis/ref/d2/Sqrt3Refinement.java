// code by jph
package ch.alpine.sophis.ref.d2;

import ch.alpine.sophis.srf.SurfaceMesh;
import ch.alpine.sophus.bm.BiinvariantMean;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.ConstantArray;

public record Sqrt3Refinement(BiinvariantMean biinvariantMean) implements SurfaceMeshRefinement {
  private static final Tensor WEIGHTS = ConstantArray.of(Rational.THIRD, 3);

  @Override
  public SurfaceMesh refine(SurfaceMesh surfaceMesh) {
    SurfaceMesh out = new SurfaceMesh();
    for (int[] face : surfaceMesh.faces()) {
      Tensor sequence = Tensors.of( //
          surfaceMesh.vrt.get(face[0]), //
          surfaceMesh.vrt.get(face[1]), //
          surfaceMesh.vrt.get(face[2]));
      Tensor mean = biinvariantMean.mean(sequence, WEIGHTS);
      out.addVert(mean);
    }
    return out;
  }
}
