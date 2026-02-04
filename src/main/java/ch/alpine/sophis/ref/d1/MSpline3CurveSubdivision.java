// code by jph
package ch.alpine.sophis.ref.d1;

import java.io.Serializable;
import java.util.Objects;

import ch.alpine.sophus.bm.BiinvariantMean;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;

/** cubic B-spline
 * 
 * uses biinvariant mean */
public final class MSpline3CurveSubdivision extends RefiningBSpline3CurveSubdivision implements Serializable {
  private static final Tensor MASK_MIDDLE = Tensors.vector(4, 4).divide(RealScalar.of(8));
  private static final Tensor MASK_CENTER = Tensors.vector(1, 6, 1).divide(RealScalar.of(8));
  // ---
  private final BiinvariantMean biinvariantMean;

  /** @param biinvariantMean */
  public MSpline3CurveSubdivision(BiinvariantMean biinvariantMean) {
    this.biinvariantMean = Objects.requireNonNull(biinvariantMean);
  }

  @Override // from GeodesicSpace
  public Tensor midpoint(Tensor q, Tensor r) {
    return biinvariantMean.mean(Unprotect.byRef(q, r), MASK_MIDDLE);
  }

  @Override // from AbstractBSpline3CurveSubdivision
  protected Tensor center(Tensor p, Tensor q, Tensor r) {
    return biinvariantMean.mean(Unprotect.byRef(p, q, r), MASK_CENTER);
  }
}
