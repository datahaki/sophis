// code by jph
package ch.alpine.sophis.dv;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import ch.alpine.sophus.bm.BiinvariantMean;
import ch.alpine.sophus.hs.Manifold;
import ch.alpine.sophus.lie.se2.Se2CoveringGroup;
import ch.alpine.sophus.lie.se2.Se2CoveringRandomSample;
import ch.alpine.sophus.lie.se2.Se2Group;
import ch.alpine.sophus.lie.se2.Se2RandomSample;
import ch.alpine.sophus.math.AffineQ;
import ch.alpine.sophus.math.AveragingWeights;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.lie.Symmetrize;
import ch.alpine.tensor.mat.SymmetricMatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.ev.Eigensystem;
import ch.alpine.tensor.mat.gr.InfluenceMatrix;
import ch.alpine.tensor.mat.gr.Mahalanobis;
import ch.alpine.tensor.mat.pi.PseudoInverse;
import ch.alpine.tensor.nrm.NormalizeTotal;
import ch.alpine.tensor.nrm.Vector2NormSquared;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomSample;
import ch.alpine.tensor.pdf.RandomSampleInterface;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.LogNormalDistribution;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Total;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Unitize;
import ch.alpine.tensor.sca.pow.Power;
import ch.alpine.tensor.sca.var.InversePowerVariogram;

class Se2BiinvariantTest {
  private static final RandomSampleInterface RANDOM_SAMPLE_INTERFACE = //
      Se2CoveringRandomSample.uniform(UniformDistribution.of(Clips.absolute(10)));

  @Test
  void testAdInv() {
    int n = 5 + ThreadLocalRandom.current().nextInt(3);
    Tensor sequence = RandomSample.of(RANDOM_SAMPLE_INTERFACE, n);
    Tensor point = RandomSample.of(RANDOM_SAMPLE_INTERFACE);
    Tensor shift = RandomSample.of(RANDOM_SAMPLE_INTERFACE);
    for (TensorUnaryOperator tensorMapping : BiinvariantCheck.of(Se2CoveringGroup.INSTANCE, shift)) {
      Tensor all = Tensor.of(sequence.stream().map(tensorMapping));
      Tensor one = tensorMapping.apply(point);
      for (BarycentricCoordinate barycentricCoordinate : GbcHelper.biinvariant(Se2CoveringGroup.INSTANCE)) {
        Tensor w1 = barycentricCoordinate.weights(sequence, point);
        Tensor w2 = barycentricCoordinate.weights(all, one);
        if (!Chop._03.isClose(w1, w2)) {
          System.out.println("---");
          System.out.println(w1);
          System.out.println(w2);
          fail();
        }
      }
      Biinvariant biinvariant = Biinvariants.HARBOR.ofSafe(Se2CoveringGroup.INSTANCE);
      for (int exp = 0; exp < 3; ++exp) {
        Sedarim gr1 = biinvariant.coordinate(Power.function(exp), sequence);
        Sedarim gr2 = biinvariant.coordinate(Power.function(exp), all);
        Tensor w1 = gr1.sunder(point);
        Tensor w2 = gr2.sunder(one);
        Chop._10.requireClose(w1, w2);
      }
    }
  }

  @Test
  void testLinearReproduction() {
    Random random = ThreadLocalRandom.current();
    int n = 5 + random.nextInt(5);
    Tensor sequence = RandomSample.of(RANDOM_SAMPLE_INTERFACE, n);
    Biinvariant biinvariant = Biinvariants.HARBOR.ofSafe(Se2CoveringGroup.INSTANCE);
    Sedarim grCoordinate = biinvariant.coordinate(InversePowerVariogram.of(2), sequence);
    Tensor point = RandomSample.of(RANDOM_SAMPLE_INTERFACE);
    Tensor weights = grCoordinate.sunder(point);
    Tensor mean = Se2CoveringGroup.INSTANCE.biinvariantMean().mean(sequence, weights);
    Chop._05.requireClose(point, mean);
  }

  static BarycentricCoordinate[] barycentric_coordinates() {
    return GbcHelper.biinvariant(Se2CoveringGroup.INSTANCE);
  }

  private static final BarycentricCoordinate[] QUANTITY_COORDINATES = //
      GbcHelper.biinvariant_quantity(Se2CoveringGroup.INSTANCE);
  private static final BarycentricCoordinate AD_INVAR = new HsCoordinates( //
      Se2CoveringGroup.INSTANCE, //
      new MetricCoordinate( //
          new NormWeighting(new Se2CoveringTarget(Vector2NormSquared::of, RealScalar.ONE), InversePowerVariogram.of(1))));

  @ParameterizedTest
  @MethodSource("barycentric_coordinates")
  void test4Exact(BarycentricCoordinate barycentricCoordinate) {
    Distribution distribution = UniformDistribution.unit();
    final int n = 4;
    RandomGenerator random = new Random(1);
    for (int count = 0; count < 5; ++count) {
      Tensor points = RandomVariate.of(distribution, random, n, 3);
      Se2CoveringBarycenter se2CoveringBarycenter = new Se2CoveringBarycenter(points);
      Tensor xya = RandomVariate.of(distribution, random, 3);
      Tensor weights = barycentricCoordinate.weights(points, xya);
      Chop._06.requireClose(weights, se2CoveringBarycenter.apply(xya));
      Chop._06.requireClose(xya, Se2CoveringGroup.INSTANCE.biinvariantMean().mean(points, weights));
    }
  }

  @ParameterizedTest
  @MethodSource("barycentric_coordinates")
  void testLinearReproduction2(BarycentricCoordinate barycentricCoordinate) {
    RandomGenerator random = ThreadLocalRandom.current();
    Distribution distribution = NormalDistribution.standard();
    BiinvariantMean biinvariantMean = Se2CoveringGroup.INSTANCE.biinvariantMean();
    int n = 4 + random.nextInt(4);
    Tensor points = RandomVariate.of(distribution, random, n, 3);
    Tensor target = AveragingWeights.INSTANCE.origin(points);
    Tensor x = biinvariantMean.mean(points, target);
    Tensor weights = barycentricCoordinate.weights(points, x);
    Chop._10.requireClose(Total.ofVector(weights), RealScalar.ONE);
    Tensor x_recreated = biinvariantMean.mean(points, weights);
    Chop._06.requireClose(x, x_recreated);
  }

  @Disabled
  @ParameterizedTest
  @MethodSource("barycentric_coordinates")
  void testRandom(BarycentricCoordinate barycentricCoordinate) {
    RandomGenerator random = ThreadLocalRandom.current();
    Distribution distributiox = NormalDistribution.standard();
    Distribution distribution = NormalDistribution.of(0, 0.1);
    BiinvariantMean biinvariantMean = Se2CoveringGroup.INSTANCE.biinvariantMean();
    int n = 4 + random.nextInt(4);
    Tensor points = RandomVariate.of(distributiox, n, 3);
    Tensor xya = RandomVariate.of(distribution, 3);
    Tensor weights = barycentricCoordinate.weights(points, xya);
    AffineQ.INSTANCE.requireMember(weights); // , Chop._08);
    Tensor check1 = biinvariantMean.mean(points, weights);
    Chop._06.requireClose(check1, xya);
    Chop._06.requireClose(Total.ofVector(weights), RealScalar.ONE);
    Tensor x_recreated = biinvariantMean.mean(points, weights);
    Chop._06.requireClose(xya, x_recreated);
    Tensor shift = RandomSample.of(RANDOM_SAMPLE_INTERFACE);
    for (TensorUnaryOperator tensorMapping : BiinvariantCheck.of(Se2CoveringGroup.INSTANCE, shift)) {
      Tensor all = Tensor.of(points.stream().map(tensorMapping));
      Tensor one = tensorMapping.apply(xya);
      Chop._06.requireClose(one, biinvariantMean.mean(all, weights));
      Chop._06.requireClose(weights, barycentricCoordinate.weights(all, one));
    }
  }

  @ParameterizedTest
  @MethodSource("barycentric_coordinates")
  void testNullFail(BarycentricCoordinate barycentricCoordinate) {
    assertThrows(Exception.class, () -> barycentricCoordinate.weights(null, null));
  }

  @ParameterizedTest
  @MethodSource("barycentric_coordinates")
  void testLagrange(BarycentricCoordinate barycentricCoordinate) {
    Distribution distribution = NormalDistribution.standard();
    for (int n = 5; n < 8; ++n) {
      Tensor sequence = RandomVariate.of(distribution, n, 3);
      for (int index = 0; index < n; ++index) {
        Tensor weights = barycentricCoordinate.weights(sequence, sequence.get(index));
        AffineQ.INSTANCE.requireMember(weights); // , Chop._08);
        if (!Chop._06.isClose(weights, UnitVector.of(n, index))) {
          IO.println(barycentricCoordinate);
          IO.println(weights);
        }
      }
    }
  }

  private static Tensor withUnits(Tensor xya) {
    return Tensors.of( //
        Quantity.of(xya.Get(0), "m"), //
        Quantity.of(xya.Get(1), "m"), //
        xya.Get(2));
  }

  @Test
  void testQuantity2() {
    RandomGenerator random = ThreadLocalRandom.current();
    Distribution distribution = NormalDistribution.standard();
    int n = 4 + random.nextInt(4);
    Tensor sequence = RandomVariate.of(distribution, n, 3);
    sequence.set(Se2BiinvariantTest::withUnits, Tensor.ALL);
    for (BarycentricCoordinate barycentricCoordinate : QUANTITY_COORDINATES) {
      for (int index = 0; index < n; ++index) {
        Tensor weights = barycentricCoordinate.weights(sequence, sequence.get(index));
        AffineQ.INSTANCE.requireMember(weights); // , Chop._08);
        Chop._06.requireClose(weights, UnitVector.of(n, index));
      }
      Tensor weights = barycentricCoordinate.weights(sequence, withUnits(RandomVariate.of(distribution, 3)));
      AffineQ.INSTANCE.requireMember(weights); // , Chop._08);
    }
  }

  @Disabled
  @ParameterizedTest
  @MethodSource("barycentric_coordinates")
  void testProjection(BarycentricCoordinate barycentricCoordinate) {
    RandomGenerator random = ThreadLocalRandom.current();
    Distribution distributiox = NormalDistribution.of(0, 0.1);
    Distribution distribution = NormalDistribution.of(0, 0.1);
    BiinvariantMean biinvariantMean = Se2CoveringGroup.INSTANCE.biinvariantMean();
    Manifold manifold = Se2CoveringGroup.INSTANCE;
    int n = 4 + random.nextInt(4);
    Tensor points = RandomVariate.of(distributiox, n, 3);
    Tensor xya = RandomVariate.of(distribution, 3);
    Tensor weights = barycentricCoordinate.weights(points, xya);
    Tensor matrix = manifold.exponential(xya).log().slash(points);
    Tensor influence = matrix.dot(PseudoInverse.of(matrix));
    new SymmetricMatrixQ(Chop._10).requireMember(influence);
    Chop._10.requireClose(Symmetrize.of(influence), influence);
    AffineQ.INSTANCE.requireMember(weights); // , Chop._08);
    Tensor check1 = biinvariantMean.mean(points, weights);
    Chop._06.requireClose(check1, xya);
    Chop._10.requireClose(Total.ofVector(weights), RealScalar.ONE);
    Tensor x_recreated = biinvariantMean.mean(points, weights);
    Chop._06.requireClose(xya, x_recreated);
    Tensor shift = RandomSample.of(RANDOM_SAMPLE_INTERFACE);
    for (TensorUnaryOperator tensorMapping : BiinvariantCheck.of(Se2CoveringGroup.INSTANCE, shift)) {
      Tensor all = Tensor.of(points.stream().map(tensorMapping));
      Tensor one = tensorMapping.apply(xya);
      Chop._08.requireClose(one, biinvariantMean.mean(all, weights));
      Chop._06.requireClose(weights, barycentricCoordinate.weights(all, one));
      Tensor levers = manifold.exponential(one).log().slash(all);
      Chop._06.requireClose(influence, InfluenceMatrix.of(levers).matrix());
    }
  }

  @ParameterizedTest
  @MethodSource("barycentric_coordinates")
  void testProjectionIntoAdInvariant(BarycentricCoordinate barycentricCoordinate) {
    Distribution distribution = NormalDistribution.standard();
    BiinvariantMean biinvariantMean = Se2CoveringGroup.INSTANCE.biinvariantMean();
    Manifold manifold = Se2CoveringGroup.INSTANCE;
    int n = 4 + ThreadLocalRandom.current().nextInt(4);
    Tensor sequence = RandomVariate.of(distribution, n, 3);
    Tensor weights = NormalizeTotal.FUNCTION.apply(RandomVariate.of(UniformDistribution.unit(), n));
    Tensor xya = biinvariantMean.mean(sequence, weights);
    Tensor weights1 = barycentricCoordinate.weights(sequence, xya); // projection
    AffineQ.INSTANCE.requireMember(weights1); // , Chop._08);
    Chop._08.requireClose(weights, weights);
    Tensor matrix = manifold.exponential(xya).log().slash(sequence);
    Tensor residualMaker = InfluenceMatrix.of(matrix).residualMaker();
    Chop._08.requireClose(residualMaker.dot(weights), weights);
    assertEquals(Dimensions.of(residualMaker), Arrays.asList(n, n));
    Chop._08.requireClose(Symmetrize.of(residualMaker), residualMaker);
    Eigensystem eigensystem = Eigensystem.ofSymmetric(Symmetrize.of(residualMaker)).decreasing();
    Tensor unitize = eigensystem.values().maps(Tolerance.CHOP).maps(Unitize.FUNCTION);
    Chop._08.requireClose(eigensystem.values(), unitize);
    assertEquals(Total.ofVector(unitize), RealScalar.of(n - 3));
    for (int index = 0; index < n - 3; ++index) {
      Chop._08.requireClose(eigensystem.values().get(index), RealScalar.ONE);
      Tensor eigenw = NormalizeTotal.FUNCTION.apply(eigensystem.vectors().get(index));
      Tensor recons = biinvariantMean.mean(sequence, eigenw);
      Chop._07.requireClose(xya, recons);
    }
  }

  private static final BarycentricCoordinate[] BIINVARIANT_COORDINATES = { //
      // LeveragesCoordinate.slow(Se2CoveringManifold.INSTANCE, InversePowerVariogram.of(0)), //
      // LeveragesCoordinate.slow(Se2CoveringManifold.INSTANCE, InversePowerVariogram.of(1)), //
      // LeveragesCoordinate.slow(Se2CoveringManifold.INSTANCE, InversePowerVariogram.of(2)), //
      AD_INVAR };

  @Test
  void testA4Exact() {
    Distribution distribution = UniformDistribution.unit();
    for (BarycentricCoordinate barycentricCoordinate : BIINVARIANT_COORDINATES) {
      int n = 4;
      Tensor points = RandomVariate.of(distribution, n, 3);
      Se2CoveringBarycenter se2CoveringBarycenter = new Se2CoveringBarycenter(points);
      Tensor xya = RandomVariate.of(distribution, 3);
      Tensor w1 = barycentricCoordinate.weights(points, xya);
      Tensor w2 = se2CoveringBarycenter.apply(xya);
      Chop._04.requireClose(w1, w2);
      Tensor mean = Se2CoveringGroup.INSTANCE.biinvariantMean().mean(points, w1);
      Chop._04.requireClose(xya, mean);
    }
  }

  @Test
  void testALinearReproduction() {
    Random random = new Random();
    Distribution distribution = NormalDistribution.standard();
    BiinvariantMean biinvariantMean = Se2CoveringGroup.INSTANCE.biinvariantMean();
    for (BarycentricCoordinate barycentricCoordinate : BIINVARIANT_COORDINATES) {
      int n = 4 + random.nextInt(4);
      Tensor points = RandomVariate.of(distribution, n, 3);
      Tensor target = AveragingWeights.INSTANCE.origin(points);
      Tensor x = biinvariantMean.mean(points, target);
      Tensor weights = barycentricCoordinate.weights(points, x);
      Chop._10.requireClose(Total.ofVector(weights), RealScalar.ONE);
      Tensor x_recreated = biinvariantMean.mean(points, weights);
      Chop._06.requireClose(x, x_recreated);
    }
  }

  @Test
  void testARandom() {
    Random random = ThreadLocalRandom.current();
    Distribution distributiox = NormalDistribution.standard();
    Distribution distribution = NormalDistribution.of(0, 0.1);
    BiinvariantMean biinvariantMean = Se2CoveringGroup.INSTANCE.biinvariantMean();
    for (BarycentricCoordinate barycentricCoordinate : BIINVARIANT_COORDINATES) {
      int n = 4 + random.nextInt(4);
      Tensor points = RandomVariate.of(distributiox, n, 3);
      Tensor xya = RandomVariate.of(distribution, 3);
      Tensor weights = barycentricCoordinate.weights(points, xya);
      AffineQ.INSTANCE.requireMember(weights); // , Chop._08);
      Tensor check1 = biinvariantMean.mean(points, weights);
      Chop._07.requireClose(check1, xya);
      Chop._10.requireClose(Total.ofVector(weights), RealScalar.ONE);
      Tensor x_recreated = biinvariantMean.mean(points, weights);
      Chop._06.requireClose(xya, x_recreated);
      Tensor shift = RandomSample.of(RANDOM_SAMPLE_INTERFACE);
      for (TensorUnaryOperator tensorMapping : BiinvariantCheck.of(Se2CoveringGroup.INSTANCE, shift)) {
        Tensor all = Tensor.of(points.stream().map(tensorMapping));
        Tensor one = tensorMapping.apply(xya);
        Chop._06.requireClose(one, biinvariantMean.mean(all, weights));
        Chop._06.requireClose(weights, barycentricCoordinate.weights(all, one));
      }
    }
  }

  @Test
  void testDiagonalNorm() {
    Tensor betas = RandomVariate.of(UniformDistribution.of(1, 2), 4);
    for (Tensor _beta : betas) {
      Scalar beta = (Scalar) _beta;
      // BarycentricCoordinate bc0 = LeveragesCoordinate.slow(Se2CoveringManifold.INSTANCE, InversePowerVariogram.of(beta));
      BarycentricCoordinate bc1 = LeveragesCoordinate.of(Se2CoveringGroup.INSTANCE, InversePowerVariogram.of(beta));
      for (int n = 4; n < 10; ++n) {
        Tensor sequence = RandomSample.of(RANDOM_SAMPLE_INTERFACE, n);
        Tensor mean = RandomSample.of(RANDOM_SAMPLE_INTERFACE);
        // Tensor w0 = bc0.weights(sequence, mean);
        // Tensor w1 =
        bc1.weights(sequence, mean);
        // Chop._06.requireClose(w0, w1);
      }
    }
  }

  @Test
  void testSimple3() {
    int n = 5 + ThreadLocalRandom.current().nextInt(5);
    Tensor sequence = RandomSample.of(Se2RandomSample.of(LogNormalDistribution.standard()), n);
    Biinvariant biinvariant = Biinvariants.HARBOR.ofSafe(Se2Group.INSTANCE);
    Sedarim tuo = biinvariant.distances(sequence);
    Tensor matrix = Tensor.of(sequence.stream().map(tuo::sunder));
    assertEquals(Dimensions.of(matrix), Arrays.asList(n, n));
    assertTrue(SymmetricMatrixQ.INSTANCE.isMember(matrix));
    // matrix entry i,j contains frobenius norm between
    // projection matrices at point i, and at point j
  }

  @Test
  void testSe2CadInvariant() {
    Distribution distribution = UniformDistribution.of(-10, +10);
    Manifold manifold = Se2CoveringGroup.INSTANCE;
    for (int count = 4; count < 10; ++count) {
      Tensor sequence = RandomVariate.of(distribution, count, 3);
      Tensor point = RandomVariate.of(distribution, 3);
      Tensor leverages_sqrt = new Mahalanobis(manifold.exponential(point).log().slash(sequence)).leverages_sqrt();
      leverages_sqrt.stream().map(Scalar.class::cast).forEach(Clips.unit()::requireInside);
      Tensor shift = RandomVariate.of(distribution, 3);
      for (TensorUnaryOperator tensorMapping : BiinvariantCheck.of(Se2CoveringGroup.INSTANCE, shift)) {
        Tensor matrix = manifold.exponential(tensorMapping.apply(point)).log().slash(tensorMapping.slash(sequence));
        Chop._05.requireClose(leverages_sqrt, //
            new Mahalanobis(matrix).leverages_sqrt());
      }
    }
  }

  @Test
  void testANullFail() {
    for (BarycentricCoordinate barycentricCoordinate : BIINVARIANT_COORDINATES)
      assertThrows(Exception.class, () -> barycentricCoordinate.weights(null, null));
  }
}
