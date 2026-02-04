// code by jph
package ch.alpine.sophis.crv.d2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.sophis.crv.d2.FranklinPnpoly;

class FranklinPnpolyTest {
  @Test
  void testVisibility() {
    assertEquals(FranklinPnpoly.class.getModifiers() & 1, 0);
  }
}
