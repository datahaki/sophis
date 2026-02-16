// code by jph
package ch.alpine.sophis.flt.ga;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import ch.alpine.sophis.crv.BSplineLimitMask;
import ch.alpine.sophis.flt.ga.GeodesicCenter.Splits;
import ch.alpine.sophis.math.win.UniformWindowSampler;
import ch.alpine.sophus.lie.rn.RGroup;
import ch.alpine.sophus.lie.se2.Se2Group;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.sca.win.DirichletWindow;
import ch.alpine.tensor.sca.win.GaussianWindow;
import ch.alpine.tensor.sca.win.WindowFunctions;

class GeodesicCenterTest {
  @SuppressWarnings("unchecked")
  private static final Function<Integer, Tensor> CONSTANT = (Function<Integer, Tensor> & Serializable) //
  i -> Array.of(_ -> Rational.of(1, i), i);

  @Test
  void testSimple() {
    // function generates window to compute mean: all points in window have same weight
    TensorUnaryOperator tensorUnaryOperator = GeodesicCenter.of(RGroup.INSTANCE, CONSTANT);
    for (int index = 0; index < 9; ++index) {
      Tensor apply = tensorUnaryOperator.apply(UnitVector.of(9, index));
      assertEquals(apply, Rational.of(1, 9));
    }
  }

  @Test
  void testDirichlet() {
    // function generates window to compute mean: all points in window have same weight
    TensorUnaryOperator tensorUnaryOperator = GeodesicCenter.of(RGroup.INSTANCE, DirichletWindow.FUNCTION);
    for (int index = 0; index < 9; ++index) {
      Tensor apply = tensorUnaryOperator.apply(UnitVector.of(9, index));
      assertEquals(apply, Rational.of(1, 9));
    }
  }

  @Test
  void testSe2() {
    for (WindowFunctions smoothingKernel : WindowFunctions.values()) {
      TensorUnaryOperator tensorUnaryOperator = GeodesicCenter.of(Se2Group.INSTANCE, smoothingKernel.get());
      Distribution distribution = UniformDistribution.unit();
      Tensor sequence = RandomVariate.of(distribution, 7, 3);
      Tensor tensor = tensorUnaryOperator.apply(sequence);
      assertEquals(Dimensions.of(tensor), List.of(3));
    }
  }

  @Test
  void testEvenFail() {
    TensorUnaryOperator tensorUnaryOperator = GeodesicCenter.of(RGroup.INSTANCE, CONSTANT);
    for (int index = 0; index < 9; ++index) {
      int fi = index;
      assertThrows(Exception.class, () -> tensorUnaryOperator.apply(Array.zeros(2 * fi)));
    }
  }

  @Test
  void testFail() {
    assertThrows(Exception.class, () -> GeodesicCenter.of(RGroup.INSTANCE, (UniformWindowSampler) null));
    assertThrows(Exception.class, () -> GeodesicCenter.of(RGroup.INSTANCE, (ScalarUnaryOperator) null));
    assertThrows(Exception.class, () -> GeodesicCenter.of(null, CONSTANT));
  }

  @Test
  void testSplitsMean() {
    Function<Integer, Tensor> uniformWindowSampler = UniformWindowSampler.of(DirichletWindow.FUNCTION);
    {
      Tensor tensor = GeodesicCenter.Splits.of(uniformWindowSampler.apply(3));
      assertEquals(tensor, Tensors.fromString("{1/3}"));
    }
    {
      Tensor tensor = GeodesicCenter.Splits.of(uniformWindowSampler.apply(5));
      assertEquals(tensor, Tensors.fromString("{1/2, 1/5}"));
    }
    {
      Tensor tensor = GeodesicCenter.Splits.of(uniformWindowSampler.apply(7));
      assertEquals(tensor, Tensors.fromString("{1/2, 1/3, 1/7}"));
    }
  }

  @Test
  void testSplitsBinomial() {
    {
      Tensor tensor = GeodesicCenter.Splits.of(BinomialWeights.INSTANCE.apply(1 * 2 + 1));
      assertEquals(tensor, Tensors.fromString("{1/2}"));
    }
    {
      Tensor tensor = GeodesicCenter.Splits.of(BinomialWeights.INSTANCE.apply(2 * 2 + 1));
      assertEquals(tensor, Tensors.fromString("{4/5, 3/8}"));
    }
    {
      Tensor tensor = GeodesicCenter.Splits.of(BinomialWeights.INSTANCE.apply(3 * 2 + 1));
      assertEquals(tensor, Tensors.fromString("{6/7, 15/22, 5/16}"));
    }
  }

  private static final TensorUnaryOperator TENSOR_UNARY_OPERATOR = //
      GeodesicCenter.of(RGroup.INSTANCE, BSplineLimitMask.FUNCTION);

  @Test
  void testSimple3() {
    Tensor tensor = TENSOR_UNARY_OPERATOR.apply(Tensors.vector(1, 2, 3));
    assertEquals(tensor, RealScalar.of(2));
  }

  @Test
  void testSimple5() {
    Tensor tensor = TENSOR_UNARY_OPERATOR.apply(Tensors.vector(1, 2, 3, 4, 5));
    assertEquals(tensor, RealScalar.of(3));
  }

  @Test
  void testAdvanced5() {
    Tensor tensor = TENSOR_UNARY_OPERATOR.apply(Tensors.vector(3, 2, 3, 4, 5));
    assertEquals(tensor, Rational.of(181, 60));
  }

  @Test
  void testEvenVectorFail() {
    assertThrows(Exception.class, () -> TENSOR_UNARY_OPERATOR.apply(Tensors.vector(1, 2, 3, 4)));
  }

  @Test
  void testScalarFail() {
    assertThrows(Exception.class, () -> TENSOR_UNARY_OPERATOR.apply(RealScalar.ONE));
  }

  @Test
  void testFailEven() {
    assertThrows(Exception.class, () -> GeodesicCenter.Splits.of(Tensors.vector(1, 2)));
  }

  @Test
  void testNonSymmetric() {
    assertThrows(Exception.class, () -> GeodesicCenter.Splits.of(Tensors.vector(1, 2, 2)));
  }

  @Test
  void testSplitsEvenFail() {
    Splits splits = new GeodesicCenter.Splits(UniformWindowSampler.of(GaussianWindow.FUNCTION));
    splits.apply(5);
    assertThrows(Exception.class, () -> splits.apply(4));
  }

  @Test
  void testSplitsNullFail() {
    assertThrows(Exception.class, () -> new GeodesicCenter.Splits(null));
  }
}
