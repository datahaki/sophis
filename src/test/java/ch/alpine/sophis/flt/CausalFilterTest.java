// code by jph
package ch.alpine.sophis.flt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.Serializable;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import ch.alpine.sophis.flt.CausalFilter;
import ch.alpine.sophis.flt.ga.GeodesicExtrapolation;
import ch.alpine.sophis.flt.ga.GeodesicFIR2;
import ch.alpine.sophis.flt.ga.GeodesicIIR1;
import ch.alpine.sophis.flt.ga.GeodesicIIR2;
import ch.alpine.sophis.flt.ga.GeodesicIIRnFilter;
import ch.alpine.sophus.lie.rn.RGroup;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.sca.win.DirichletWindow;

class CausalFilterTest {
  @Test
  void testIIR1() throws ClassNotFoundException, IOException {
    @SuppressWarnings("unchecked")
    TensorUnaryOperator causalFilter = Serialization.copy(CausalFilter.of( //
        (Supplier<TensorUnaryOperator> & Serializable) //
        () -> new GeodesicIIR1(RGroup.INSTANCE, RationalScalar.HALF)));
    {
      Tensor tensor = causalFilter.apply(UnitVector.of(10, 0));
      assertEquals(tensor, Tensors.fromString("{1, 1/2, 1/4, 1/8, 1/16, 1/32, 1/64, 1/128, 1/256, 1/512}"));
      ExactTensorQ.require(tensor);
    }
    {
      Tensor tensor = causalFilter.apply(UnitVector.of(10, 1));
      assertEquals(tensor, Tensors.fromString("{0, 1/2, 1/4, 1/8, 1/16, 1/32, 1/64, 1/128, 1/256, 1/512}"));
      ExactTensorQ.require(tensor);
    }
  }

  @Test
  void testIIR2a() throws ClassNotFoundException, IOException {
    @SuppressWarnings("unchecked")
    TensorUnaryOperator causalFilter = Serialization.copy(CausalFilter.of(//
        (Supplier<TensorUnaryOperator> & Serializable) //
        () -> new GeodesicIIR2(RGroup.INSTANCE, RationalScalar.HALF)));
    Tensor tensor = causalFilter.apply(UnitVector.of(10, 0));
    ExactTensorQ.require(tensor);
  }

  @Test
  void testIIR2b() {
    TensorUnaryOperator geodesicExtrapolation = GeodesicExtrapolation.of(RGroup.INSTANCE, DirichletWindow.FUNCTION);
    TensorUnaryOperator causalFilter = GeodesicIIRnFilter.of(geodesicExtrapolation, RGroup.INSTANCE, 2, RationalScalar.HALF);
    Tensor tensor = causalFilter.apply(UnitVector.of(10, 0));
    ExactTensorQ.require(tensor);
  }

  @Test
  void testFIR2() {
    TensorUnaryOperator causalFilter = CausalFilter.of(() -> GeodesicFIR2.of(RGroup.INSTANCE, RationalScalar.HALF));
    {
      Tensor tensor = causalFilter.apply(UnitVector.of(10, 0));
      assertEquals(tensor, Tensors.fromString("{1, 0, -1/2, 0, 0, 0, 0, 0, 0, 0}"));
      ExactTensorQ.require(tensor);
    }
    {
      Tensor tensor = causalFilter.apply(UnitVector.of(10, 1));
      assertEquals(tensor, Tensors.fromString("{0, 1, 1, -1/2, 0, 0, 0, 0, 0, 0}"));
      ExactTensorQ.require(tensor);
    }
  }

  @Test
  void testFailNull() {
    assertThrows(Exception.class, () -> CausalFilter.of(null));
  }
}
