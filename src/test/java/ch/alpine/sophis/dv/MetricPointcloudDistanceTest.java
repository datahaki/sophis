// code by jph
package ch.alpine.sophis.dv;

import org.junit.jupiter.api.Test;

import ch.alpine.sophis.dv.MetricPointcloudDistance;
import ch.alpine.sophus.lie.rn.RGroup;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.TensorScalarFunction;
import ch.alpine.tensor.lie.rot.CirclePoints;
import ch.alpine.tensor.sca.Clips;

class MetricPointcloudDistanceTest {
  @Test
  void testSimple() {
    TensorScalarFunction tensorScalarFunction = new MetricPointcloudDistance(CirclePoints.of(20), RGroup.INSTANCE);
    Scalar distance = tensorScalarFunction.apply(Tensors.vector(1, 1));
    Clips.interval(0.4, 0.5).requireInside(distance);
  }
}
