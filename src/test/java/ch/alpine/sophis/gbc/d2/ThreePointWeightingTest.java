// code by jph
package ch.alpine.sophis.gbc.d2;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ThreePointWeightingTest {
  @Test
  void testSimple() {
    assertThrows(Exception.class, () -> new ThreePointWeighting(null));
  }
}
