// code by jph
package ch.alpine.sophis.dv;

import ch.alpine.sophus.api.Manifold;
import ch.alpine.sophus.hs.gr.GrManifold;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.io.MathematicaFormat;

/** bi-invariant
 * results in a symmetric distance matrix -> can use for kriging and minimum spanning tree */
/* package */ class CupolaBiinvariant extends MatrixBiinvariant {
  public CupolaBiinvariant(Manifold manifold) {
    super(manifold);
  }

  @Override // from TensorMetric
  public Scalar distance(Tensor p, Tensor q) {
    return GrManifold.INSTANCE.distance(p, q);
  }

  @Override
  public String toString() {
    return MathematicaFormat.concise("Cupola", manifold);
  }
}
