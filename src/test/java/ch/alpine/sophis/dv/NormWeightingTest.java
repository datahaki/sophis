// code by jph
package ch.alpine.sophis.dv;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.sophis.math.Genesis;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.var.InversePowerVariogram;

class NormWeightingTest {
  @Test
  void testSimple() {
    Genesis inverseNorm = new NormWeighting(Vector2Norm::of, InversePowerVariogram.of(1));
    Tensor weights = inverseNorm.origin(Tensors.vector(1, 3).maps(Tensors::of));
    assertEquals(weights, Tensors.of(Rational.of(3, 4), Rational.of(1, 4)));
  }

  @Test
  void testPoints() {
    Distribution distribution = UniformDistribution.unit();
    int j = 2;
    for (int d = 2; d < 6; ++d)
      for (int n = d + 1; n < 10; ++n) {
        Tensor tensor = RandomVariate.of(distribution, n, d);
        tensor.set(Scalar::zero, j, Tensor.ALL);
        Genesis inverseNorm = new NormWeighting(Vector2Norm::of, InversePowerVariogram.of(1));
        for (int index = 0; index < tensor.length(); ++index) {
          Tensor q = inverseNorm.origin(tensor);
          Chop._10.requireClose(q, UnitVector.of(n, j));
        }
      }
  }
}
