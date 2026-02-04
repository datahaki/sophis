// code by jph
package ch.alpine.sophis.dv;

import java.util.Random;
import java.util.random.RandomGenerator;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.sophis.dv.BarycentricCoordinate;
import ch.alpine.sophis.dv.HsBarycentricCoordinate;
import ch.alpine.sophis.dv.LeveragesGenesis;
import ch.alpine.sophus.bm.BiinvariantMean;
import ch.alpine.sophus.hs.HsAlgebra;
import ch.alpine.sophus.hs.HsBiinvariantMean;
import ch.alpine.sophus.hs.s.SnAlgebra;
import ch.alpine.sophus.math.sample.BallRandomSample;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.RandomSample;
import ch.alpine.tensor.pdf.RandomSampleInterface;
import ch.alpine.tensor.sca.var.InversePowerVariogram;

class HsBarycentricCoordinateTest {
  RandomGenerator randomGenerator = new Random(3);

  @ParameterizedTest
  @ValueSource(ints = { 2, 3, 4 })
  void testSimple(int d) {
    HsAlgebra hsAlgebra = SnAlgebra.of(d);
    RandomSampleInterface randomSampleInterface = BallRandomSample.of(Array.zeros(d), RealScalar.of(0.05));
    BarycentricCoordinate barycentricCoordinate = new HsBarycentricCoordinate(hsAlgebra, new LeveragesGenesis(InversePowerVariogram.of(2)));
    for (int n = d + 1; n < d + 4; ++n) {
      Tensor sequence = RandomSample.of(randomSampleInterface, randomGenerator, n);
      Tensor point = RandomSample.of(randomSampleInterface, randomGenerator);
      Tensor weights = barycentricCoordinate.weights(sequence, point);
      BiinvariantMean biinvariantMean = HsBiinvariantMean.of(hsAlgebra);
      Tensor hsmean = biinvariantMean.mean(sequence, weights);
      Tolerance.CHOP.requireClose(point, hsmean);
    }
  }
}
