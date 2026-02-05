// code by jph
package ch.alpine.sophis.flt.ga;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.lie.rn.RGroup;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.ext.Serialization;

class Regularization2StepTest {
  private static final TensorUnaryOperator STRING = //
      new Regularization2Step(RGroup.INSTANCE, RationalScalar.of(1, 4))::string;

  @Test
  void testLo() throws ClassNotFoundException, IOException {
    Tensor signal = Tensors.vector(1, 0, 0, 0, 0);
    Tensor tensor = Serialization.copy(STRING).apply(signal);
    ExactTensorQ.require(tensor);
    assertEquals(tensor, Tensors.vector(1, 0.125, 0, 0, 0));
    TensorUnaryOperator tensorUnaryOperator = new Regularization2Step(RGroup.INSTANCE, RealScalar.of(0.25))::string;
    assertEquals(tensor, tensorUnaryOperator.apply(signal));
  }

  @Test
  void testHi() {
    Tensor signal = Tensors.vector(0, 0, 0, 0, 1);
    Tensor tensor = STRING.apply(signal);
    ExactTensorQ.require(tensor);
    assertEquals(tensor, Tensors.vector(0, 0, 0, 0.125, 1));
    TensorUnaryOperator tensorUnaryOperator = new Regularization2Step(RGroup.INSTANCE, RealScalar.of(0.25))::string;
    assertEquals(tensor, tensorUnaryOperator.apply(signal));
  }

  @Test
  void testEmpty() {
    assertEquals(STRING.apply(Tensors.empty()), Tensors.empty());
  }

  @Test
  void testSingle() {
    assertEquals(STRING.apply(Tensors.vector(2)), Tensors.vector(2));
  }

  @Test
  void testTuple() {
    assertEquals(STRING.apply(Tensors.vector(3, 2)), Tensors.vector(3, 2));
  }

  @Test
  void testSimple() {
    TensorUnaryOperator STRING = //
        new Regularization2Step(RGroup.INSTANCE, RationalScalar.of(1, 2))::string;
    Tensor signal = Tensors.vector(1, 1, 1, 2, 1, 1, 1, 1, 1, 1);
    Tensor tensor = STRING.apply(signal);
    ExactTensorQ.require(tensor);
    assertEquals(tensor, Tensors.fromString("{1, 1, 5/4, 3/2, 5/4, 1, 1, 1, 1, 1}"));
  }

  @Test
  void testMatrix() {
    TensorUnaryOperator STRING = //
        new Regularization2Step(RGroup.INSTANCE, RationalScalar.of(1, 2))::string;
    Tensor signal = Tensors.fromString("{{1, 2}, {2, 2}, {3, 2}, {4, 2}, {3, 3}}");
    Tensor tensor = STRING.apply(signal);
    ExactTensorQ.require(tensor);
    assertEquals(tensor, Tensors.fromString("{{1, 2}, {2, 2}, {3, 2}, {7/2, 9/4}, {3, 3}}"));
  }

  @Test
  void testCZero() {
    Tensor signal = Tensors.vector(1, 1, 1, 2, 1, 1, 3, 1, 1, 1);
    Tensor tensor = new Regularization2Step(RGroup.INSTANCE, RealScalar.ZERO).string(signal);
    ExactTensorQ.require(tensor);
    assertEquals(tensor, signal);
  }

  @Test
  void testCScalarFail() {
    TensorUnaryOperator tensorUnaryOperator = new Regularization2Step(RGroup.INSTANCE, RationalScalar.HALF)::string;
    assertThrows(Exception.class, () -> tensorUnaryOperator.apply(RealScalar.ZERO));
  }

  private static final TensorUnaryOperator CYCLIC = //
      new Regularization2Step(RGroup.INSTANCE, RationalScalar.of(1, 4))::cyclic;

  @Test
  void testCLo() {
    Tensor signal = Tensors.vector(1, 0, 0, 0, 0);
    Tensor tensor = CYCLIC.apply(signal);
    assertEquals(tensor, Tensors.fromString("{3/4, 1/8, 0, 0, 1/8}"));
    TensorUnaryOperator regularization2StepCyclic = new Regularization2Step(RGroup.INSTANCE, RealScalar.of(0.25))::cyclic;
    assertEquals(tensor, regularization2StepCyclic.apply(signal));
  }

  @Test
  void testCHi() {
    Tensor signal = Tensors.vector(0, 0, 0, 0, 1);
    Tensor tensor = CYCLIC.apply(signal);
    assertEquals(tensor, Tensors.fromString("{1/8, 0, 0, 1/8, 3/4}"));
    TensorUnaryOperator tensorUnaryOperator = new Regularization2Step(RGroup.INSTANCE, RealScalar.of(0.25))::cyclic;
    assertEquals(tensor, tensorUnaryOperator.apply(signal));
  }

  @Test
  void testCEmpty() {
    assertEquals(CYCLIC.apply(Tensors.empty()), Tensors.empty());
  }

  @Test
  void testCSingle() {
    assertEquals(CYCLIC.apply(Tensors.vector(3)), Tensors.vector(3));
  }

  @Test
  void testCTuple() {
    Tensor tensor = CYCLIC.apply(Tensors.vector(3, 2));
    ExactTensorQ.require(tensor);
    assertEquals(tensor, Tensors.fromString("{11/4, 9/4}"));
  }

  @Test
  void testZero() {
    Tensor signal = Tensors.vector(1, 1, 1, 2, 1, 1, 3, 1, 1, 1);
    Tensor tensor = new Regularization2Step(RGroup.INSTANCE, RealScalar.ZERO).cyclic(signal);
    ExactTensorQ.require(tensor);
    assertEquals(tensor, signal);
  }

  @Test
  void testScalarFail() {
    TensorUnaryOperator tensorUnaryOperator = new Regularization2Step(RGroup.INSTANCE, RationalScalar.HALF)::cyclic;
    assertThrows(Exception.class, () -> tensorUnaryOperator.apply(RealScalar.ZERO));
  }
}
