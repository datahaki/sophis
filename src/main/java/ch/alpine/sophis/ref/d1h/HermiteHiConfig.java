// code by jph
package ch.alpine.sophis.ref.d1h;

import java.io.Serializable;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalar;

public record HermiteHiConfig(Scalar theta, Scalar omega) implements Serializable {
  public static final HermiteHiConfig STANDARD = new HermiteHiConfig( //
      RationalScalar.of(+1, 128), //
      RationalScalar.of(-1, 16));
}
