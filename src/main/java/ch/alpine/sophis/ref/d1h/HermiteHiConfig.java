// code by jph
package ch.alpine.sophis.ref.d1h;

import java.io.Serializable;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.Scalar;

public record HermiteHiConfig(Scalar theta, Scalar omega) implements Serializable {
  public static final HermiteHiConfig STANDARD = new HermiteHiConfig( //
      Rational.of(+1, 128), //
      Rational.of(-1, 16));
}
