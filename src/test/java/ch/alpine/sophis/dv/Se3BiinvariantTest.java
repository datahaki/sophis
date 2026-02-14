// code by jph
package ch.alpine.sophis.dv;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Random;
import java.util.random.RandomGenerator;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import ch.alpine.sophus.bm.BiinvariantMean;
import ch.alpine.sophus.bm.IterativeBiinvariantMean;
import ch.alpine.sophus.bm.MeanDefect;
import ch.alpine.sophus.lie.se3.Se3Group;
import ch.alpine.sophus.lie.se3.Se3RandomSample;
import ch.alpine.sophus.math.AffineQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.nrm.NormalizeTotal;
import ch.alpine.tensor.pdf.RandomSample;
import ch.alpine.tensor.pdf.RandomSampleInterface;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.TriangularDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.red.Total;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Clips;

class Se3BiinvariantTest {
  private static final IterativeBiinvariantMean ITERATIVE_BIINVARIANT_MEAN = //
      IterativeBiinvariantMean.argmax(Se3Group.INSTANCE, Chop._12);

  static BarycentricCoordinate[] barycentrics() {
    return GbcHelper.biinvariant(Se3Group.INSTANCE);
  }

  private static final RandomSampleInterface RSI_Se3A = new Se3RandomSample( //
      UniformDistribution.of(Clips.absolute(5)), //
      TriangularDistribution.with(0, 0.25));

  @ParameterizedTest
  @MethodSource("barycentrics")
  void testRelativeRandom(BarycentricCoordinate barycentricCoordinate) {
    RandomGenerator random = new Random(3);
    BiinvariantMean biinvariantMean = ITERATIVE_BIINVARIANT_MEAN;
    int n = 7 + random.nextInt(3);
    Tensor points = RandomSample.of(RSI_Se3A, random, n);
    Tensor xya = RandomSample.of(RSI_Se3A, random);
    Tensor weights = barycentricCoordinate.weights(points, xya);
    AffineQ.INSTANCE.require(weights); // , Chop._08);
    Tensor check1 = biinvariantMean.mean(points, weights);
    Chop._10.requireClose(check1, xya);
    Chop._10.requireClose(Total.ofVector(weights), RealScalar.ONE);
    Tensor x_recreated = biinvariantMean.mean(points, weights);
    Chop._06.requireClose(xya, x_recreated);
    Tensor shift = RandomSample.of(RSI_Se3A, random);
    for (TensorUnaryOperator tensorMapping : BiinvariantCheck.of(Se3Group.INSTANCE, shift)) {
      Tensor all = Tensor.of(points.stream().map(tensorMapping));
      Tensor one = tensorMapping.apply(xya);
      Chop._10.requireClose(one, biinvariantMean.mean(all, weights));
      Chop._05.requireClose(weights, barycentricCoordinate.weights(all, one));
    }
  }

  @ParameterizedTest
  @MethodSource("barycentrics")
  void testRandom2(BarycentricCoordinate barycentricCoordinate) {
    RandomGenerator random = new Random(3);
    int n = 7 + random.nextInt(4);
    Tensor sequence = RandomSample.of(RSI_Se3A, random, n);
    Tensor point = RandomSample.of(RSI_Se3A, random);
    {
      Tensor weights = barycentricCoordinate.weights(sequence, point);
      AffineQ.INSTANCE.require(weights);
    }
    {
      Tensor weights = RandomVariate.of(TriangularDistribution.with(1, 0.3), n);
      weights = NormalizeTotal.FUNCTION.apply(weights);
      Tensor mean = ITERATIVE_BIINVARIANT_MEAN.mean(sequence, weights);
      assertEquals(Dimensions.of(mean), Arrays.asList(4, 4));
      Tensor defect = MeanDefect.of(sequence, weights, Se3Group.INSTANCE.exponential(mean)).tangent();
      Chop._08.requireAllZero(defect);
    }
  }
}
