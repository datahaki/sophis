// code by jph
package ch.alpine.sophis.crv.d2.alg;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

class FranklinPnpolyTest {
  @Test
  void testVisibility() {
    assertFalse(Modifier.isPublic(FranklinPnpoly.class.getModifiers()));
  }
}
