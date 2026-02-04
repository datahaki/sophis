// code by jph
package ch.alpine.sophis.ref.d1;

import java.util.Objects;

import ch.alpine.sophus.bm.BiinvariantMean;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.itp.BinaryAverage;

public class MSpline4CurveSubdivision extends Dual3PointCurveSubdivision {
  private static final Tensor MASK_LO = Tensors.vector(5, 10, 1).divide(RealScalar.of(16));
  private static final Tensor MASK_HI = Tensors.vector(1, 10, 5).divide(RealScalar.of(16));

  /** @param biinvariantMean
   * @return */
  public static CurveSubdivision of(BiinvariantMean biinvariantMean) {
    BinaryAverage binaryAverage = (p, q, scalar) -> //
    biinvariantMean.mean(Unprotect.byRef(p, q), Tensors.of(RealScalar.ONE.subtract(scalar), scalar));
    return new MSpline4CurveSubdivision(binaryAverage, biinvariantMean);
  }

  // ---
  private final BiinvariantMean biinvariantMean;

  private MSpline4CurveSubdivision(BinaryAverage binaryAverage, BiinvariantMean biinvariantMean) {
    super(binaryAverage);
    this.biinvariantMean = Objects.requireNonNull(biinvariantMean);
  }

  @Override
  public Tensor lo(Tensor p, Tensor q, Tensor r) {
    return biinvariantMean.mean(Unprotect.byRef(p, q, r), MASK_LO);
  }

  @Override
  public Tensor hi(Tensor p, Tensor q, Tensor r) {
    return biinvariantMean.mean(Unprotect.byRef(p, q, r), MASK_HI);
  }
}
