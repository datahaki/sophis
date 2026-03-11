// code by jph
package ch.alpine.sophis.crv.d2.ex;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.sophis.crv.d2.PolygonArea;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

class Box2DTest {
  @Test
  void testSquare() {
    Tensor SQUARE = Tensors.fromString("{{0, 0}, {1, 0}, {1, 1}, {0, 1}}").unmodifiable();
    assertEquals(SQUARE, Box2D.UNIT_SQUARE);
    assertEquals(PolygonArea.of(SQUARE), RealScalar.ONE);
  }

  @Test
  void testCorners() {
    Tensor CORNERS = Tensors.fromString("{{-1, -1}, {1, -1}, {1, 1}, {-1, 1}}").unmodifiable();
    assertEquals(CORNERS, Box2D.ABSOLUTE_ONE);
    assertEquals(PolygonArea.of(CORNERS), RealScalar.of(4));
  }
}
