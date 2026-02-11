// code by jph
package ch.alpine.sophis.dv;

import java.io.IOException;
import java.util.Random;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.lie.he.HeGroup;
import ch.alpine.sophus.lie.he.HeRandomSample;
import ch.alpine.sophus.math.AveragingWeights;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.pdf.RandomSample;
import ch.alpine.tensor.pdf.RandomSampleInterface;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.var.InversePowerVariogram;

class HeBiinvariantTest {
  private static final BarycentricCoordinate AFFINE = AffineWrap.of(HeGroup.INSTANCE);
  public static final BarycentricCoordinate INSTANCE = new HsCoordinates(HeGroup.INSTANCE, //
      new MetricCoordinate( //
          new NormWeighting(new HeTarget(Vector2Norm::of, RealScalar.ONE), //
              InversePowerVariogram.of(1))));
  private static final BarycentricCoordinate[] BARYCENTRIC_COORDINATES = { //
      // LeveragesCoordinate.slow(HeManifold.INSTANCE, InversePowerVariogram.of(1)), //
      // LeveragesCoordinate.slow(HeManifold.INSTANCE, InversePowerVariogram.of(2)), //
      AFFINE, //
      INSTANCE //
  };

  @Test
  void testSimple() {
    for (BarycentricCoordinate barycentricCoordinate : BARYCENTRIC_COORDINATES)
      for (int n = 1; n < 3; ++n)
        for (int length = 2 * n + 2; length < 2 * n + 10; ++length) {
          RandomSampleInterface rsi = new HeRandomSample(n, UniformDistribution.of(Clips.absolute(2)));
          Tensor sequence = RandomSample.of(rsi, length);
          Tensor mean1 = RandomSample.of(rsi);
          Tensor weights = barycentricCoordinate.weights(sequence, mean1);
          Tensor mean2 = HeGroup.INSTANCE.biinvariantMean().mean(sequence, weights);
          Chop._05.requireClose(mean1, mean2);
          // ---
          Tensor shift = RandomSample.of(rsi);
          for (TensorUnaryOperator tensorMapping : BiinvariantCheck.of(HeGroup.INSTANCE, shift))
            Chop._05.requireClose(weights, //
                barycentricCoordinate.weights(Tensor.of(sequence.stream().map(tensorMapping)), tensorMapping.apply(mean1)));
        }
  }

  @Test
  void testAffineBiinvariant() throws ClassNotFoundException, IOException {
    Random random = new Random(1);
    BarycentricCoordinate barycentricCoordinate = Serialization.copy(AFFINE);
    for (int n = 1; n < 3; ++n)
      for (int length = 2 * n + 2; length < 2 * n + 10; ++length) {
        RandomSampleInterface rsi = new HeRandomSample(n, UniformDistribution.of(Clips.absolute(10)));
        Tensor sequence = RandomSample.of(rsi, random, length);
        Tensor mean1 = RandomSample.of(rsi, random);
        Tensor weights = barycentricCoordinate.weights(sequence, mean1);
        Tensor mean2 = HeGroup.INSTANCE.biinvariantMean().mean(sequence, weights);
        Chop._06.requireClose(mean1, mean2);
        // ---
        Tensor shift = RandomSample.of(rsi, random);
        for (TensorUnaryOperator tensorMapping : BiinvariantCheck.of(HeGroup.INSTANCE, shift))
          Chop._04.requireClose(weights, //
              barycentricCoordinate.weights( //
                  Tensor.of(sequence.stream().map(tensorMapping)), tensorMapping.apply(mean1)));
      }
  }

  @Test
  void testAffineCenter() throws ClassNotFoundException, IOException {
    BarycentricCoordinate barycentricCoordinate = Serialization.copy(AFFINE);
    for (int n = 1; n < 3; ++n)
      for (int length = 2 * n + 2; length < 2 * n + 10; ++length) {
        RandomSampleInterface rsi = new HeRandomSample(n, UniformDistribution.of(Clips.absolute(10)));
        Tensor sequence = RandomSample.of(rsi, length);
        Tensor constant = AveragingWeights.INSTANCE.origin(sequence);
        Tensor center = HeGroup.INSTANCE.biinvariantMean().mean(sequence, constant);
        Tensor weights = barycentricCoordinate.weights(sequence, center);
        Tolerance.CHOP.requireClose(weights, constant);
      }
  }
}
