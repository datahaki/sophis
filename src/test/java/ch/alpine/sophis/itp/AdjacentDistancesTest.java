// code by jph
package ch.alpine.sophis.itp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.sophis.itp.AdjacentDistances;
import ch.alpine.sophus.lie.rn.RGroup;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.num.Pi;

class AdjacentDistancesTest {
  @Test
  void testR2() throws ClassNotFoundException, IOException {
    Tensor tensor = Serialization.copy(AdjacentDistances.of(RGroup.INSTANCE)) //
        .apply(Tensors.fromString("{{1, 2}, {2, 2}, {2, 4}}"));
    assertEquals(ExactTensorQ.require(tensor), Tensors.vector(1, 2));
  }

  @Test
  void testR2Single() {
    Tensor tensor = AdjacentDistances.of(RGroup.INSTANCE).apply(Tensors.fromString("{{1, 2}}"));
    assertEquals(tensor, Tensors.empty());
  }

  @Test
  void testR2SingleFail() {
    assertThrows(Exception.class, () -> AdjacentDistances.of(null));
  }

  @Test
  void testScalarFail() {
    assertThrows(Exception.class, () -> AdjacentDistances.of(RGroup.INSTANCE).apply(Pi.HALF));
  }
}
