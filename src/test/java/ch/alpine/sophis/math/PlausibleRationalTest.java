// code by jph
package ch.alpine.sophis.math;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.lie.su.Su3Algebra;
import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.lie.KillingForm;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.red.Diagonal;
import ch.alpine.tensor.sca.Re;
import ch.alpine.tensor.spa.SparseArray;

class PlausibleRationalTest {
  @Test
  void testComplex() {
    Scalar scalar = PlausibleRational.of(10).apply(ComplexScalar.of(0.5, 0.2344523));
    assertEquals(Rational.HALF.toString(), Re.FUNCTION.apply(scalar).toString());
  }

  @Test
  void testQuantity() {
    Scalar scalar = PlausibleRational.of(10).apply(Scalars.fromString("0.333333333333333333333333333[m]"));
    assertEquals(scalar.toString(), "1/3[m]");
  }

  @Test
  void testSimple() {
    Su3Algebra su3Algebra = Su3Algebra.INSTANCE;
    Tensor ad = su3Algebra.ad();
    ad = ad.maps(PlausibleRational.of(10));
    assertInstanceOf(SparseArray.class, ad);
    Tensor form = KillingForm.of(ad);
    Tolerance.CHOP.requireClose(Diagonal.of(form), ConstantArray.of(RealScalar.of(3), 8));
  }

  @Test
  void testPlausibleRational() {
    ScalarUnaryOperator suo = PlausibleRational.of(10);
    assertDoesNotThrow(() -> Serialization.copy(suo));
    Scalar scalar = suo.apply(RealScalar.of(0.0));
    ExactScalarQ.require(scalar);
    assertEquals(suo.apply(Pi.VALUE), Pi.VALUE);
  }
}
