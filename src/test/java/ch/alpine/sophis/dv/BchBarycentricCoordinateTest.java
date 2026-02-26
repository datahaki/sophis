// code by jph
package ch.alpine.sophis.dv;

import java.util.Random;
import java.util.random.RandomGenerator;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import ch.alpine.sophus.hs.HsAlgebra;
import ch.alpine.sophus.hs.HsBiinvariantMean;
import ch.alpine.sophus.lie.LieAlgebraMatrixBasis;
import ch.alpine.sophus.lie.MatrixAlgebra;
import ch.alpine.sophus.lie.se2.Se2CoveringGroup;
import ch.alpine.sophus.lie.so.So3Group;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.api.TensorBinaryOperator;
import ch.alpine.tensor.lie.bch.BakerCampbellHausdorff;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.var.InversePowerVariogram;

class BchBarycentricCoordinateTest {
  @Disabled
  @Test
  void testSe2() {
    Distribution distribution = UniformDistribution.of(-0.1, 0.1);
    RandomGenerator randomGenerator = new Random(1);
    Tensor basis = LieAlgebraMatrixBasis.of(Se2CoveringGroup.INSTANCE);
    MatrixAlgebra matrixAlgebra = new MatrixAlgebra(basis);
    TensorBinaryOperator bch = BakerCampbellHausdorff.of(matrixAlgebra.ad(), 10, Tolerance.CHOP);
    Tensor ad = matrixAlgebra.ad();
    HsAlgebra hsAlgebra = new HsAlgebra(ad, ad.length(), 6);
    ScalarUnaryOperator variogram = InversePowerVariogram.of(2.5);
    for (int n = 4; n < 7; ++n) {
      Tensor sequence = RandomVariate.of(distribution, randomGenerator, n, 3);
      Tensor x = RandomVariate.of(distribution, randomGenerator, 3);
      BarycentricCoordinate barycentricCoordinate = //
          new BchBarycentricCoordinate(bch, new LeveragesGenesis(variogram));
      Tensor weights = barycentricCoordinate.weights(sequence, x);
      Tensor mean = HsBiinvariantMean.of(hsAlgebra).mean(sequence, weights);
      Chop._06.requireClose(mean, x);
      // ---
      Tensor seqG = Tensor.of(sequence.stream().map(Se2CoveringGroup.INSTANCE.exponential0()::exp));
      BarycentricCoordinate bc = LeveragesCoordinate.of(Se2CoveringGroup.INSTANCE, variogram);
      Tensor weights2 = bc.weights(seqG, Se2CoveringGroup.INSTANCE.exponential0().exp(x));
      Chop._08.requireClose(weights, weights2);
    }
  }

  @Test
  void testSo3MeanRandom() {
    Distribution distribution = UniformDistribution.of(-0.1, 0.1);
    RandomGenerator randomGenerator = new Random(1);
    Tensor basis = LieAlgebraMatrixBasis.of(So3Group.INSTANCE);
    MatrixAlgebra matrixAlgebra = new MatrixAlgebra(basis);
    Tensor ad = matrixAlgebra.ad();
    TensorBinaryOperator bch = BakerCampbellHausdorff.of(ad, 6);
    HsAlgebra hsAlgebra = new HsAlgebra(ad, ad.length(), 6);
    for (int n = 4; n < 7; ++n) {
      Tensor sequence = RandomVariate.of(distribution, randomGenerator, n, 3);
      Tensor x = RandomVariate.of(distribution, randomGenerator, 3);
      BarycentricCoordinate barycentricCoordinate = //
          new BchBarycentricCoordinate(bch, new LeveragesGenesis(InversePowerVariogram.of(2)));
      Tensor weights = barycentricCoordinate.weights(sequence, x);
      Tensor mean = HsBiinvariantMean.of(hsAlgebra).mean(sequence, weights);
      Tolerance.CHOP.requireClose(mean, x);
    }
  }
}
