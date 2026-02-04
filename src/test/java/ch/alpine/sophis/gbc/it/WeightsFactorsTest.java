// code by jph
package ch.alpine.sophis.gbc.it;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.sophis.gbc.it.WeightsFactors;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;

class WeightsFactorsTest {
  @Test
  void test() throws ClassNotFoundException, IOException {
    WeightsFactors weightsFactors = new WeightsFactors(Tensors.empty(), Tensors.empty());
    Serialization.copy(weightsFactors);
  }
}
