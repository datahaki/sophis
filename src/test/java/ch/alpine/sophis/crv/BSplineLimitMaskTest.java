// code by jph
package ch.alpine.sophis.crv;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensors;

class BSplineLimitMaskTest {
  @Test
  void testLimitMask() {
    assertEquals(BSplineLimitMask.FUNCTION.apply(0 * 2 + 1), Tensors.fromString("{1}"));
    assertEquals(BSplineLimitMask.FUNCTION.apply(1 * 2 + 1), Tensors.fromString("{1/6, 2/3, 1/6}"));
    assertEquals(BSplineLimitMask.FUNCTION.apply(2 * 2 + 1), Tensors.fromString("{1/120, 13/60, 11/20, 13/60, 1/120}"));
    assertEquals(BSplineLimitMask.FUNCTION.apply(3 * 2 + 1), Tensors.fromString("{1/5040, 1/42, 397/1680, 151/315, 397/1680, 1/42, 1/5040}"));
  }

  @Test
  void testEvenFail() {
    for (int i = 0; i < 10; ++i) {
      int fi = i;
      assertThrows(Exception.class, () -> BSplineLimitMask.FUNCTION.apply(fi * 2));
    }
  }

  @Test
  void testNegativeFail() {
    for (int i = 1; i < 4; ++i) {
      int fi = i;
      assertThrows(Exception.class, () -> BSplineLimitMask.FUNCTION.apply(-fi));
    }
  }
}
