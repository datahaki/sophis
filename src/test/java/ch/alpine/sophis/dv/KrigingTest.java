// code by jph
package ch.alpine.sophis.dv;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import ch.alpine.sophis.dv.Biinvariant;
import ch.alpine.sophis.dv.Biinvariants;
import ch.alpine.sophis.dv.Kriging;
import ch.alpine.sophis.dv.Sedarim;
import ch.alpine.sophus.lie.rn.RGroup;
import ch.alpine.sophus.lie.se2.Se2CoveringGroup;
import ch.alpine.sophus.math.AffineQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.DiagonalMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityMagnitude;
import ch.alpine.tensor.qty.Unit;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.var.ExponentialVariogram;
import ch.alpine.tensor.sca.var.PowerVariogram;

class KrigingTest {
  // private static final Biinvariant[] BIINV = { Biinvariants.HARBOR };
  // private static final Biinvariant[] SYMME = { MetricBiinvariant.EUCLIDEAN, Biinvariants.HARBOR };
  @Test
  void testSimple2() {
    Distribution distributiox = NormalDistribution.standard();
    Distribution distribution = NormalDistribution.of(0, 0.1);
    PowerVariogram powerVariogram = PowerVariogram.of(1, 1.4);
    Map<Biinvariants, Biinvariant> map = Biinvariants.kriging(Se2CoveringGroup.INSTANCE);
    for (Biinvariant biinvariant : map.values()) {
      int n = 4 + ThreadLocalRandom.current().nextInt(6);
      Tensor points = RandomVariate.of(distributiox, n, 3);
      Tensor xya = RandomVariate.of(distribution, 3);
      Tensor values = RandomVariate.of(distributiox, n);
      Tensor covariance = DiagonalMatrix.with(ConstantArray.of(RealScalar.of(0.02), n));
      Sedarim tensorUnaryOperator1 = //
          biinvariant.var_dist(powerVariogram, points);
      Kriging kriging1 = Kriging.regression(tensorUnaryOperator1, points, values, covariance);
      Tensor est1 = kriging1.estimate(xya);
      Scalar var1 = kriging1.variance(xya);
      Tensor shift = RandomVariate.of(distribution, 3);
      for (TensorUnaryOperator tensorMapping : BiinvariantCheck.of(Se2CoveringGroup.INSTANCE, shift)) {
        Tensor all = Tensor.of(points.stream().map(tensorMapping));
        Sedarim tensorUnaryOperatorL = //
            biinvariant.var_dist(powerVariogram, all);
        Kriging krigingL = Kriging.regression(tensorUnaryOperatorL, all, values, covariance);
        Tensor one = tensorMapping.apply(xya);
        Chop._10.requireClose(est1, krigingL.estimate(one));
        Chop._10.requireClose(var1, krigingL.variance(one));
      }
    }
  }

  @Test
  void testSimple() throws ClassNotFoundException, IOException {
    Distribution distribution = NormalDistribution.standard();
    int n = 10;
    Tensor sequence = RandomVariate.of(distribution, n, 3);
    Tensor values = RandomVariate.of(distribution, n, 2);
    ScalarUnaryOperator variogram = PowerVariogram.of(RealScalar.ONE, RealScalar.of(1.5));
    Map<Biinvariants, Biinvariant> map = Biinvariants.kriging(RGroup.INSTANCE);
    for (Biinvariant biinvariant : map.values()) {
      Sedarim weightingInterface = biinvariant.var_dist(variogram, sequence);
      Kriging kriging = Serialization.copy(Kriging.interpolation(weightingInterface, sequence, values));
      for (int index = 0; index < sequence.length(); ++index) {
        Tensor tensor = kriging.estimate(sequence.get(index));
        Tolerance.CHOP.requireClose(tensor, values.get(index));
      }
    }
  }

  @Test
  void testScalarValued() throws ClassNotFoundException, IOException {
    Distribution distribution = NormalDistribution.standard();
    int n = 10;
    Tensor sequence = RandomVariate.of(distribution, n, 3);
    Tensor values = RandomVariate.of(distribution, n);
    ScalarUnaryOperator variogram = PowerVariogram.of(RealScalar.ONE, RealScalar.of(1.5));
    Map<Biinvariants, Biinvariant> map = Biinvariants.kriging(RGroup.INSTANCE);
    for (Biinvariant biinvariant : map.values()) {
      Sedarim weightingInterface = biinvariant.var_dist(variogram, sequence);
      Kriging kriging = Serialization.copy(Kriging.interpolation(weightingInterface, sequence, values));
      for (int index = 0; index < sequence.length(); ++index) {
        Tensor tensor = kriging.estimate(sequence.get(index));
        Tolerance.CHOP.requireClose(tensor, values.get(index));
      }
    }
  }

  @Test
  void testBarycentric() throws ClassNotFoundException, IOException {
    Random random = ThreadLocalRandom.current();
    Distribution distribution = NormalDistribution.standard();
    ScalarUnaryOperator variogram = PowerVariogram.of(RealScalar.ONE, RealScalar.of(1.5));
    int n = 5 + random.nextInt(5);
    Map<Biinvariants, Biinvariant> map = Biinvariants.kriging(RGroup.INSTANCE);
    for (int d = 1; d < 4; ++d) {
      Tensor sequence = RandomVariate.of(distribution, n, d);
      for (Biinvariant biinvariant : map.values()) {
        Sedarim tensorUnaryOperator = Serialization.copy(biinvariant.var_dist(variogram, sequence));
        Kriging kriging = Serialization.copy(Kriging.barycentric(tensorUnaryOperator, sequence));
        for (int index = 0; index < sequence.length(); ++index) {
          Tensor tensor = kriging.estimate(sequence.get(index));
          Chop._08.requireClose(tensor, UnitVector.of(n, index));
          // ---
          Tensor point = RandomVariate.of(distribution, d);
          Tensor weights = kriging.estimate(point);
          AffineQ.require(weights, Chop._08);
        }
      }
    }
  }

  @Test
  void testQuantityAbsolute() {
    Distribution distributionX = NormalDistribution.of(Quantity.of(0, "m"), Quantity.of(2, "m"));
    ScalarUnaryOperator variogram = new ExponentialVariogram(Quantity.of(3, "m"), RealScalar.of(2));
    int n = 10;
    int d = 3;
    Tensor sequence = RandomVariate.of(distributionX, n, d);
    Distribution distributionY = NormalDistribution.of(Quantity.of(0, "s"), Quantity.of(2, "s"));
    Tensor values = RandomVariate.of(distributionY, n);
    Sedarim sedarim = Biinvariants.METRIC.ofSafe(RGroup.INSTANCE).var_dist(variogram, sequence);
    Kriging kriging = Kriging.interpolation(sedarim, sequence, values);
    Scalar apply = (Scalar) kriging.estimate(RandomVariate.of(distributionX, d));
    QuantityMagnitude.singleton(Unit.of("s")).apply(apply);
  }

  @Test
  void testQuantityBiinvariant() {
    Distribution distributionX = NormalDistribution.of(Quantity.of(0, "m"), Quantity.of(2, "m"));
    ScalarUnaryOperator variogram = ExponentialVariogram.of(3, 2);
    int n = 10;
    int d = 3;
    Tensor sequence = RandomVariate.of(distributionX, n, d);
    Distribution distributionY = NormalDistribution.of(Quantity.of(0, "s"), Quantity.of(2, "s"));
    Tensor values = RandomVariate.of(distributionY, n);
    // Map<Biinvariants, Biinvariant> map = Biinvariants.kriging();
    Biinvariant biinvariant = Biinvariants.HARBOR.ofSafe(RGroup.INSTANCE);
    // for (Biinvariant biinvariant : map.values())
    {
      Sedarim weightingInterface = biinvariant.var_dist(variogram, sequence);
      Kriging kriging = Kriging.interpolation(weightingInterface, sequence, values);
      Scalar apply = (Scalar) kriging.estimate(RandomVariate.of(distributionX, d));
      QuantityMagnitude.singleton(Unit.of("s")).apply(apply);
    }
  }

  @Disabled
  @Test
  void testFitQuantity() throws ClassNotFoundException, IOException {
    Distribution distributionX = NormalDistribution.of(Quantity.of(0, "m"), Quantity.of(2, "m"));
    int n = 10;
    int d = 3;
    Tensor sequence = RandomVariate.of(distributionX, n, d);
    Distribution distributionY = NormalDistribution.of(Quantity.of(0, "s"), Quantity.of(2, "s"));
    Tensor values = RandomVariate.of(distributionY, n);
    Biinvariant biinvariant = Biinvariants.METRIC.ofSafe(RGroup.INSTANCE);
    {
      ScalarUnaryOperator variogram = Serialization.copy(new ExponentialVariogram(Quantity.of(3, "m"), RealScalar.of(2)));
      Sedarim weightingInterface = biinvariant.var_dist(variogram, sequence);
      Kriging kriging = Kriging.interpolation(weightingInterface, sequence, values);
      Scalar value = (Scalar) kriging.estimate(RandomVariate.of(distributionX, d));
      QuantityMagnitude.singleton(Unit.of("s")).apply(value);
    }
    {
      PowerVariogram variogram = Serialization.copy(PowerVariogramFit.fit(RGroup.INSTANCE, sequence, values, RealScalar.ONE));
      Tensor covariance = DiagonalMatrix.of(n, Quantity.of(1, "s^2"));
      Sedarim weightingInterface = biinvariant.var_dist(variogram, sequence);
      Kriging kriging = Kriging.regression(weightingInterface, sequence, values, covariance);
      Scalar value = (Scalar) kriging.estimate(RandomVariate.of(distributionX, d));
      QuantityMagnitude.singleton(Unit.of("s")).apply(value);
    }
  }

  @Test
  void testEmpty() {
    assertThrows(Exception.class, () -> PowerVariogramFit.fit(RGroup.INSTANCE, Tensors.empty(), Tensors.empty(), RealScalar.of(1.5)));
  }
}
