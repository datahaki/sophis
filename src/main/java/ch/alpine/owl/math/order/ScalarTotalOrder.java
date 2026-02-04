// code by jph
package ch.alpine.owl.math.order;

import java.io.Serializable;
import java.util.function.BiPredicate;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;

public enum ScalarTotalOrder {
  ;
  @SuppressWarnings("unchecked")
  public static final OrderComparator<Scalar> INSTANCE = new Order<>( //
      (BiPredicate<Scalar, Scalar> & Serializable) //
      Scalars::lessEquals);
}
