// code by jph
package ch.alpine.sophis.var;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.qty.Quantity;

class ExponentialVariogramTest {
  @Test
  void testSimple() {
    ScalarUnaryOperator scalarUnaryOperator = ExponentialVariogram.of(Quantity.of(1, "m"));
    Scalar value = scalarUnaryOperator.apply(Vector2Norm.of(Tensors.fromString("{2[m], 3[m]}")));
    Tolerance.CHOP.requireClose(value, RealScalar.of(0.9728275388277644));
  }
}
