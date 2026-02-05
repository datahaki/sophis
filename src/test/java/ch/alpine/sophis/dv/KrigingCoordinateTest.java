// code by jph
package ch.alpine.sophis.dv;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.lie.rn.RGroup;
import ch.alpine.tensor.mat.IdentityMatrix;

class KrigingCoordinateTest {
  @Test
  void testNull1Fail() {
    assertThrows(Exception.class, () -> new KrigingCoordinate(RGroup.INSTANCE, t -> t, null));
  }

  @Test
  void testNull2Fail() {
    assertThrows(Exception.class, () -> new KrigingCoordinate(RGroup.INSTANCE, null, IdentityMatrix.of(4)));
  }

  @Test
  void testNull3Fail() {
    assertThrows(Exception.class, () -> new KrigingCoordinate(null, t -> t, IdentityMatrix.of(4)));
  }
}
