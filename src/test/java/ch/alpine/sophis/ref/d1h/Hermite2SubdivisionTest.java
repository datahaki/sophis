// code by jph
package ch.alpine.sophis.ref.d1h;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.lie.rn.RGroup;

class Hermite2SubdivisionTest {
  @Test
  void testQuantity() {
    new HermiteSubdivisionQ(Hermite2Subdivisions.standard(RGroup.INSTANCE)).checkQuantity();
  }

  @Test
  void testNullFail() {
    assertThrows(Exception.class, () -> Hermite2Subdivisions.standard(null));
  }
}
