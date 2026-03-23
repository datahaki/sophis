// code by jph
package ch.alpine.sophis.ref.d1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import ch.alpine.sophis.api.CurveOperator;
import ch.alpine.sophus.clt.ClothoidBuilder;
import ch.alpine.sophus.clt.ClothoidBuilders;
import ch.alpine.sophus.lie.rn.RGroup;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.lie.rot.CirclePoints;
import ch.alpine.tensor.num.Rationalize;

class BSpline1CurveSubdivisionTest {
  private static final ClothoidBuilder CLOTHOID_BUILDER = ClothoidBuilders.SE2_ANALYTIC.clothoidBuilder();

  @Test
  void testCyclic() {
    CurveOperator curveOperator = new BSpline1CurveSubdivision(RGroup.INSTANCE);
    ScalarUnaryOperator operator = Rationalize.withDenominatorLessEquals(100);
    Tensor tensor = CirclePoints.of(4).maps(operator);
    Tensor actual = curveOperator.cyclic(tensor);
    ExactTensorQ.require(actual);
    Tensor expected = Tensors.fromString("{{1, 0}, {1/2, 1/2}, {0, 1}, {-1/2, 1/2}, {-1, 0}, {-1/2, -1/2}, {0, -1}, {1/2, -1/2}}");
    assertEquals(expected, actual);
  }

  @Test
  void testString() {
    CurveOperator curveOperator = new BSpline1CurveSubdivision(RGroup.INSTANCE);
    Tensor string = curveOperator.string(Tensors.fromString("{{0, 10}, {1, 12}}"));
    assertEquals(string, Tensors.fromString("{{0, 10}, {1/2, 11}, {1, 12}}"));
    ExactTensorQ.require(string);
  }

  @Test
  void testStringTwo() {
    Tensor curve = Tensors.vector(0, 1);
    CurveOperator curveOperator = new BSpline1CurveSubdivision(RGroup.INSTANCE);
    Tensor refined = curveOperator.string(curve);
    assertEquals(refined, Tensors.fromString("{0, 1/2, 1}"));
    ExactTensorQ.require(refined);
  }

  @Test
  void testStringRange() {
    int length = 9;
    Tensor curve = Range.of(0, length + 1);
    CurveOperator curveOperator = new BSpline1CurveSubdivision(RGroup.INSTANCE);
    Tensor refined = curveOperator.string(curve);
    assertEquals(refined, Subdivide.of(0, length, length * 2));
    ExactTensorQ.require(refined);
  }

  @Test
  void testStringOne() {
    Tensor curve = Tensors.vector(8);
    CurveOperator curveOperator = new BSpline1CurveSubdivision(RGroup.INSTANCE);
    Tensor refined = curveOperator.string(curve);
    assertEquals(refined, Tensors.fromString("{8}"));
    ExactTensorQ.require(refined);
  }

  @Test
  void testStringEmpty() {
    Tensor curve = Tensors.vector();
    CurveOperator curveOperator = new BSpline1CurveSubdivision(RGroup.INSTANCE);
    Tensor refined = curveOperator.string(curve);
    assertTrue(Tensors.isEmpty(refined));
    ExactTensorQ.require(refined);
  }

  @Test
  void testCyclicEmpty() {
    Tensor curve = Tensors.vector();
    CurveOperator curveOperator = new BSpline1CurveSubdivision(RGroup.INSTANCE);
    Tensor refined = curveOperator.cyclic(curve);
    assertTrue(Tensors.isEmpty(refined));
    ExactTensorQ.require(refined);
  }

  @Test
  void testCirclePoints() {
    CurveOperator curveOperator = new BSpline1CurveSubdivision(RGroup.INSTANCE);
    for (int n = 3; n < 10; ++n) {
      Tensor tensor = curveOperator.cyclic(CirclePoints.of(n));
      Tensor filter = Tensor.of(IntStream.range(0, tensor.length() / 2) //
          .map(i -> i * 2) //
          .mapToObj(tensor::get));
      assertEquals(filter, CirclePoints.of(n));
    }
  }

  @Test
  void testSingleton() {
    Tensor singleton = Tensors.of(Tensors.vector(1, 2, 3));
    CurveOperator curveOperator = new BSpline1CurveSubdivision(CLOTHOID_BUILDER);
    assertEquals(curveOperator.cyclic(singleton), singleton);
    assertEquals(curveOperator.string(singleton), singleton);
  }

  @Test
  void testSerializable() throws ClassNotFoundException, IOException {
    TensorUnaryOperator fps = new BSpline1CurveSubdivision(RGroup.INSTANCE)::cyclic;
    TensorUnaryOperator copy = Serialization.copy(fps);
    assertEquals(copy.apply(CirclePoints.of(10)), fps.apply(CirclePoints.of(10)));
  }

  @Test
  void testNullFail() {
    assertThrows(Exception.class, () -> new BSpline1CurveSubdivision(null));
  }
}
