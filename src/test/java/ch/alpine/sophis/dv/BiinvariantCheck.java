// code by jph
package ch.alpine.sophis.dv;

import java.util.Collection;
import java.util.List;

import ch.alpine.sophus.lie.LieGroup;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorUnaryOperator;

/** utility class to perform simultaneous transformations */
public enum BiinvariantCheck {
  ;
  /** Hint: function is intended to use in tests to assert biinvariance
   * 
   * @param g
   * @return */
  public static Collection<TensorUnaryOperator> of(LieGroup lieGroup, Tensor g) {
    return List.of(lieGroup.actionL(g), lieGroup.actionR(g), lieGroup.conjugation(g), lieGroup::invert);
  }
}
