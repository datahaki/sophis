// code by jph
package ch.alpine.sophis.gbc.amp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;

class SmoothRampTest {
  @Test
  void testZero() {
    Scalar scalar = new SmoothRamp(RealScalar.of(0.3)).apply(RealScalar.ZERO);
    assertEquals(scalar, RealScalar.ONE);
  }
}
