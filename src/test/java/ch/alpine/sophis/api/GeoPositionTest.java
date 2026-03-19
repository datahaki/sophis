// code by jph
package ch.alpine.sophis.api;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.hs.s.Sphere;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.UnitSystem;
import ch.alpine.tensor.sca.Chop;

class GeoPositionTest {
  @Test
  void testSimple() {
    UnitSystem.SI().apply(Quantity.of(180, "deg"));
    Tensor vec = GeoPosition.of(Tensors.fromString("{23[deg], -126[deg]}"));
    new Sphere(2).isPointQ().require(vec);
  }

  @Test
  void testRandom() {
    Tensor ps = RandomVariate.of(NormalDistribution.standard(), 30, 2);
    TensorUnaryOperator op = GeoPosition::of;
    op.slash(ps);
  }

  @Test
  void testSpec() {
    Tensor vec = GeoPosition.xyz(Tensors.fromString("{40.113[deg], -88.2612[deg]}"));
    Tensor exp = Tensors.fromString("{148008.95519178986[m], -4875595.173667141[m], 4109415.6083883415[m]}");
    Chop._02.requireClose(vec, exp);
  }
}
