// code by jph
package ch.alpine.sophis.math.bij;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.lie.se2.Se2AxisYProject;
import ch.alpine.sophus.lie.se2.Se2CoveringGroup;
import ch.alpine.sophus.lie.se2.Se2Matrix;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.sca.Chop;

class Se2BijectionTest {
  @Test
  void testSimple() {
    Bijection bijection = new Se2Bijection(Tensors.vector(2, 3, .3));
    Tensor vector = Tensors.vector(0.32, -0.98);
    Tensor sameor = bijection.inverse().apply(bijection.forward().apply(vector));
    Chop._14.requireClose(vector, sameor);
  }

  @Test
  void testInverse() {
    Se2Bijection se2Bijection = new Se2Bijection(Tensors.vector(2, -3, 1.3));
    Tensor matrix = se2Bijection.forward_se2();
    Tensor se2inv = Inverse.of(matrix);
    Tensor xya = Se2Matrix.toVector(se2inv);
    Se2Bijection se2Inverse = new Se2Bijection(xya);
    Chop._14.requireClose(se2Inverse.forward_se2().dot(matrix), IdentityMatrix.of(3));
    Tensor vector = Tensors.vector(5, 6);
    Tensor imaged = se2Bijection.forward().apply(vector);
    Chop._14.requireClose(se2Inverse.forward().apply(imaged), vector);
  }

  @Test
  void testSerializable() throws ClassNotFoundException, IOException {
    Se2Bijection se2Bijection = new Se2Bijection(Tensors.vector(2, -3, 1.3));
    Se2Bijection copy = Serialization.copy(se2Bijection);
    Tensor vector = Tensors.vector(0.32, -0.98);
    assertEquals(se2Bijection.forward().apply(vector), copy.forward().apply(vector));
  }

  @Test
  void testEx2NegU() {
    double speed = -1;
    Tensor u = Tensors.vector(1 * speed, 0, 0.3 * speed);
    Tensor p = Tensors.vector(-10, 3);
    Scalar t = Se2AxisYProject.of(u).apply(p);
    Chop._12.requireClose(t, RealScalar.of(5.124917769722165));
    TensorUnaryOperator se2ForwardAction = //
        new Se2Bijection(Se2CoveringGroup.INSTANCE.lieExponential().exp(u.multiply(t.negate()))).forward();
    Tensor v = se2ForwardAction.apply(p);
    Chop._13.requireClose(v, Tensors.fromString("{0, -6.672220679869088}"));
  }

  @Test
  void testEx2Neg() {
    Tensor u = Tensors.vector(1, 0, 0.3);
    Tensor p = Tensors.vector(-10, 3);
    Scalar t = Se2AxisYProject.of(u).apply(p);
    Chop._12.requireClose(t, RealScalar.of(-5.124917769722165));
    TensorUnaryOperator se2ForwardAction = //
        new Se2Bijection(Se2CoveringGroup.INSTANCE.lieExponential().exp(u.multiply(t.negate()))).forward();
    Tensor v = se2ForwardAction.apply(p);
    Chop._13.requireClose(v, Tensors.fromString("{0, -6.672220679869088}"));
  }

  @Test
  void testEx4Neg() {
    Tensor u = Tensors.vector(2, 0, 0);
    Tensor p = Tensors.vector(-10, 3);
    Scalar t = Se2AxisYProject.of(u).apply(p);
    Chop._12.requireClose(t, RealScalar.of(-5));
    TensorUnaryOperator se2ForwardAction = //
        new Se2Bijection(Se2CoveringGroup.INSTANCE.lieExponential().exp(u.multiply(t.negate()))).forward();
    Tensor v = se2ForwardAction.apply(p);
    assertEquals(v, Tensors.vector(0, 3));
  }
}
