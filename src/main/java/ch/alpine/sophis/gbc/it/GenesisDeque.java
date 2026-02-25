// code by jph
package ch.alpine.sophis.gbc.it;

import java.util.Deque;

import ch.alpine.sophis.api.Genesis;
import ch.alpine.tensor.Tensor;

public interface GenesisDeque extends Genesis {
  Deque<WeightsFactors> deque(Tensor levers);
}
