package ch.alpine.sophis.dv;

import java.io.IOException;
import java.util.Random;
import java.util.random.RandomGenerator;

import org.junit.jupiter.api.Test;

import ch.alpine.sophis.dv.BarycentricCoordinate;
import ch.alpine.sophus.lie.td.TdGroup;
import ch.alpine.sophus.lie.td.TdRandomSample;
import ch.alpine.sophus.math.AveragingWeights;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.RandomSample;
import ch.alpine.tensor.pdf.RandomSampleInterface;
import ch.alpine.tensor.pdf.c.ExponentialDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.sca.Chop;

class TdBiinvariantTest {
  private static final BarycentricCoordinate AFFINE = AffineWrap.of(TdGroup.INSTANCE);
  private static final BarycentricCoordinate[] BARYCENTRIC_COORDINATES = { //
      // LeveragesCoordinate.slow(DtManifold.INSTANCE, InversePowerVariogram.of(1)), //
      // LeveragesCoordinate.slow(DtManifold.INSTANCE, InversePowerVariogram.of(2)), //
      AFFINE };

  @Test
  void testSimple3() {
    RandomGenerator random = new Random(3);
    for (BarycentricCoordinate barycentricCoordinate : BARYCENTRIC_COORDINATES)
      for (int n = 1; n < 3; ++n)
        for (int length = n + 2; length < n + 8; ++length) {
          int fn = n;
          RandomSampleInterface rsi = new TdRandomSample(UniformDistribution.of(-1, 1), fn, ExponentialDistribution.standard());
          Tensor sequence = RandomSample.of(rsi, random, length);
          Tensor mean1 = RandomSample.of(rsi, random);
          Tensor weights = barycentricCoordinate.weights(sequence, mean1);
          Tensor mean2 = TdGroup.INSTANCE.biinvariantMean().mean(sequence, weights);
          Chop._06.requireClose(mean1, mean2);
          // ---
          Tensor shift = RandomSample.of(rsi, random);
          for (TensorUnaryOperator tensorMapping : BiinvariantCheck.of(TdGroup.INSTANCE, shift)) {
            Tensor all = Tensor.of(sequence.stream().map(tensorMapping));
            Chop._03.requireClose(weights, //
                barycentricCoordinate.weights(all, tensorMapping.apply(mean1)));
          }
        }
  }

  @Test
  void testAffineBiinvariant() throws ClassNotFoundException, IOException {
    RandomGenerator random = new Random(3);
    for (BarycentricCoordinate barycentricCoordinate : BARYCENTRIC_COORDINATES)
      for (int n = 1; n < 3; ++n)
        for (int length = n + 2; length < n + 8; ++length) {
          barycentricCoordinate = Serialization.copy(barycentricCoordinate);
          RandomSampleInterface rsi = new TdRandomSample(UniformDistribution.of(-1, 1), n, ExponentialDistribution.standard());
          Tensor sequence = RandomSample.of(rsi, random, length);
          Tensor mean1 = RandomSample.of(rsi, random);
          Tensor weights = barycentricCoordinate.weights(sequence, mean1);
          Tensor mean2 = TdGroup.INSTANCE.biinvariantMean().mean(sequence, weights);
          Chop._08.requireClose(mean1, mean2); // linear reproduction
          // ---
          Tensor shift = RandomSample.of(rsi, random);
          for (TensorUnaryOperator tensorMapping : BiinvariantCheck.of(TdGroup.INSTANCE, shift)) {
            Tensor all = Tensor.of(sequence.stream().map(tensorMapping));
            Chop._05.requireClose(weights, barycentricCoordinate.weights( //
                all, tensorMapping.apply(mean1)));
          }
        }
  }

  @Test
  void testAffineCenter() throws ClassNotFoundException, IOException {
    BarycentricCoordinate barycentricCoordinate = Serialization.copy(AFFINE);
    for (int n = 1; n < 3; ++n)
      for (int length = n + 2; length < n + 8; ++length) {
        RandomSampleInterface rsi = new TdRandomSample(UniformDistribution.of(-1, 1), n, ExponentialDistribution.standard());
        Tensor sequence = RandomSample.of(rsi, length);
        Tensor constant = AveragingWeights.INSTANCE.origin(sequence);
        Tensor center = TdGroup.INSTANCE.biinvariantMean().mean(sequence, constant);
        Tensor weights = barycentricCoordinate.weights(sequence, center);
        Tolerance.CHOP.requireClose(weights, constant);
      }
  }
}
