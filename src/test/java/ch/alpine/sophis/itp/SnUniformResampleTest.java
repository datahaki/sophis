// code by jph
package ch.alpine.sophis.itp;

import org.junit.jupiter.api.Test;

import ch.alpine.sophis.itp.AdjacentDistances;
import ch.alpine.sophis.itp.UniformResample;
import ch.alpine.sophis.ref.d1.CurveSubdivision;
import ch.alpine.sophus.hs.s.SnManifold;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.red.Variance;
import ch.alpine.tensor.sca.Chop;

class SnUniformResampleTest {
  @Test
  void testSimple() {
    Scalar spacing = Pi.VALUE.divide(RealScalar.of(10));
    CurveSubdivision curveSubdivision = UniformResample.of(SnManifold.INSTANCE, SnManifold.INSTANCE, spacing);
    Tensor tensor = Tensors.fromString("{{1, 0}, {0, 1}, {-1, 0}}");
    Tensor string = curveSubdivision.string(tensor);
    Tensor distances = AdjacentDistances.of(SnManifold.INSTANCE).apply(string);
    Scalar variance = Variance.ofVector(distances);
    Chop._20.requireAllZero(variance);
  }
}
