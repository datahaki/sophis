// code by jph
package ch.alpine.sophis.var;

import java.util.OptionalInt;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.pow.Power;

/** Does not work properly with units?
 * 
 * <p>Reference:
 * "Interpolation on Scattered Data in Multidimensions" in NR, 2007 */
public class InversePowerVariogram implements ScalarUnaryOperator {
  private enum Distinct implements ScalarUnaryOperator {
    ONE {
      @Override
      public Scalar apply(Scalar r) {
        Sign.requirePositiveOrZero(r);
        return Scalars.isZero(r) //
            ? DoubleScalar.POSITIVE_INFINITY
            : r.reciprocal();
      }
    },
    TWO {
      @Override
      public Scalar apply(Scalar r) {
        return Scalars.isZero(r) //
            ? DoubleScalar.POSITIVE_INFINITY
            : r.multiply(r).reciprocal();
      }
    }
  }

  /** @param exponent for instance 2
   * @return */
  public static ScalarUnaryOperator of(Scalar exponent) {
    OptionalInt optionalInt = Scalars.optionalInt(exponent);
    if (optionalInt.isPresent()) {
      switch (optionalInt.getAsInt()) {
      case 0:
        return ConstantOneVariogram.INSTANCE;
      case 1:
        return Distinct.ONE;
      case 2:
        return Distinct.TWO;
      default:
      }
    }
    return new InversePowerVariogram(exponent);
  }

  /** @param exponent for instance 2
   * @return */
  public static ScalarUnaryOperator of(Number exponent) {
    return of(RealScalar.of(exponent));
  }

  // ---
  private final Scalar exponent;
  private final ScalarUnaryOperator power;

  private InversePowerVariogram(Scalar exponent) {
    this.exponent = exponent;
    this.power = Power.function(exponent.negate());
  }

  @Override
  public Scalar apply(Scalar r) {
    Sign.requirePositiveOrZero(r);
    return Scalars.isZero(r) //
        ? DoubleScalar.POSITIVE_INFINITY
        : power.apply(r);
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("InversePowerVariogram", exponent);
  }
}
