// code by jph
package ch.alpine.sophis.flow;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityTensor;
import ch.alpine.tensor.qty.Unit;

class TimeIntegratorsTest {
  @ParameterizedTest
  @EnumSource
  void testAll(TimeIntegrators timeIntegrators) {
    StateSpaceModel stateSpaceModel = StateSpaceModels.SINGLE_INTEGRATOR;
    Tensor u = QuantityTensor.of(Tensors.vector(1, 2), Unit.of("m*s^-1"));
    Tensor x0 = QuantityTensor.of(Tensors.vector(10, 2), Unit.of("m"));
    Scalar h = Quantity.of(1, "s");
    Tensor x1 = timeIntegrators.step(stateSpaceModel, x0, u, h);
    assertEquals(x1, Tensors.fromString("{11[m], 4[m]}"));
  }
}
