// code by jph
package ch.alpine.sophis.dv;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import ch.alpine.sophus.bm.MeanDefect;
import ch.alpine.sophus.lie.LieGroup;
import ch.alpine.sophus.lie.VectorizedGroup;
import ch.alpine.sophus.lie.so.So3Exponential;
import ch.alpine.sophus.lie.so.So3Group;
import ch.alpine.sophus.math.AffineQ;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.nrm.NormalizeTotal;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.sca.Chop;

class So3BiinvariantTest {
  private static final LieGroup LIE_GROUP = new VectorizedGroup(So3Group.INSTANCE);

  static BarycentricCoordinate[] barycentrics() {
    return GbcHelper.barycentrics(LIE_GROUP);
  }

  @Test
  void testAffineLinearReproduction() {
    RandomGenerator random = new Random(1);
    Distribution distribution = NormalDistribution.of(0.0, 0.3);
    Distribution d2 = NormalDistribution.of(0.0, 0.1);
    BarycentricCoordinate AFFINE = AffineWrap.of(LIE_GROUP);
    int n = 4 + random.nextInt(2);
    Tensor sequence = Tensor.of(RandomVariate.of(distribution, n, 3).stream().map(So3Exponential::vectorExp));
    Tensor mean = So3Exponential.vectorExp(RandomVariate.of(d2, 3));
    Tensor weights1 = AFFINE.weights(sequence, mean);
    Tensor o2 = LIE_GROUP.biinvariantMean().mean(sequence, weights1);
    Chop._08.requireClose(mean, o2);
    // ---
    Tensor p = So3Group.INSTANCE.randomSample(ThreadLocalRandom.current());
    Tensor seqlft = Tensor.of(sequence.stream().map(t -> LIE_GROUP.combine(p, t)));
    Tensor weights2 = AFFINE.weights(seqlft, LIE_GROUP.combine(p, mean));
    Chop._10.requireClose(weights1, weights2);
    // ---
    Chop._10.requireClose(weights1, AFFINE.weights(Tensor.of(sequence.stream().map(LIE_GROUP::invert)), LIE_GROUP.invert(mean)));
  }

  @ParameterizedTest
  @MethodSource("barycentrics")
  void testSimple3(BarycentricCoordinate barycentricCoordinate) {
    Tensor g1 = So3Exponential.vectorExp(Tensors.vector(0.2, 0.3, 0.4));
    Tensor g2 = So3Exponential.vectorExp(Tensors.vector(0.1, 0.0, 0.5));
    Tensor g3 = So3Exponential.vectorExp(Tensors.vector(0.3, 0.5, 0.2));
    Tensor g4 = So3Exponential.vectorExp(Tensors.vector(0.5, 0.2, 0.1));
    Tensor sequence = Tensors.of(g1, g2, g3, g4);
    Tensor mean = So3Exponential.vectorExp(Tensors.vector(0.4, 0.2, 0.3));
    Tensor weights = barycentricCoordinate.weights(sequence, mean);
    Tensor defect = MeanDefect.of(sequence, weights, LIE_GROUP.exponential(mean)).tangent();
    Chop._10.requireAllZero(defect);
  }

  @ParameterizedTest
  @MethodSource("barycentrics")
  void testLinearReproduction(BarycentricCoordinate barycentricCoordinate) {
    RandomGenerator random = new Random(4);
    Distribution distribution = NormalDistribution.of(0.0, 0.3);
    Distribution d2 = NormalDistribution.of(0.0, 0.1);
    int n = 4 + random.nextInt(2);
    {
      Tensor sequence = Tensor.of(RandomVariate.of(distribution, random, n, 3).stream().map(So3Exponential::vectorExp));
      Tensor mean = So3Exponential.vectorExp(RandomVariate.of(d2, random, 3));
      Tensor weights1 = barycentricCoordinate.weights(sequence, mean);
      Tensor o2 = LIE_GROUP.biinvariantMean().mean(sequence, weights1);
      Chop._08.requireClose(mean, o2);
      // ---
      Tensor p = So3Group.INSTANCE.randomSample(ThreadLocalRandom.current());
      Tensor seqlft = Tensor.of(sequence.stream().map(t -> LIE_GROUP.combine(p, t)));
      Tensor weights2 = barycentricCoordinate.weights(seqlft, LIE_GROUP.combine(p, mean));
      Chop._06.requireClose(weights1, weights2);
      // ---
      {
        // TensorMapping tensorMapping = LIE_GROUP_OPS.inversion();
        Chop._06.requireClose(weights1, //
            barycentricCoordinate.weights(Tensor.of(sequence.stream().map(LIE_GROUP::invert)), LIE_GROUP.invert(mean)));
      }
    }
  }

  @ParameterizedTest
  @MethodSource("barycentrics")
  void testLagrange(BarycentricCoordinate barycentricCoordinate) {
    Distribution distribution = NormalDistribution.of(0.0, 0.1);
    for (int n = 4; n < 8; ++n) {
      Tensor sequence = Tensor.of(RandomVariate.of(distribution, n, 3).stream().map(So3Exponential::vectorExp));
      int index = 0;
      for (Tensor point : sequence) {
        Tensor weights = barycentricCoordinate.weights(sequence, point);
        AffineQ.INSTANCE.require(weights);
        if (!Chop._06.isClose(weights, UnitVector.of(n, index))) {
          IO.println(barycentricCoordinate);
          IO.println(weights);
          // Chop._06.requireClose(weights, UnitVector.of(n, index));
        } else {
          Tensor o2 = LIE_GROUP.biinvariantMean().mean(sequence, weights);
          Chop._06.requireClose(point, o2);
        }
        ++index;
      }
    }
  }

  @ParameterizedTest
  @MethodSource("barycentrics")
  void testConvergence(BarycentricCoordinate barycentricCoordinate) {
    Distribution distribution = NormalDistribution.of(0.0, 0.3);
    int n = 4 + ThreadLocalRandom.current().nextInt(6);
    Tensor sequence = Tensor.of(RandomVariate.of(distribution, n, 3).stream().map(So3Exponential::vectorExp));
    Tensor weights = NormalizeTotal.FUNCTION.apply(RandomVariate.of(UniformDistribution.unit(), n));
    Tensor mean = So3Group.INSTANCE.biinvariantMean().mean(sequence, weights);
    Tensor w2 = barycentricCoordinate.weights(sequence, mean);
    Tensor o2 = So3Group.INSTANCE.biinvariantMean().mean(sequence, w2);
    Chop._08.requireClose(mean, o2);
  }

  @ParameterizedTest
  @MethodSource("barycentrics")
  void testConvergenceExact(BarycentricCoordinate barycentricCoordinate) {
    Distribution distribution = NormalDistribution.of(0.0, 0.3);
    int n = 4;
    Tensor sequence = Tensor.of(RandomVariate.of(distribution, n, 3).stream().map(So3Exponential::vectorExp));
    Tensor weights = NormalizeTotal.FUNCTION.apply(RandomVariate.of(UniformDistribution.unit(), n));
    Tensor mean = So3Group.INSTANCE.biinvariantMean().mean(sequence, weights);
    Tensor w2 = barycentricCoordinate.weights(sequence, mean);
    Tensor o2 = So3Group.INSTANCE.biinvariantMean().mean(sequence, w2);
    Chop._08.requireClose(mean, o2.get());
    Chop._08.requireClose(weights, w2);
  }
}
