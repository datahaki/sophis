// code by jph
package ch.alpine.sophis.flow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

class DoubleIntegratorStateSpaceModelTest {
  @Test
  void testSimple() {
    Tensor x = Tensors.vector(9, 8, 1, 2);
    Tensor u = Tensors.vector(3, 4);
    Tensor r = StateSpaceModels.DOUBLE_INTEGRATOR.f(x, u);
    assertEquals(r, Tensors.vector(1, 2, 3, 4));
  }

  @Test
  void testFail() {
    Tensor x = Tensors.vector(1, 2, 3, 4);
    assertThrows(Exception.class, () -> StateSpaceModels.DOUBLE_INTEGRATOR.f(x, Tensors.vector(3)));
    assertThrows(Exception.class, () -> StateSpaceModels.DOUBLE_INTEGRATOR.f(x, Tensors.vector(3, 4, 3)));
  }
}
