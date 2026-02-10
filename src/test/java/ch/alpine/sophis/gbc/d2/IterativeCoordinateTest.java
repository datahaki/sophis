// code by jph
package ch.alpine.sophis.gbc.d2;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;

import org.junit.jupiter.api.Test;

import ch.alpine.sophis.crv.d2.alg.OriginEnclosureQ;
import ch.alpine.sophis.dv.MetricCoordinate;
import ch.alpine.sophus.bm.LinearBiinvariantMean;
import ch.alpine.sophus.bm.MeanDefect;
import ch.alpine.sophus.lie.rn.RGroup;
import ch.alpine.sophus.math.Genesis;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.itp.LinearBinaryAverage;
import ch.alpine.tensor.lie.rot.CirclePoints;
import ch.alpine.tensor.nrm.NormalizeTotal;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.var.InversePowerVariogram;

class IterativeCoordinateTest {
  private static void _checkIterative(Genesis genesis) {
    Distribution distribution = UniformDistribution.of(-0.05, 0.05);
    for (int n = 3; n < 10; ++n) {
      Tensor levers = CirclePoints.of(n).add(RandomVariate.of(distribution, n, 2));
      for (int k = 0; k < 5; ++k) {
        Tensor weights = new IterativeCoordinate(genesis, k).origin(levers);
        Chop._10.requireAllZero(weights.dot(levers));
        {
          Tensor matrix = new IterativeCoordinateMatrix(k).origin(levers);
          Tensor circum = matrix.dot(levers);
          Tensor wn = NormalizeTotal.FUNCTION.apply(genesis.origin(circum).dot(matrix));
          Chop._10.requireClose(wn, weights);
        }
        MeanDefect meanDefect = MeanDefect.of(levers, weights, RGroup.INSTANCE.exponential0());
        Tensor tangent = meanDefect.tangent();
        Chop._07.requireAllZero(tangent);
      }
    }
  }

  private static void _checkCornering(Genesis genesis) {
    for (int n = 3; n < 10; ++n) {
      Tensor polygon = CirclePoints.of(n);
      int index = 0;
      for (Tensor x : polygon) {
        Tensor weights = genesis.origin(Tensor.of(polygon.stream().map(x::subtract)));
        Chop._12.requireClose(weights, UnitVector.of(n, index));
        ++index;
      }
    }
  }

  private static void _checkAlongedge(Genesis genesis, boolean strict) {
    RandomGenerator randomGenerator = ThreadLocalRandom.current();
    for (int n = 3; n < 10; ++n) {
      Tensor polygon = CirclePoints.of(n);
      int index = randomGenerator.nextInt(polygon.length() - 1);
      Tensor x = LinearBinaryAverage.INSTANCE.split( //
          polygon.get(index), //
          polygon.get(index + 1), //
          RealScalar.of(randomGenerator.nextDouble()));
      Tensor levers = Tensor.of(polygon.stream().map(x::subtract));
      if (OriginEnclosureQ.INSTANCE.isMember(levers) && strict) {
        Tensor weights = genesis.origin(levers);
        Chop._10.requireClose(LinearBiinvariantMean.INSTANCE.mean(polygon, weights), x);
      }
    }
  }

  @Test
  void testMV() {
    Genesis genesis = ThreePointCoordinate.of(ThreePointScalings.MEAN_VALUE);
    _checkIterative(genesis);
    _checkCornering(genesis);
    _checkAlongedge(genesis, false);
  }

  @Test
  void testID() {
    Genesis genesis = MetricCoordinate.of(InversePowerVariogram.of(2));
    _checkIterative(genesis);
    _checkCornering(genesis);
    _checkAlongedge(genesis, true);
  }

  @Test
  void testIC() {
    Genesis genesis = new IterativeCoordinate(MetricCoordinate.affine(), 3);
    _checkCornering(genesis);
    _checkAlongedge(genesis, false);
  }

  @Test
  void testKis0() {
    Distribution distribution = UniformDistribution.of(-10, 10);
    for (int n = 3; n < 10; ++n) {
      Tensor levers = RandomVariate.of(distribution, n, 2);
      if (OriginEnclosureQ.INSTANCE.isMember(levers)) {
        Tensor weights = ThreePointCoordinate.of(ThreePointScalings.MEAN_VALUE).origin(levers);
        Chop._07.requireClose( //
            weights, //
            new IterativeCoordinate(new ThreePointWeighting(ThreePointScalings.MEAN_VALUE), 0).origin(levers));
        if (weights.stream().map(Scalar.class::cast).anyMatch(Sign::isNegative)) {
          boolean result = Chop._10.isClose( //
              weights, //
              new IterativeCoordinate(new ThreePointWeighting(ThreePointScalings.MEAN_VALUE), 2).origin(levers));
          if (4 < n)
            assertFalse(result);
        }
      }
    }
  }

  @Test
  void testBiinv() {
    Distribution distribution = UniformDistribution.of(-10, 10);
    Genesis genesis = MetricCoordinate.of(InversePowerVariogram.of(2));
    for (int n = 3; n < 10; ++n) {
      Tensor levers = RandomVariate.of(distribution, n, 2);
      if (OriginEnclosureQ.INSTANCE.isMember(levers)) {
        for (int k = 0; k < 3; ++k) {
          Genesis ic = new IterativeCoordinate(genesis, k);
          Tensor weights = ic.origin(levers);
          MeanDefect meanDefect = MeanDefect.of(levers, weights, RGroup.INSTANCE.exponential0());
          Tensor tangent = meanDefect.tangent();
          Chop._07.requireAllZero(tangent);
        }
      }
    }
  }

  @Test
  void testSimple123() {
    Genesis genesis = MetricCoordinate.affine();
    Distribution distribution = UniformDistribution.of(-0.1, 0.1);
    for (int n = 3; n < 10; ++n) {
      Tensor levers = CirclePoints.of(n).add(RandomVariate.of(distribution, n, 2));
      for (int k = 0; k < 5; ++k) {
        Tensor weights = new IterativeCoordinate(genesis, k).origin(levers);
        Chop._10.requireAllZero(weights.dot(levers));
        MeanDefect meanDefect = MeanDefect.of(levers, weights, RGroup.INSTANCE.exponential0());
        Tensor tangent = meanDefect.tangent();
        Chop._07.requireAllZero(tangent);
      }
    }
  }
}
