// code by jph
package ch.alpine.sophis.ref.d1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.sophis.api.CurveOperator;
import ch.alpine.sophus.clt.ClothoidBuilder;
import ch.alpine.sophus.clt.ClothoidBuilders;
import ch.alpine.sophus.lie.rn.RGroup;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.chq.ExactTensorQ;

class BSpline6CurveSubdivisionTest {
  private static final ClothoidBuilder CLOTHOID_BUILDER = ClothoidBuilders.SE2_ANALYTIC.clothoidBuilder();

  @Test
  void testSimple() {
    CurveOperator curveSubdivision = //
        BSpline6CurveSubdivision.of(RGroup.INSTANCE);
    Tensor tensor = curveSubdivision.cyclic(UnitVector.of(5, 0));
    assertEquals(tensor, //
        Tensors.fromString("{35/64, 21/64, 7/64, 1/64, 0, 0, 1/64, 7/64, 21/64, 35/64}"));
    ExactTensorQ.require(tensor);
  }

  @Test
  void testEmpty() {
    Tensor curve = Tensors.vector();
    CurveOperator curveSubdivision = BSpline6CurveSubdivision.of(RGroup.INSTANCE);
    assertEquals(curveSubdivision.cyclic(curve), Tensors.empty());
  }

  @Test
  void testSingleton() {
    Tensor singleton = Tensors.of(Tensors.vector(1, 2, 3));
    CurveOperator curveSubdivision = BSpline6CurveSubdivision.of(CLOTHOID_BUILDER);
    assertEquals(curveSubdivision.cyclic(singleton), singleton);
  }

  @Test
  void testNullFail() {
    assertThrows(Exception.class, () -> BSpline6CurveSubdivision.of(null));
  }
}
