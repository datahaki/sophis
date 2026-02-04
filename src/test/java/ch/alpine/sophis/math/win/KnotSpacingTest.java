// code by jph
package ch.alpine.sophis.math.win;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class KnotSpacingTest {
  @Test
  void testChordalFail() {
    assertThrows(Exception.class, () -> KnotSpacing.chordal(null));
  }
}
