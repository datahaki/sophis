// code by jph
package ch.alpine.sophis.dv;

import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.Test;

import ch.alpine.sophis.var.InversePowerVariogram;
import ch.alpine.sophus.bm.MeanDefect;
import ch.alpine.sophus.hs.rpn.RpManifold;
import ch.alpine.sophus.lie.rn.RGroup;
import ch.alpine.sophus.lie.so.So3Exponential;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.nrm.NormalizeTotal;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityMagnitude;
import ch.alpine.tensor.red.Total;
import ch.alpine.tensor.sca.Chop;

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

  private static final BarycentricCoordinate[] BARYCENTRIC_COORDINATES = GbcHelper.barycentrics(RpManifold.INSTANCE);

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
        Chop._12.requireAllZero(MeanDefect.of(sequence, weights, RpManifold.INSTANCE.tangentSpace(mean)).tangent());
        {
          Tensor point = RpManifold.INSTANCE.biinvariantMean().mean(sequence, weights);
          Chop._05.requireAllZero(MeanDefect.of(sequence, weights, RpManifold.INSTANCE.tangentSpace(point)).tangent());
        }
      }
  }
}
