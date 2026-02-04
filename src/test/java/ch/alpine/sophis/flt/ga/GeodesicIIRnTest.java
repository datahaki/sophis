// code by jph
package ch.alpine.sophis.flt.ga;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.lie.rn.RGroup;
import ch.alpine.tensor.RealScalar;

class GeodesicIIRnTest {
  @Test
  void testFailOpNull() {
    assertThrows(Exception.class, () -> GeodesicIIRn.of(null, RGroup.INSTANCE, 3, RealScalar.ONE));
  }

  @Test
  void testFailScalarNull() {
    assertThrows(Exception.class, () -> GeodesicIIRn.of(x -> x.get(0), RGroup.INSTANCE, 3, null));
  }

  @Test
  void testFailGeodesicNull() {
    assertThrows(Exception.class, () -> GeodesicIIRn.of(x -> x.get(0), null, 3, RealScalar.ONE));
  }
}
