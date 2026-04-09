// code by jph
package ch.alpine.sophis.dv;

import java.util.Random;
import java.util.random.RandomGenerator;

import org.junit.jupiter.api.Test;

import ch.alpine.sophis.var.InversePowerVariogram;
import ch.alpine.sophus.hs.HsAlgebra;
import ch.alpine.sophus.hs.HsBiinvariantMean;
import ch.alpine.sophus.lie.MatrixAlgebra;
import ch.alpine.sophus.lie.so.So3Group;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorBinaryOperator;
import ch.alpine.tensor.lie.BakerCampbellHausdorff;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;

class BchBarycentricCoordinateTest {
  @Test
  void testSo3MeanRandom() {
    Distribution distribution = UniformDistribution.of(-0.1, 0.1);
    RandomGenerator randomGenerator = new Random(1);
    MatrixAlgebra matrixAlgebra = MatrixAlgebra.of(So3Group.INSTANCE);
    Tensor ad = matrixAlgebra.ad();
    TensorBinaryOperator bch = BakerCampbellHausdorff.of(ad, 6);
    HsAlgebra hsAlgebra = new HsAlgebra(ad, ad.length(), 6);
    for (int n = 4; n < 7; ++n) {
      Tensor sequence = RandomVariate.of(distribution, randomGenerator, n, 3);
      Tensor x = RandomVariate.of(distribution, randomGenerator, 3);
      BarycentricCoordinate barycentricCoordinate = //
          new BchBarycentricCoordinate(bch, new UsanceGenesis(InversePowerVariogram.of(2)));
      Tensor weights = barycentricCoordinate.weights(sequence, x);
      Tensor mean = HsBiinvariantMean.of(hsAlgebra).mean(sequence, weights);
      Tolerance.CHOP.requireClose(mean, x);
    }
  }
}
