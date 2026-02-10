// code by jph
package ch.alpine.sophis.dv;

import java.util.Optional;
import java.util.Random;
import java.util.random.RandomGenerator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import ch.alpine.sophus.bm.IterativeBiinvariantMean;
import ch.alpine.sophus.hs.s.Sphere;
import ch.alpine.sophus.lie.rn.RGroup;
import ch.alpine.sophus.math.Genesis;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.nrm.NormalizeTotal;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomSample;
import ch.alpine.tensor.pdf.RandomSampleInterface;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.sca.Chop;

class AffineCoordinateTest {
  static BarycentricCoordinate[] barycentrics() {
    return GbcHelper.barycentrics(RGroup.INSTANCE);
  }

  @Test
  void testS1() {
    Genesis genesis = MetricCoordinate.affine();
    RandomSampleInterface randomSampleInterface = new Sphere(1);
    RandomGenerator randomGenerator = new Random(3);
    for (int n = 3; n < 10; ++n) {
      Tensor levers = RandomSample.of(randomSampleInterface, randomGenerator, n);
      Tensor w1 = genesis.origin(levers.unmodifiable());
      Tensor w2 = AffineCoordinate.INSTANCE.origin(levers);
      Chop._07.requireClose(w1, w2);
    }
  }

  @Test
  void testRn() {
    Genesis genesis = MetricCoordinate.affine();
    Distribution distribution = NormalDistribution.standard();
    RandomGenerator randomGenerator = new Random(3);
    for (int n = 3; n < 10; ++n)
      for (int k = 0; k < 2; ++k) {
        int d = 2 + k;
        Tensor levers = RandomVariate.of(distribution, randomGenerator, n + k, d);
        Tensor w1 = genesis.origin(levers);
        Tensor w2 = AffineCoordinate.INSTANCE.origin(levers);
        Chop._04.requireClose(w1, w2);
      }
  }

  private static final IterativeBiinvariantMean ITERATIVE_BIINVARIANT_MEAN = //
      IterativeBiinvariantMean.argmax(RGroup.INSTANCE);

  @ParameterizedTest
  @MethodSource("barycentrics")
  void testConvergence(BarycentricCoordinate barycentricCoordinate) {
    Distribution distribution = NormalDistribution.of(0.0, 0.3);
    for (int d = 3; d <= 5; ++d)
      for (int n = d + 1; n < 10; ++n) {
        Tensor sequence = Tensor.of(RandomVariate.of(distribution, n, d).stream().map(RGroup.INSTANCE.exponential0()::exp));
        Tensor weights = NormalizeTotal.FUNCTION.apply(RandomVariate.of(UniformDistribution.unit(), n));
        Optional<Tensor> optional = ITERATIVE_BIINVARIANT_MEAN.apply(sequence, weights);
        Tensor mean = optional.orElseThrow();
        Tensor w2 = barycentricCoordinate.weights(sequence, mean);
        Optional<Tensor> o2 = ITERATIVE_BIINVARIANT_MEAN.apply(sequence, w2);
        Chop._08.requireClose(mean, o2.orElseThrow());
      }
  }

  @ParameterizedTest
  @MethodSource("barycentrics")
  void testConvergenceExact(BarycentricCoordinate barycentricCoordinate) {
    Random random = new Random(3);
    Distribution distribution = NormalDistribution.of(0.0, 0.3);
    int n = 4;
    Tensor sequence = Tensor.of(RandomVariate.of(distribution, random, n, 3).stream().map(RGroup.INSTANCE.exponential0()::exp));
    Tensor weights = NormalizeTotal.FUNCTION.apply(RandomVariate.of(UniformDistribution.unit(), random, n));
    Optional<Tensor> optional = ITERATIVE_BIINVARIANT_MEAN.apply(sequence, weights);
    Tensor mean = optional.orElseThrow();
    Tensor w2 = barycentricCoordinate.weights(sequence, mean);
    Optional<Tensor> o2 = ITERATIVE_BIINVARIANT_MEAN.apply(sequence, w2);
    Chop._08.requireClose(mean, o2.orElseThrow());
    Chop._08.requireClose(weights, w2);
  }
}
