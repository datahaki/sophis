// code by jph
package ch.alpine.sophis.ref.d1h;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.sophis.ref.d1.FourPointCurveSubdivision;
import ch.alpine.sophus.lie.rn.RGroup;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.jet.JetScalar;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.ply.Polynomial;

class Hermite1SubdivisionTest {
  @Test
  void testJetScalar() {
    HermiteSubdivision hermiteSubdivision = Hermite1Subdivisions.standard(RGroup.INSTANCE);
    Polynomial polynomial = Polynomial.of(Tensors.vector(2, 1, 3, 4));
    Polynomial derivative = polynomial.derivative();
    ScalarTensorFunction stf = s -> Tensors.of(polynomial.apply(s), derivative.apply(s));
    Tensor domain = Range.of(-5, 6);
    Tensor control = domain.maps(stf);
    TensorIteration tensorIteration = hermiteSubdivision.string(RealScalar.ONE, control);
    Tensor tensor = tensorIteration.iterate();
    Tensor x = Subdivide.of(-5, 5, 20);
    Tensor expect = x.maps(stf);
    assertEquals(tensor, expect);
    Tensor points = Tensor.of(control.stream().map(JetScalar::of));
    FourPointCurveSubdivision fps = new FourPointCurveSubdivision(RGroup.INSTANCE);
    Tensor string = fps.string(points);
    Tensor lifted = Tensor.of(tensor.stream().map(JetScalar::of));
    assertEquals( //
        string.extract(2, string.length() - 3), //
        lifted.extract(2, lifted.length() - 3));
  }

  @Test
  void testJetQuantity() {
    ScalarUnaryOperator lift = s -> Quantity.of(s, "s");
    HermiteSubdivision hermiteSubdivision = Hermite1Subdivisions.standard(RGroup.INSTANCE);
    Polynomial polynomial = Polynomial.of(Tensors.fromString("{2[m],3[m*s^-1],1[m*s^-2],4[m*s^-3]}"));
    Polynomial derivative = polynomial.derivative();
    ScalarTensorFunction stf = s -> Tensors.of(polynomial.apply(s), derivative.apply(s));
    Tensor domain = Range.of(-5, 6).maps(lift);
    Tensor control = domain.maps(stf);
    TensorIteration tensorIteration = hermiteSubdivision.string(lift.apply(RealScalar.ONE), control);
    Tensor tensor = tensorIteration.iterate();
    Tensor x = Subdivide.of(-5, 5, 20).maps(lift);
    Tensor expect = x.maps(stf);
    assertEquals(tensor, expect);
    Tensor points = Tensor.of(control.stream().map(JetScalar::of));
    FourPointCurveSubdivision fps = new FourPointCurveSubdivision(RGroup.INSTANCE);
    Tensor string = fps.string(points);
    Tensor lifted = Tensor.of(tensor.stream().map(JetScalar::of));
    assertEquals( //
        string.extract(2, string.length() - 3), //
        lifted.extract(2, lifted.length() - 3));
  }

  @Test
  void testQuantity() {
    new HermiteSubdivisionQ(Hermite1Subdivisions.standard(RGroup.INSTANCE)).checkQuantity();
  }

  @Test
  void testNullFail() {
    assertThrows(Exception.class, () -> Hermite1Subdivisions.standard(null));
  }
}
