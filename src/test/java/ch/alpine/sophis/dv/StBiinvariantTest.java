package ch.alpine.sophis.dv;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import ch.alpine.sophis.dv.HsBarycentricCoordinate;
import ch.alpine.sophis.dv.LeveragesGenesis;
import ch.alpine.sophus.bm.BiinvariantMean;
import ch.alpine.sophus.hs.HsAlgebra;
import ch.alpine.sophus.hs.HsBiinvariantMean;
import ch.alpine.sophus.hs.st.StAlgebra;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.TriangularDistribution;

class StBiinvariantTest {
  @Test
  void test5x2Bary() {
    HsAlgebra hsAlgebra = StAlgebra.of(5, 2, 8);
    assertFalse(hsAlgebra.isHTrivial());
    assertEquals(hsAlgebra.dimM(), 7);
    assertEquals(hsAlgebra.dimG(), 10);
    Distribution distribution = TriangularDistribution.with(0, 0.01);
    Tensor sequence = RandomVariate.of(distribution, 12, 7);
    Tensor point = RandomVariate.of(distribution, 7);
    HsBarycentricCoordinate hsBarycentricCoordinate = //
        new HsBarycentricCoordinate(hsAlgebra, LeveragesGenesis.DEFAULT);
    Tensor weights = hsBarycentricCoordinate.weights(sequence, point);
    BiinvariantMean biinvariantMean = HsBiinvariantMean.of(hsAlgebra);
    Tensor mean = biinvariantMean.mean(sequence, weights);
    Tolerance.CHOP.requireClose(point, mean);
  }
}
