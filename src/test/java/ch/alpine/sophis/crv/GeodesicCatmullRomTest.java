// code by ob
package ch.alpine.sophis.crv;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.sophis.math.win.KnotSpacing;
import ch.alpine.sophus.lie.rn.RGroup;
import ch.alpine.sophus.lie.se2.Se2Group;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.sca.Chop;

class GeodesicCatmullRomTest {
  @Test
  void testUniformInterpolatory() throws ClassNotFoundException, IOException {
    Tensor control = RandomVariate.of(UniformDistribution.unit(), 5, 3);
    TensorUnaryOperator centripedalKnotSpacing = KnotSpacing.uniform();
    Tensor knots = centripedalKnotSpacing.apply(control);
    GeodesicCatmullRom geodesicCatmullRom = //
        Serialization.copy(GeodesicCatmullRom.of(Se2Group.INSTANCE, knots, control));
    // ---
    Tensor actual1 = geodesicCatmullRom.apply(RealScalar.of(1));
    Tensor expected1 = control.get(1);
    // ----
    Tensor actual2 = geodesicCatmullRom.apply(RealScalar.of(2));
    Tensor expected2 = control.get(2);
    // ----
    Chop._10.requireClose(actual2, expected2);
    Chop._10.requireClose(actual1, expected1);
    assertEquals(geodesicCatmullRom.control(), control);
  }

  @Test
  void testLengthFail() {
    Tensor control = RandomVariate.of(UniformDistribution.unit(), 3, 7);
    Tensor knots = KnotSpacing.uniform().apply(control);
    assertThrows(Exception.class, () -> GeodesicCatmullRom.of(RGroup.INSTANCE, knots, control));
  }

  @Test
  void testKnotsInconsistentFail() {
    Tensor control = RandomVariate.of(UniformDistribution.unit(), 5, 7);
    Tensor knots = KnotSpacing.uniform().apply(control);
    GeodesicCatmullRom.of(RGroup.INSTANCE, knots, control);
    assertThrows(Exception.class, () -> GeodesicCatmullRom.of(RGroup.INSTANCE, knots.extract(0, knots.length() - 1), control));
  }
}
