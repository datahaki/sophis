// code by jph
package ch.alpine.sophis.dv;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import ch.alpine.sophis.var.InversePowerVariogram;
import ch.alpine.sophus.api.MetricManifold;
import ch.alpine.sophus.api.TangentSpace;
import ch.alpine.sophus.hs.HomogeneousSpace;
import ch.alpine.sophus.hs.gr.Grassmannian;
import ch.alpine.sophus.hs.h.Hyperboloid;
import ch.alpine.sophus.hs.s.SnManifold;
import ch.alpine.sophus.hs.s.Sphere;
import ch.alpine.sophus.hs.st.StiefelManifold;
import ch.alpine.sophus.lie.rn.RGroup;
import ch.alpine.sophus.lie.rn.RnGroup;
import ch.alpine.sophus.lie.se.SeNGroup;
import ch.alpine.sophus.lie.se2.Se2CoveringGroup;
import ch.alpine.sophus.lie.se2.Se2Group;
import ch.alpine.sophus.lie.so.SoNGroup;
import ch.alpine.sophus.math.AffineQ;
import ch.alpine.sophus.rsm.LocalRandomSample;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomSample;
import ch.alpine.tensor.pdf.RandomSampleInterface;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Clips;

class UsanceCoordinateTest {
  public static List<HomogeneousSpace> homogeneousSpaces() {
    return Arrays.asList( //
        new StiefelManifold(3, 1), //
        new Grassmannian(5, 2), //
        new RnGroup(3), //
        Se2Group.INSTANCE, //
        Se2CoveringGroup.INSTANCE, //
        new SeNGroup(3), //
        new SeNGroup(4), //
        new SoNGroup(2), //
        new SoNGroup(3), //
        new SoNGroup(4), //
        new Sphere(2), //
        new Sphere(3), //
        new Hyperboloid(2), //
        new Hyperboloid(3), //
        new Hyperboloid(4) //
    );
  }

  @ParameterizedTest
  @MethodSource("homogeneousSpaces")
  void testSimple(HomogeneousSpace homogeneousSpace) {
    RandomSampleInterface rsi = (RandomSampleInterface) homogeneousSpace;
    Tensor p = RandomSample.of(rsi);
    TangentSpace tangentSpace = homogeneousSpace.tangentSpace(p);
    RandomSampleInterface rpnts = LocalRandomSample.of(tangentSpace, 0.1);
    Tensor sequence = RandomSample.of(rpnts, 20);
    Biinvariant biinvariant = Biinvariants.GARDEN.ofSafe(homogeneousSpace);
    biinvariant.relative_distances(sequence);
    biinvariant.coordinate(InversePowerVariogram.of(2), sequence);
    BarycentricCoordinate barycentricCoordinate = UsanceCoordinate.of(homogeneousSpace, InversePowerVariogram.of(2));
    Tensor weights = barycentricCoordinate.weights(sequence, p);
    AffineQ.INSTANCE.require(weights);
    Tensor levers = tangentSpace.vectorLog().slash(sequence);
    Tensor residual = weights.dot(levers);
    Tolerance.CHOP.requireAllZero(residual);
    Tensor q = homogeneousSpace.biinvariantMean().mean(sequence, weights);
    Tolerance.CHOP.requireClose(p, q);
  }

  @Test
  void testR1equiv() {
    // in R1 we have W^ID = w^IL
    // but not in R2 etc.
    MetricManifold manifold = RGroup.INSTANCE;
    MetricBiinvariant metricBiinvariant = new MetricBiinvariant(manifold);
    UsanceBiinvariant leveragesBiinvariant = new UsanceBiinvariant(manifold);
    ScalarUnaryOperator variogram = s -> s;
    Distribution distribution = UniformDistribution.of(Clips.absolute(Pi.TWO));
    for (int length = 3; length < 10; ++length) {
      Tensor sequence = RandomVariate.of(distribution, length, 1);
      Tensor origin = RandomVariate.of(distribution, 1);
      Chop._08.requireClose( //
          metricBiinvariant.weighting(variogram, sequence).sunder(origin), //
          leveragesBiinvariant.weighting(variogram, sequence).sunder(origin));
    }
  }

  @Test
  void testDiagonalNorm() {
    Distribution distribution = NormalDistribution.of(0, 0.2);
    Tensor betas = RandomVariate.of(UniformDistribution.of(1, 2), 4);
    for (Tensor beta_ : betas) {
      Scalar beta = (Scalar) beta_;
      BarycentricCoordinate bc1 = UsanceCoordinate.of(SnManifold.INSTANCE, InversePowerVariogram.of(beta));
      for (int d = 3; d < 7; ++d) {
        Tensor mean = UnitVector.of(d, 0);
        for (int n = d + 1; n < d + 3; ++n) {
          Tensor sequence = Tensor.of(RandomVariate.of(distribution, n, d).stream() //
              .map(mean::add) //
              .map(Vector2Norm.NORMALIZE));
          bc1.weights(sequence, mean);
        }
      }
    }
  }

  @Test
  void testSe2() {
    Distribution distribution = UniformDistribution.of(Clips.absolute(10));
    ScalarUnaryOperator variogram = s -> s;
    BarycentricCoordinate targetCoordinate = UsanceCoordinate.of(Se2CoveringGroup.INSTANCE, variogram);
    for (int length = 4; length < 10; ++length) {
      Tensor sequence = RandomVariate.of(distribution, length, 3);
      Tensor point = RandomVariate.of(distribution, 3);
      targetCoordinate.weights(sequence, point);
    }
  }
}
