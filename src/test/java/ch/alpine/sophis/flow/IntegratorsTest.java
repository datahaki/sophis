// code by jph
package ch.alpine.sophis.flow;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityTensor;
import ch.alpine.tensor.qty.Unit;

class IntegratorsTest {
  @Test
  void testDouble() {
    StateSpaceModel stateSpaceModel = StateSpaceModels.DOUBLE_INTEGRATOR;
    Tensor u = QuantityTensor.of(Tensors.vector(1, 2), Unit.of("m*s^-2"));
    Tensor x = Tensors.fromString("{2[m], 3[m], 4[m*s^-1], 5[m*s^-1]}"); // pos and vel
    Tensor r = TimeIntegrators.EULER.step(stateSpaceModel, x, u, Quantity.of(2, "s"));
    assertEquals(r, Tensors.fromString("{10[m], 13[m], 6[m*s^-1], 9[m*s^-1]}"));
  }

  @Test
  void testSimple() {
    StateSpaceModel stateSpaceModel = StateSpaceModels.SINGLE_INTEGRATOR;
    Tensor u = Tensors.vector(1, 2);
    Tensor x = Tensors.vector(7, 2);
    Scalar h = RealScalar.of(3);
    Tensor euler_x1 = TimeIntegrators.EULER.step(stateSpaceModel, x, u, h);
    Tensor mid_x1 = TimeIntegrators.MIDPOINT.step(stateSpaceModel, x, u, h);
    Tensor rk4_x1 = TimeIntegrators.RK4.step(stateSpaceModel, x, u, h);
    Tensor rk45_x1 = TimeIntegrators.RK45.step(stateSpaceModel, x, u, h);
    assertEquals(euler_x1, x.add(u.multiply(h)));
    assertEquals(euler_x1, mid_x1);
    for (int n = 1; n < 10; ++n) {
      Tensor mmi_x1 = new ModifiedMidpointIntegrator(n).step(stateSpaceModel, x, u, h);
      assertEquals(euler_x1, mmi_x1);
    }
    assertEquals(euler_x1, rk4_x1);
    assertEquals(euler_x1, rk45_x1);
    // ---
    ExactTensorQ.require(euler_x1);
    ExactTensorQ.require(mid_x1);
    ExactTensorQ.require(rk4_x1);
    ExactTensorQ.require(rk45_x1);
  }

  @Test
  void testSimple2() {
    StateSpaceModel stateSpaceModel = StateSpaceModels.SINGLE_INTEGRATOR;
    Tensor u = QuantityTensor.of(Tensors.vector(1, 2), Unit.of("m*s^-1"));
    Tensor x = QuantityTensor.of(Tensors.vector(1, 2), Unit.of("m"));
    Tensor r = TimeIntegrators.MIDPOINT.step(stateSpaceModel, x, u, Quantity.of(2, "s"));
    assertEquals(r, Tensors.fromString("{3[m], 6[m]}"));
  }

  @Test
  void testDouble2() {
    StateSpaceModel stateSpaceModel = StateSpaceModels.DOUBLE_INTEGRATOR;
    Tensor u = QuantityTensor.of(Tensors.vector(1, 2), Unit.of("m*s^-2"));
    Tensor x = Tensors.fromString("{2[m], 3[m], 4[m*s^-1], 5[m*s^-1]}"); // pos and vel
    Tensor r = TimeIntegrators.MIDPOINT.step(stateSpaceModel, x, u, Quantity.of(2, "s"));
    assertEquals(r, Tensors.fromString("{12[m], 17[m], 6[m*s^-1], 9[m*s^-1]}"));
  }
}
