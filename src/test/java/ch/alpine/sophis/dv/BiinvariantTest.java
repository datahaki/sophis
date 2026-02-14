// code by jph
package ch.alpine.sophis.dv;

import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import ch.alpine.sophus.bm.MeanDefect;
import ch.alpine.sophus.hs.Manifold;
import ch.alpine.sophus.hs.gr.GrAction;
import ch.alpine.sophus.hs.gr.GrManifold;
import ch.alpine.sophus.hs.gr.Grassmannian;
import ch.alpine.sophus.hs.rpn.RpnManifold;
import ch.alpine.sophus.lie.rn.RGroup;
import ch.alpine.sophus.lie.so.So3Exponential;
import ch.alpine.sophus.lie.so.SoNGroup;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.nrm.NormalizeTotal;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomSample;
import ch.alpine.tensor.pdf.RandomSampleInterface;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityMagnitude;
import ch.alpine.tensor.red.Total;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.var.InversePowerVariogram;

class BiinvariantTest {
  @Test
  void testAbsolute() throws ClassNotFoundException, IOException {
    Distribution distribution = NormalDistribution.of(Quantity.of(1, "m"), Quantity.of(2, "m"));
    Biinvariant biinvariant = new MetricBiinvariant(RGroup.INSTANCE);
    Tensor sequence = RandomVariate.of(distribution, 10, 3);
    Sedarim weightingInterface = Serialization.copy(biinvariant.relative_distances(sequence));
    Tensor point = RandomVariate.of(distribution, 3);
    Tensor weights = weightingInterface.sunder(point);
    weights.maps(QuantityMagnitude.singleton("m"));
  }

  @Test
  void testBiinvariant() {
    Distribution distribution = NormalDistribution.of(Quantity.of(1, "m"), Quantity.of(2, "m"));
    Map<Biinvariants, Biinvariant> map = Biinvariants.all(RGroup.INSTANCE);
    for (Biinvariant biinvariant : map.values()) {
      Tensor sequence = RandomVariate.of(distribution, 10, 3);
      Sedarim weightingInterface = biinvariant.relative_distances(sequence);
      weightingInterface.sunder(RandomVariate.of(distribution, 3));
    }
  }

  @Test
  void testWeighting() throws ClassNotFoundException, IOException {
    Distribution distribution = UniformDistribution.unit();
    Map<Biinvariants, Biinvariant> map = Biinvariants.all(RGroup.INSTANCE);
    for (Biinvariant biinvariant : map.values()) {
      Tensor sequence = RandomVariate.of(distribution, 7, 3);
      Sedarim tensorUnaryOperator = Serialization.copy( //
          biinvariant.weighting(InversePowerVariogram.of(2), sequence));
      Tensor vector = tensorUnaryOperator.sunder(RandomVariate.of(distribution, 3));
      Chop._08.requireClose(Total.ofVector(vector), RealScalar.ONE);
    }
  }

  @Test
  void testCoordinate() throws ClassNotFoundException, IOException {
    Distribution distribution = UniformDistribution.unit();
    Map<Biinvariants, Biinvariant> map = Biinvariants.all(RGroup.INSTANCE);
    for (Biinvariant biinvariant : map.values()) {
      Tensor sequence = RandomVariate.of(distribution, 7, 3);
      Sedarim tensorUnaryOperator = Serialization.copy( //
          biinvariant.coordinate(InversePowerVariogram.of(2), sequence));
      Tensor vector = tensorUnaryOperator.sunder(RandomVariate.of(distribution, 3));
      Chop._08.requireClose(Total.ofVector(vector), RealScalar.ONE);
    }
  }

  @Disabled
  @Test
  void testBiinvariance() {
    Manifold manifold = GrManifold.INSTANCE;
    Biinvariant[] biinvariants = new Biinvariant[] { //
        Biinvariants.METRIC.ofSafe(manifold), //
        Biinvariants.LEVERAGES.ofSafe(manifold), //
        Biinvariants.GARDEN.ofSafe(manifold) };
    RandomGenerator random1 = ThreadLocalRandom.current();
    int n = 3 + random1.nextInt(2);
    ScalarUnaryOperator variogram = InversePowerVariogram.of(2);
    int k = 1 + random1.nextInt(n - 1);
    RandomSampleInterface randomSampleInterface = new Grassmannian(n, k);
    int d = k * (n - k);
    RandomGenerator randomGenerator = new Random(1);
    Tensor seq_o = RandomSample.of(randomSampleInterface, randomGenerator, d + 2);
    Tensor pnt_o = RandomSample.of(randomSampleInterface, randomGenerator);
    for (Biinvariant biinvariant : biinvariants) {
      Tensor w_o = biinvariant.coordinate(variogram, seq_o).sunder(pnt_o);
      GrAction grAction = new GrAction(RandomSample.of(new SoNGroup(n), randomGenerator));
      Tensor seq_l = Tensor.of(seq_o.stream().map(grAction));
      Tensor pnt_l = grAction.apply(pnt_o);
      Tensor w_l = biinvariant.coordinate(variogram, seq_l).sunder(pnt_l);
      Chop._06.requireClose(w_o, w_l);
    }
  }

  private static final BarycentricCoordinate[] BARYCENTRIC_COORDINATES = GbcHelper.barycentrics(RpnManifold.INSTANCE);

  @Test
  void testSpecific() {
    Distribution distribution = NormalDistribution.of(0, 0.2);
    for (BarycentricCoordinate barycentricCoordinate : BARYCENTRIC_COORDINATES)
      for (int count = 0; count < 10; ++count) {
        Tensor rotation = So3Exponential.vectorExp(RandomVariate.of(distribution, 3));
        Tensor mean = rotation.dot(Vector2Norm.NORMALIZE.apply(Tensors.vector(1, 1, 1)));
        Tensor sequence = Tensor.of(IdentityMatrix.of(3).stream().map(rotation::dot));
        Tensor weights = barycentricCoordinate.weights(sequence, mean);
        Chop._12.requireClose(weights, NormalizeTotal.FUNCTION.apply(Tensors.vector(1, 1, 1)));
        Chop._12.requireAllZero(MeanDefect.of(sequence, weights, RpnManifold.INSTANCE.exponential(mean)).tangent());
        {
          Tensor point = RpnManifold.INSTANCE.biinvariantMean().mean(sequence, weights);
          Chop._05.requireAllZero(MeanDefect.of(sequence, weights, RpnManifold.INSTANCE.exponential(point)).tangent());
        }
      }
  }
}
