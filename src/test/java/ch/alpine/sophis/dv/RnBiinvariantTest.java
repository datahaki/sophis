package ch.alpine.sophis.dv;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;

import org.junit.jupiter.api.Test;

import ch.alpine.sophis.dv.BarycentricCoordinate;
import ch.alpine.sophis.dv.Biinvariant;
import ch.alpine.sophis.dv.Biinvariants;
import ch.alpine.sophis.dv.RnAffineCoordinate;
import ch.alpine.sophis.dv.Sedarim;
import ch.alpine.sophus.bm.BiinvariantMean;
import ch.alpine.sophus.bm.LinearBiinvariantMean;
import ch.alpine.sophus.hs.Manifold;
import ch.alpine.sophus.lie.rn.RGroup;
import ch.alpine.sophus.math.AffineQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.SymmetricMatrixQ;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Total;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.var.InversePowerVariogram;

class RnBiinvariantTest {
  @Test
  void testSymmetric() {
    int d = 2;
    int n = 5;
    Tensor sequence = RandomVariate.of(NormalDistribution.standard(), n, d);
    Manifold manifold = RGroup.INSTANCE;
    Biinvariant metricBiinvariant = Biinvariants.METRIC.ofSafe(manifold);
    Biinvariant harborBiinvariant = Biinvariants.HARBOR.ofSafe(manifold);
    for (Biinvariant biinvariant : new Biinvariant[] { metricBiinvariant, harborBiinvariant }) {
      Sedarim tensorUnaryOperator = biinvariant.distances(sequence);
      Tensor vardst = Tensor.of(sequence.stream().map(tensorUnaryOperator::sunder));
      SymmetricMatrixQ.INSTANCE.requireMember(vardst);
    }
    Biinvariant leveragesBiinvariant = Biinvariants.LEVERAGES.ofSafe(manifold);
    {
      Sedarim tensorUnaryOperator = leveragesBiinvariant.distances(sequence);
      Tensor vardst = Tensor.of(sequence.stream().map(tensorUnaryOperator::sunder));
      assertFalse(SymmetricMatrixQ.INSTANCE.isMember(vardst));
    }
  }

  @Test
  void testLinearPrecision() {
    Tensor sequence = RandomVariate.of(NormalDistribution.standard(), 10, 3);
    Tensor point = RandomVariate.of(NormalDistribution.standard(), 3);
    for (BarycentricCoordinate barycentricCoordinates : GbcHelper.barycentrics(RGroup.INSTANCE)) {
      Tensor weights = barycentricCoordinates.weights(sequence, point);
      Chop._10.requireClose(weights.dot(sequence), point);
    }
  }

  @Test
  void testWeights() {
    Distribution distribution = UniformDistribution.unit();
    BarycentricCoordinate barycentricCoordinate = AffineWrap.of(RGroup.INSTANCE);
    for (int d = 2; d < 5; ++d)
      for (int n = 5; n < 10; ++n) {
        Tensor sequence = RandomVariate.of(distribution, n, d);
        TensorUnaryOperator affineCoordinates = RnAffineCoordinate.of(sequence);
        Tensor point = RandomVariate.of(distribution, d);
        Tensor w1 = affineCoordinates.apply(point);
        VectorQ.requireLength(w1, n);
        Chop._06.requireClose(Total.ofVector(w1), RealScalar.ONE);
        Tensor w2 = barycentricCoordinate.weights(sequence, point);
        Chop._06.requireClose(w1, w2);
      }
  }

  @Test
  void testSimple() {
    Distribution distribution = NormalDistribution.standard();
    for (int n = 2; n < 5; ++n) {
      int length = n + 1 + ThreadLocalRandom.current().nextInt(3);
      Tensor points = RandomVariate.of(distribution, length, n);
      Tensor mean = RandomVariate.of(distribution, n);
      for (BarycentricCoordinate barycentricCoordinate : GbcHelper.barycentrics(RGroup.INSTANCE)) {
        Tensor weights = barycentricCoordinate.weights(points, mean);
        Tensor result = LinearBiinvariantMean.INSTANCE.mean(points, weights);
        Chop._08.requireClose(mean, result);
      }
    }
  }

  @Test
  void testRandom() {
    Distribution distribution = UniformDistribution.unit();
    BiinvariantMean biinvariantMean = LinearBiinvariantMean.INSTANCE;
    for (int n = 2; n < 4; ++n) {
      int length = n + 1 + ThreadLocalRandom.current().nextInt(3);
      Tensor points = RandomVariate.of(distribution, length, n);
      Tensor xya = RandomVariate.of(distribution, n);
      for (BarycentricCoordinate barycentricCoordinate : GbcHelper.barycentrics(RGroup.INSTANCE)) {
        Tensor weights = barycentricCoordinate.weights(points, xya);
        Chop._10.requireClose(Total.ofVector(weights), RealScalar.ONE);
        Tensor x_recreated = biinvariantMean.mean(points, weights);
        Chop._06.requireClose(xya, x_recreated);
        Tensor shift = RandomVariate.of(distribution, n);
        for (TensorUnaryOperator tensorMapping : BiinvariantCheck.of(RGroup.INSTANCE, shift))
          Chop._04.requireClose(weights, //
              barycentricCoordinate.weights( //
                  Tensor.of(points.stream().map(tensorMapping)), tensorMapping.apply(xya)));
      }
    }
  }

  @Test
  void testLinearReproduction() {
    Distribution distribution = UniformDistribution.unit();
    Biinvariant biinvariant = Biinvariants.METRIC.ofSafe(RGroup.INSTANCE);
    for (int n = 2; n < 6; ++n) {
      int length = n + 1 + ThreadLocalRandom.current().nextInt(3);
      Tensor points = RandomVariate.of(distribution, length, n);
      Tensor x = RandomVariate.of(distribution, n);
      Sedarim tensorUnaryOperator = biinvariant.coordinate(InversePowerVariogram.of(1), points);
      Tensor weights = tensorUnaryOperator.sunder(x);
      Tensor y = LinearBiinvariantMean.INSTANCE.mean(points, weights);
      Chop._06.requireClose(x, y);
    }
  }

  @Test
  void testLagrangeProperty() {
    RandomGenerator random = ThreadLocalRandom.current();
    Distribution distribution = UniformDistribution.unit();
    Biinvariant biinvariant = Biinvariants.METRIC.ofSafe(RGroup.INSTANCE);
    for (int n = 2; n < 6; ++n) {
      int length = n + 1 + random.nextInt(3);
      Tensor points = RandomVariate.of(distribution, length, n);
      Sedarim tensorUnaryOperator = biinvariant.coordinate(InversePowerVariogram.of(1), points);
      Chop._10.requireClose(Tensor.of(points.stream().map(tensorUnaryOperator::sunder)), IdentityMatrix.of(length));
    }
  }

  @Test
  void testQuantity() {
    RandomGenerator random = ThreadLocalRandom.current();
    Distribution distribution = UniformDistribution.of(Quantity.of(-1, "m"), Quantity.of(+1, "m"));
    Biinvariant biinvariant = Biinvariants.METRIC.ofSafe(RGroup.INSTANCE);
    for (int n = 2; n < 6; ++n) {
      int length = n + 1 + random.nextInt(3);
      Tensor points = RandomVariate.of(distribution, length, n);
      Tensor x = RandomVariate.of(distribution, n);
      Sedarim tensorUnaryOperator = biinvariant.coordinate(InversePowerVariogram.of(1), points);
      Tensor weights = tensorUnaryOperator.sunder(x);
      Tensor y = LinearBiinvariantMean.INSTANCE.mean(points, weights);
      Chop._06.requireClose(x, y);
    }
  }

  @Test
  void testAffineSimple() {
    RandomGenerator random = new Random(1);
    BarycentricCoordinate barycentricCoordinate = AffineWrap.of(RGroup.INSTANCE);
    for (int dim = 2; dim < 4; ++dim)
      for (int length = dim + 1; length < 8; ++length) {
        Distribution distribution = NormalDistribution.standard();
        Tensor sequence = RandomVariate.of(distribution, random, length, dim);
        Tensor mean = RandomVariate.of(distribution, random, dim);
        Tensor lhs = barycentricCoordinate.weights(sequence, mean);
        Tensor rhs = RnAffineCoordinate.INSTANCE.weights(sequence, mean);
        Chop._06.requireClose(lhs, rhs);
      }
  }

  @Test
  void testNullFail() {
    for (BarycentricCoordinate barycentricCoordinate : GbcHelper.barycentrics(RGroup.INSTANCE))
      assertThrows(Exception.class, () -> barycentricCoordinate.weights(null, null));
  }

  @Test
  void testColinear() {
    int d = 2;
    int n = 5;
    for (BarycentricCoordinate barycentricCoordinate : GbcHelper.barycentrics(RGroup.INSTANCE)) {
      Tensor sequence = RandomVariate.of(NormalDistribution.standard(), n, d);
      sequence.append(sequence.get(n - 1).multiply(RealScalar.of(5)));
      Tensor weights = barycentricCoordinate.weights(sequence, Array.zeros(d));
      assertEquals(sequence.length(), n + 1);
      AffineQ.require(weights, Chop._08);
    }
  }
}
