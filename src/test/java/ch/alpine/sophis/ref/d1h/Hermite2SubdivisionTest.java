// code by jph
package ch.alpine.sophis.ref.d1h;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.sophis.ref.d1h.Hermite2Subdivisions;
import ch.alpine.sophus.lie.rn.RGroup;

class Hermite2SubdivisionTest {
  @Test
  void testQuantity() throws ClassNotFoundException, IOException {
    new HermiteSubdivisionQ(Hermite2Subdivisions.standard(RGroup.INSTANCE)).checkQuantity();
  }

  @Test
  void testNullFail() {
    assertThrows(Exception.class, () -> Hermite2Subdivisions.standard(null));
  }
}
