// code by jph
package ch.alpine.sophis.flow;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

class SingleIntegratorStateSpaceModelTest {
  @Test
  void testSimple() {
    Tensor u = Tensors.vector(1, 2, 3);
    Tensor r = StateSpaceModels.SINGLE_INTEGRATOR.f(null, u);
    assertEquals(u, r);
  }
}
