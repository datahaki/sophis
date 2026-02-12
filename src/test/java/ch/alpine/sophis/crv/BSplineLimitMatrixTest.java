// code by jph
package ch.alpine.sophis.crv;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.math.StochasticMatrixQ;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.itp.BSplineInterpolation;
import ch.alpine.tensor.mat.re.Inverse;

class BSplineLimitMatrixTest {
  @Test
  void testSimple() {
    for (int degree = 0; degree < 5; ++degree)
      for (int n = 1; n < 10; ++n) {
        Tensor tensor = BSplineInterpolation.matrix(degree, n);
        ExactTensorQ.require(tensor);
        StochasticMatrixQ.INSTANCE.require(tensor);
        StochasticMatrixQ.INSTANCE.require(Inverse.of(tensor));
        // System.out.println("n=" + n + " degree=" + degree);
        // System.out.println(Pretty.of(tensor));
      }
  }

  @Test
  void testNonPositiveFail() {
    assertThrows(Exception.class, () -> BSplineInterpolation.matrix(2, 0));
    assertThrows(Exception.class, () -> BSplineInterpolation.matrix(2, -1));
  }
}
