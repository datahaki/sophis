// code by jph
package ch.alpine.sophis.hull.d3;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;

class StaticHelperTest {
  @Test
  void test() {
    double nextUp = Math.nextUp(1.0) - 1;
    assertEquals(StaticHelper.DOUBLE_PREC, RealScalar.of(nextUp));
  }
}
