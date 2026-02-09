// code by jph
package ch.alpine.sophis.ref.d1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.bm.LinearBiinvariantMean;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.lie.rot.CirclePoints;
import ch.alpine.tensor.num.Rationalize;
import ch.alpine.tensor.red.Nest;

class MSpline3CurveSubdivisionTest {
  @Test
  void testSimple() {
    CurveSubdivision curveSubdivision = new MSpline3CurveSubdivision(LinearBiinvariantMean.INSTANCE);
    ScalarUnaryOperator operator = Rationalize.withDenominatorLessEquals(100);
    Tensor tensor = CirclePoints.of(4).maps(operator);
    Tensor actual = Nest.of(curveSubdivision::cyclic, tensor, 1);
    ExactTensorQ.require(actual);
    Tensor expected = Tensors.fromString("{{3/4, 0}, {1/2, 1/2}, {0, 3/4}, {-1/2, 1/2}, {-3/4, 0}, {-1/2, -1/2}, {0, -3/4}, {1/2, -1/2}}");
    assertEquals(expected, actual);
  }

  @Test
  void testString() {
    Tensor curve = Tensors.vector(0, 1, 2, 3);
    CurveSubdivision curveSubdivision = new MSpline3CurveSubdivision(LinearBiinvariantMean.INSTANCE);
    Tensor refined = curveSubdivision.string(curve);
    assertEquals(refined, Tensors.fromString("{0, 1/2, 1, 3/2, 2, 5/2, 3}"));
    ExactTensorQ.require(refined);
  }

  @Test
  void testStringTwo() {
    Tensor curve = Tensors.vector(0, 1);
    CurveSubdivision curveSubdivision = new MSpline3CurveSubdivision(LinearBiinvariantMean.INSTANCE);
    Tensor refined = curveSubdivision.string(curve);
    assertEquals(refined, Tensors.fromString("{0, 1/2, 1}"));
    ExactTensorQ.require(refined);
  }

  @Test
  void testStringOne() {
    Tensor curve = Tensors.vector(1);
    CurveSubdivision curveSubdivision = new MSpline3CurveSubdivision(LinearBiinvariantMean.INSTANCE);
    Tensor refined = curveSubdivision.string(curve);
    assertEquals(refined, Tensors.fromString("{1}"));
    ExactTensorQ.require(refined);
  }

  @Test
  void testEmpty() {
    Tensor curve = Tensors.vector();
    CurveSubdivision curveSubdivision = new MSpline3CurveSubdivision(LinearBiinvariantMean.INSTANCE);
    assertEquals(curveSubdivision.string(curve), Tensors.empty());
    assertEquals(curveSubdivision.cyclic(curve), Tensors.empty());
  }

  @Test
  void testNullFail() {
    assertThrows(Exception.class, () -> new MSpline3CurveSubdivision(null));
  }
}
