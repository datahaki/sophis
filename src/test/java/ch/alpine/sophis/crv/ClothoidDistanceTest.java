// code by jph
package ch.alpine.sophis.crv;

import org.junit.jupiter.api.Test;

import ch.alpine.sophis.api.CurveOperator;
import ch.alpine.sophis.ref.d1.LaneRiesenfeldCurveSubdivision;
import ch.alpine.sophus.clt.ClothoidBuilders;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.red.Nest;
import ch.alpine.tensor.sca.Chop;

class ClothoidDistanceTest {
  @Test
  void testSimple() {
    Chop._10.requireClose(ClothoidDistance.SE2_ANALYTIC.norm(Tensors.vector(10, 0, 0)), RealScalar.of(10));
    Chop._10.requireClose(ClothoidDistance.SE2_COVERING.norm(Tensors.vector(10, 0, 0)), RealScalar.of(10));
    Chop._10.requireClose(ClothoidDistance.SE2_ANALYTIC.norm(Tensors.vector(23, 0, 0)), RealScalar.of(23));
    Chop._10.requireClose(ClothoidDistance.SE2_COVERING.norm(Tensors.vector(23, 0, 0)), RealScalar.of(23));
  }

  @SuppressWarnings("unused")
  @Test
  void testSimple2() {
    Tensor q = Tensors.vector(-2.05, 0, 0);
    CurveOperator lrL = LaneRiesenfeldCurveSubdivision.of(ClothoidBuilders.SE2_LEGENDRE.clothoidBuilder(), 1);
    CurveOperator lrA = LaneRiesenfeldCurveSubdivision.of(ClothoidBuilders.SE2_ANALYTIC.clothoidBuilder(), 1);
    {
      Tensor pL = lrL.string(Tensors.of(Array.zeros(3), q)).get(1);
      Tolerance.CHOP.requireClose(pL, Tensors.vector(-1.025, 0, 4.245082897851892));
      Tensor mLA = lrA.string(Tensors.of(pL, q)).get(1);
      Tensor mLL = lrL.string(Tensors.of(pL, q)).get(1);
      Chop._02.requireClose(mLA, mLL);
      // System.out.println(mLL);
      // {-1.7030138036773317, 0.2166473557662918, 2.430899623066361}
      // Tensor mLO = ComplexClothoidCurve.INSTANCE.curve(pL, q).apply(Rational.HALF);
      // System.out.println(mLO);
      // Tensor mL3 = new ClothoidCurve3(pL, q).apply(Rational.HALF);
      // System.out.println(mL3);
      // System.out.println("---");
    }
    {
      Tensor pA = lrA.string(Tensors.of(Array.zeros(3), q)).get(1);
      Tolerance.CHOP.requireClose(pA, Tensors.vector(-1.025, 0, 4.245082897851892));
      Tensor mAA = lrA.string(Tensors.of(pA, q)).get(1);
      Chop._05.requireClose(mAA, Tensors.vector(-1.8944972463160186, -0.7629704708387287, 3.1340144486447543));
      Tensor mAL = lrL.string(Tensors.of(pA, q)).get(1);
      Chop._02.requireClose(mAA, mAL);
      // System.out.println(mAA);
      // {-1.8944972463160186, -0.7629704708387287, 3.1340144486447543}
      // Tensor mAO = ComplexClothoidCurve.INSTANCE.curve(pA, q).apply(Rational.HALF);
      // System.out.println(mAO);
      // Tensor mA3 = new ClothoidCurve3(pA, q).apply(Rational.HALF);
      // System.out.println(mA3);
      // System.out.println("---");
    }
    {
      Tensor subL = Nest.of(lrL::string, Tensors.of(Array.zeros(3), q), 2);
      // System.out.println(Pretty.of(subL.map(Round._4)));
    }
    {
      Tensor subA = Nest.of(lrA::string, Tensors.of(Array.zeros(3), q), 2);
      // System.out.println(Pretty.of(subA.map(Round._4)));
    }
  }
}
