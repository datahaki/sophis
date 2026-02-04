// code by jph
package ch.alpine.sophis.decim;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.sophis.decim.HsLineDistance;
import ch.alpine.sophis.decim.LineDistance;
import ch.alpine.sophis.decim.SymmetricLineDistance;
import ch.alpine.sophus.lie.se2.Se2CoveringGroup;
import ch.alpine.tensor.ext.Serialization;

class SymmetricLineDistanceTest {
  @Test
  void testSimple() throws ClassNotFoundException, IOException {
    LineDistance lineDistance = new SymmetricLineDistance(new HsLineDistance(Se2CoveringGroup.INSTANCE));
    Serialization.copy(lineDistance);
  }
}
