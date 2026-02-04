package ch.alpine.sophis.dv;

import java.util.Random;
import java.util.random.RandomGenerator;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.sophus.bm.MeanDefect;
import ch.alpine.sophus.hs.s.SnManifold;
import ch.alpine.sophus.lie.so.Rodrigues;
import ch.alpine.sophus.lie.so.SoNGroup;
import ch.alpine.sophus.math.AffineQ;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.nrm.NormalizeTotal;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomSample;
import ch.alpine.tensor.pdf.RandomSampleInterface;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.sca.Chop;

class SnBiinvariantsTest {
  private static final BarycentricCoordinate[] BARYCENTRIC_COORDINATES = //
      GbcHelper.barycentrics(SnManifold.INSTANCE);

  private static Tensor randomCloud(Tensor mean, int n, RandomGenerator random) {
    Distribution distribution = NormalDistribution.of(0, 0.2);
    return Tensor.of(RandomVariate.of(distribution, random, n, mean.length()).stream().map(mean::add).map(Vector2Norm.NORMALIZE));
  }

  @ParameterizedTest
  @ValueSource(ints = { 2, 3, 4 })
  void testLinearReproduction(int d) {
    RandomGenerator randomGenerator = new Random(3);
    for (BarycentricCoordinate barycentricCoordinate : BARYCENTRIC_COORDINATES) {
      Tensor mean = UnitVector.of(d, 0);
      int n = d + 1 + randomGenerator.nextInt(3);
      Tensor sequence = randomCloud(mean, n, randomGenerator);
      Tensor weights = barycentricCoordinate.weights(sequence, mean);
      VectorQ.requireLength(weights, n);
      AffineQ.require(weights, Chop._08);
      Tensor evaluate = new MeanDefect(sequence, weights, SnManifold.INSTANCE.exponential(mean)).tangent();
      Chop._06.requireAllZero(evaluate);
      Chop._06.requireClose(mean, SnManifold.INSTANCE.biinvariantMean().mean(sequence, weights));
    }
  }

  @Disabled // FIXME SOPHUS
  @ParameterizedTest
  @ValueSource(ints = { 2, 3 })
  void testLagrangeProperty(int d) {
    RandomGenerator randomGenerator = new Random(3);
    for (BarycentricCoordinate barycentricCoordinate : BARYCENTRIC_COORDINATES) {
      int n = d + 1 + randomGenerator.nextInt(3);
      Tensor sequence = randomCloud(UnitVector.of(d, 0), n, randomGenerator);
      int count = randomGenerator.nextInt(sequence.length());
      Tensor mean = sequence.get(count);
      Tensor weights = barycentricCoordinate.weights(sequence, mean);
      VectorQ.requireLength(weights, n);
      AffineQ.require(weights, Chop._08);
      Chop._06.requireClose(weights, UnitVector.of(n, count));
      Tensor evaluate = new MeanDefect(sequence, weights, SnManifold.INSTANCE.exponential(mean)).tangent();
      Chop._06.requireAllZero(evaluate);
      Chop._03.requireClose(mean, SnManifold.INSTANCE.biinvariantMean().mean(sequence, weights));
    }
  }

  @ParameterizedTest
  @ValueSource(ints = { 2, 3, 4 })
  void testBiinvariance(int d) {
    RandomGenerator randomGenerator = new Random(3);
    for (BarycentricCoordinate barycentricCoordinate : BARYCENTRIC_COORDINATES) {
      Tensor mean = UnitVector.of(d, 0);
      RandomSampleInterface randomSampleInterface = new SoNGroup(d);
      int n = d + 1 + randomGenerator.nextInt(3);
      Tensor sequence = randomCloud(mean, n, randomGenerator);
      Tensor weights = barycentricCoordinate.weights(sequence, mean);
      VectorQ.requireLength(weights, n);
      AffineQ.require(weights, Chop._08);
      {
        Tensor evaluate = new MeanDefect(sequence, weights, SnManifold.INSTANCE.exponential(mean)).tangent();
        Chop._08.requireAllZero(evaluate);
      }
      // ---
      {
        Tensor matrix = RandomSample.of(randomSampleInterface);
        Tensor mean2 = matrix.dot(mean);
        Tensor shifted = Tensor.of(sequence.stream().map(matrix::dot));
        Tensor evaluate = new MeanDefect(shifted, weights, SnManifold.INSTANCE.exponential(mean2)).tangent();
        Chop._10.requireAllZero(evaluate);
        Tensor weights2 = barycentricCoordinate.weights(shifted, mean2);
        Chop._04.requireClose(weights, weights2); // 1e-6 does not always work
      }
    }
  }

  @Test
  void testSpecific() {
    Distribution distribution = NormalDistribution.of(0, 0.2);
    for (BarycentricCoordinate barycentricCoordinate : BARYCENTRIC_COORDINATES) {
      Tensor rotation = Rodrigues.vectorExp(RandomVariate.of(distribution, 3));
      Tensor mean = rotation.dot(Vector2Norm.NORMALIZE.apply(Tensors.vector(1, 1, 1)));
      Tensor sequence = Tensor.of(IdentityMatrix.of(3).stream().map(rotation::dot));
      Chop._08.requireClose(sequence, Transpose.of(rotation));
      Tensor weights = barycentricCoordinate.weights(sequence, mean);
      Chop._12.requireClose(weights, NormalizeTotal.FUNCTION.apply(Tensors.vector(1, 1, 1)));
      Tensor evaluate = new MeanDefect(sequence, weights, SnManifold.INSTANCE.exponential(mean)).tangent();
      Chop._12.requireAllZero(evaluate);
      Chop._05.requireClose(mean, SnManifold.INSTANCE.biinvariantMean().mean(sequence, weights));
    }
  }
}
