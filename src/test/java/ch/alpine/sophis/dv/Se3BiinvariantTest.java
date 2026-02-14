// code by jph
package ch.alpine.sophis.dv;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Random;
import java.util.random.RandomGenerator;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import ch.alpine.sophus.bm.BiinvariantMean;
import ch.alpine.sophus.bm.MeanDefect;
import ch.alpine.sophus.hs.Exponential;
import ch.alpine.sophus.lie.LieGroup;
import ch.alpine.sophus.lie.se.SeNGroup;
import ch.alpine.sophus.lie.se3.Se3Exponential0;
import ch.alpine.sophus.math.AffineQ;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.chq.ZeroDefectArrayQ;
import ch.alpine.tensor.mat.pi.LinearSubspace;
import ch.alpine.tensor.nrm.NormalizeTotal;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomSample;
import ch.alpine.tensor.pdf.RandomSampleInterface;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.c.TriangularDistribution;
import ch.alpine.tensor.sca.Chop;

class Se3BiinvariantTest {
  private static final LieGroup LIE_GROUP = new SeNGroup(3);
  // private static final IterativeBiinvariantMean ITERATIVE_BIINVARIANT_MEAN = //
  // IterativeBiinvariantMean.argmax(LIE_GROUP, Chop._12);

  static BarycentricCoordinate[] barycentrics() {
    return GbcHelper.biinvariant(LIE_GROUP);
  }

  private static final RandomSampleInterface RSI_Se3A = (RandomSampleInterface) LIE_GROUP;

  @Disabled
  @ParameterizedTest
  @MethodSource("barycentrics")
  void testRelativeRandom(BarycentricCoordinate barycentricCoordinate) {
    BiinvariantMean biinvariantMean = LIE_GROUP.biinvariantMean();
    int n = 10;
    Exponential exponential = Se3Exponential0.INSTANCE;
    ZeroDefectArrayQ zeroDefectArrayQ = exponential.isTangentQ();
    LinearSubspace linearSubspace = LinearSubspace.of(zeroDefectArrayQ::defect, 4, 4);
    assertEquals(linearSubspace.dimensions(), 6);
    Distribution distribution = NormalDistribution.of(0, 0.1);
    Tensor vs = linearSubspace.slash(RandomVariate.of(distribution, n, 6));
    Tensor sequence = LIE_GROUP.exponential0().exp().slash(vs);
    Tensor point = LIE_GROUP.exponential0().exp(linearSubspace.apply(RandomVariate.of(distribution, 6)));
    LIE_GROUP.isPointQ().require(point);
    Tensor weights = barycentricCoordinate.weights(sequence, point);
    // Tensor points = RandomSample.of(RSI_Se3A, random, n);
    // Tensor xya = RandomSample.of(RSI_Se3A, random);
    // Tensor weights = barycentricCoordinate.weights(points, xya);
    AffineQ.INSTANCE.require(weights); // , Chop._08);
    // Tensor check1 = biinvariantMean.mean(points, weights);
    // Chop._10.requireClose(check1, xya);
    // Chop._10.requireClose(Total.ofVector(weights), RealScalar.ONE);
    // Tensor x_recreated = biinvariantMean.mean(points, weights);
    // Chop._06.requireClose(xya, x_recreated);
    // Tensor shift = RandomSample.of(RSI_Se3A, random);
    // for (TensorUnaryOperator tensorMapping : BiinvariantCheck.of(LIE_GROUP, shift)) {
    // Tensor all = Tensor.of(points.stream().map(tensorMapping));
    // Tensor one = tensorMapping.apply(xya);
    // Chop._10.requireClose(one, biinvariantMean.mean(all, weights));
    // Chop._05.requireClose(weights, barycentricCoordinate.weights(all, one));
    // }
  }

  @Disabled
  @ParameterizedTest
  @MethodSource("barycentrics")
  void testRandom2(BarycentricCoordinate barycentricCoordinate) {
    BiinvariantMean biinvariantMean = LIE_GROUP.biinvariantMean();
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
      Tensor mean = biinvariantMean.mean(sequence, weights);
      assertEquals(Dimensions.of(mean), Arrays.asList(4, 4));
      Tensor defect = MeanDefect.of(sequence, weights, LIE_GROUP.exponential(mean)).tangent();
      Chop._08.requireAllZero(defect);
    }
  }
}
