package ch.alpine.sophis.dv;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.util.Random;

import org.junit.jupiter.api.Test;

import ch.alpine.sophis.dv.HsBarycentricCoordinate;
import ch.alpine.sophis.dv.LeveragesGenesis;
import ch.alpine.sophus.bm.BiinvariantMean;
import ch.alpine.sophus.hs.HsAlgebra;
import ch.alpine.sophus.hs.HsBiinvariantMean;
import ch.alpine.sophus.hs.gr.GrAlgebra;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.spa.SparseArray;

class LeveragesGenesisTest {
  @Test
  void testSimple() {
    Random randomGenerator = new Random(2);
    HsAlgebra hsAlgebra = GrAlgebra.of(5, 2, 6);
    assertInstanceOf(SparseArray.class, hsAlgebra.ad());
    int n = 6;
    assertEquals(hsAlgebra.dimM(), n);
    assertEquals(hsAlgebra.dimH(), 1 + 3);
    Distribution distribution = UniformDistribution.of(-0.05, 0.05);
    HsBarycentricCoordinate hsBarycentricCoordinate = new HsBarycentricCoordinate(hsAlgebra, LeveragesGenesis.DEFAULT);
    Tensor sequence = RandomVariate.of(distribution, randomGenerator, n + 2, n);
    Tensor x = RandomVariate.of(distribution, randomGenerator, n);
    Tensor weights = hsBarycentricCoordinate.weights(sequence, x);
    BiinvariantMean biinvariantMean = HsBiinvariantMean.of(hsAlgebra);
    Tensor mean = biinvariantMean.mean(sequence, weights);
    Tolerance.CHOP.requireClose(x, mean);
  }
}
