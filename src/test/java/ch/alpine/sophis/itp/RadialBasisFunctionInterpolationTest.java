// code by jph
package ch.alpine.sophis.itp;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.random.RandomGenerator;

import org.junit.jupiter.api.Test;

import ch.alpine.sophis.dv.Biinvariant;
import ch.alpine.sophis.dv.Biinvariants;
import ch.alpine.sophis.dv.Sedarim;
import ch.alpine.sophus.lie.rn.RGroup;
import ch.alpine.sophus.math.AffineQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.var.PowerVariogram;
import ch.alpine.tensor.sca.var.VariogramFunctions;

class RadialBasisFunctionInterpolationTest {
  @Test
  void testSimple() throws ClassNotFoundException, IOException {
    RandomGenerator randomGenerator = new Random(3);
    Distribution distribution = NormalDistribution.standard();
    int n = 10;
    Tensor sequence = RandomVariate.of(distribution, randomGenerator, n, 3);
    Tensor values = RandomVariate.of(distribution, randomGenerator, n, 2);
    VariogramFunctions[] vars = { VariogramFunctions.POWER, VariogramFunctions.INVERSE_POWER, VariogramFunctions.GAUSSIAN,
        VariogramFunctions.INVERSE_MULTIQUADRIC };
    Map<Biinvariants, Biinvariant> map = Biinvariants.magic3(RGroup.INSTANCE);
    for (Biinvariant biinvariant : map.values())
      for (VariogramFunctions variograms : vars) {
        Sedarim weightingInterface = biinvariant.weighting(variograms.of(RealScalar.TWO), sequence);
        TensorUnaryOperator tensorUnaryOperator = Serialization.copy( //
            RadialBasisFunctionInterpolation.of(weightingInterface, sequence, values));
        int index = randomGenerator.nextInt(sequence.length());
        Tensor tensor = tensorUnaryOperator.apply(sequence.get(index));
        Chop._08.requireClose(tensor, values.get(index));
      }
  }

  @Test
  void testNormalized() {
    Distribution distribution = NormalDistribution.standard();
    int n = 10;
    RandomGenerator randomGenerator = new Random(1);
    Tensor sequence = RandomVariate.of(distribution, randomGenerator, n, 3);
    Tensor values = RandomVariate.of(distribution, randomGenerator, n, 2);
    Map<Biinvariants, Biinvariant> map = Biinvariants.magic3(RGroup.INSTANCE);
    for (Biinvariant biinvariant : map.values()) {
      Sedarim weightingInterface = biinvariant.var_dist(PowerVariogram.of(1, 2), sequence);
      TensorUnaryOperator tensorUnaryOperator = //
          RadialBasisFunctionInterpolation.of(weightingInterface, sequence, values);
      for (int index = 0; index < sequence.length(); ++index) {
        Tensor tensor = tensorUnaryOperator.apply(sequence.get(index));
        Chop._06.requireClose(tensor, values.get(index));
      }
    }
  }

  @Test
  void testBarycentric() {
    RandomGenerator randomGenerator = new Random(1);
    Distribution distribution = NormalDistribution.standard();
    int n = 10;
    Tensor sequence = RandomVariate.of(distribution, randomGenerator, n, 3);
    Map<Biinvariants, Biinvariant> map = Biinvariants.magic3(RGroup.INSTANCE);
    for (Biinvariant biinvariant : map.values()) {
      Sedarim weightingInterface = biinvariant.weighting(PowerVariogram.of(1, 2), sequence);
      TensorUnaryOperator tensorUnaryOperator = RadialBasisFunctionInterpolation.of(weightingInterface, sequence);
      for (int index = 0; index < sequence.length(); ++index) {
        Tensor tensor = tensorUnaryOperator.apply(sequence.get(index));
        Chop._05.requireClose(tensor, UnitVector.of(n, index));
        // ---
        Tensor point = RandomVariate.of(distribution, randomGenerator, 3);
        Tensor weights = tensorUnaryOperator.apply(point);
        AffineQ.require(weights, Chop._08);
      }
    }
  }

  @Test
  void testNullFail() {
    assertThrows(Exception.class, () -> RadialBasisFunctionInterpolation.of(null, Tensors.empty(), Tensors.empty()));
  }
}
