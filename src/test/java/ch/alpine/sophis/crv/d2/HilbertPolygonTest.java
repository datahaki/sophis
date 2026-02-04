// code by jph
package ch.alpine.sophis.crv.d2;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.sophis.crv.d2.HilbertPolygon;

class HilbertPolygonTest {
  @Test
  void testZeroClosedFail() {
    assertThrows(Exception.class, () -> HilbertPolygon.of(0));
  }
}
