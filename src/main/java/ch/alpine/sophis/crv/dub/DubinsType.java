// code by jph
package ch.alpine.sophis.crv.dub;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.PackageTestAccess;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Sign;

public enum DubinsType {
  LSR(+1, +0, -1, DubinsSteers.STEER_2_TURNS_DIFF_SIDE),
  RSL(-1, +0, +1, DubinsSteers.STEER_2_TURNS_DIFF_SIDE),
  LSL(+1, +0, +1, DubinsSteers.STEER_2_TURNS_SAME_SIDE),
  RSR(-1, +0, -1, DubinsSteers.STEER_2_TURNS_SAME_SIDE),
  LRL(+1, -1, +1, DubinsSteers.STEER_3_TURNS),
  RLR(-1, +1, -1, DubinsSteers.STEER_3_TURNS);

  private final Tensor signature;
  private final Tensor signatureAbs;
  private final boolean isFirstTurnRight;
  private final boolean isFirstEqualsLast;
  private final boolean containsStraight;
  private final DubinsSteers dubinsSteer;

  DubinsType(int s0s, int s1s, int s2s, DubinsSteers dubinsSteer) {
    signature = Tensors.vector(s0s, s1s, s2s).unmodifiable();
    signatureAbs = signature.maps(Abs.FUNCTION).unmodifiable();
    isFirstTurnRight = s0s == -1;
    isFirstEqualsLast = s0s == s2s;
    containsStraight = s1s == 0;
    this.dubinsSteer = dubinsSteer;
  }

  /** @return true if type is RSL or RSR or RLR */
  public boolean isFirstTurnRight() {
    return isFirstTurnRight;
  }

  /** @return true if type is LSL or RSR or LRL or RLR */
  public boolean isFirstEqualsLast() {
    return isFirstEqualsLast;
  }

  public Tensor signatureAbs() {
    return signatureAbs;
  }

  public boolean containsStraight() {
    return containsStraight;
  }

  @PackageTestAccess
  DubinsSteers dubinsSteer() {
    return dubinsSteer;
  }

  /** @param index 0, 1, or 2
   * @param radius positive
   * @return vector with first and second entry unitless.
   * result is multiplied with length of segment */
  @PackageTestAccess
  Tensor tangent(int index, Scalar radius) {
    return Tensors.of(RealScalar.ONE, RealScalar.ZERO, //
        signature.Get(index).divide(Sign.requirePositive(radius)));
  }
}
