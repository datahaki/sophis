// code by jph
package ch.alpine.sophis.crv;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.sophis.crv.AbstractBSplineInterpolation;
import ch.alpine.sophis.crv.GeodesicBSplineInterpolation;
import ch.alpine.sophis.crv.AbstractBSplineInterpolation.Iteration;
import ch.alpine.sophus.lie.rn.RGroup;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.N;

class GeodesicBSplineInterpolationTest {
  /* package */ static Tensor pet(Tensor prev, Tensor eval, Tensor goal) {
    return prev.add(goal.subtract(eval));
  }

  @Test
  void testApplyRn() {
    Tensor target = Tensors.vector(1, 2, 0, 2, 1, 3).map(N.DOUBLE);
    AbstractBSplineInterpolation geodesicBSplineInterpolation = //
        new GeodesicBSplineInterpolation(RGroup.INSTANCE, 2, target);
    Tensor control = geodesicBSplineInterpolation.apply();
    Tensor vector = Tensors.vector(1, 2.7510513036161504, -0.922624053826282, 2.784693019343523, 0.21446593776315992, 3);
    Chop._10.requireClose(control, vector);
  }

  @Test
  void testMoveRn() {
    Tensor tensor = RandomVariate.of(DiscreteUniformDistribution.of(2, 100), 3, 5);
    AbstractBSplineInterpolation geodesicBSplineInterpolation = //
        new GeodesicBSplineInterpolation(RGroup.INSTANCE, 2, tensor);
    Tensor prev = tensor.get(0);
    Tensor eval = tensor.get(1);
    Tensor goal = tensor.get(2);
    Tensor pos0 = geodesicBSplineInterpolation.move(prev, eval, goal);
    Tensor pos1 = pet(prev, eval, goal);
    assertEquals(pos0, pos1);
    ExactTensorQ.require(pos0);
    ExactTensorQ.require(pos1);
    Iteration iteration = geodesicBSplineInterpolation.init();
    assertEquals(iteration.steps(), 0);
  }
}
