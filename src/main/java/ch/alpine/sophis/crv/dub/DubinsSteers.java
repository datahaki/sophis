// code by jph
// adapted from https://github.com/mcapino/trajectorytools
// adapted from https://github.com/AtsushiSakai/PythonRobotics
package ch.alpine.sophis.crv.dub;

import java.util.Optional;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.tri.ArcCos;
import ch.alpine.tensor.sca.tri.ArcSin;
import ch.alpine.tensor.sca.tri.Cos;

/* package */ enum DubinsSteers {
  STEER_2_TURNS_DIFF_SIDE {
    @Override // from DubinsSteer
    public Optional<Tensor> steer(Scalar dist_tr, Scalar th_tr, Scalar th_total, Scalar radius) {
      Scalar aux = radius.add(radius).divide(dist_tr);
      if (StaticHelper.greaterThanOne(aux)) // if intersecting, no tangent line
        return Optional.empty();
      Scalar th_aux = ArcSin.FUNCTION.apply(aux);
      return Optional.of(Tensors.of( //
          radius.multiply(StaticHelper.principalValue(th_tr.add(th_aux))), //
          dist_tr.multiply(Cos.FUNCTION.apply(th_aux)), //
          radius.multiply(StaticHelper.principalValue(th_tr.add(th_aux).subtract(th_total)))));
    }
  },
  STEER_2_TURNS_SAME_SIDE {
    @Override // from DubinsSteer
    public Optional<Tensor> steer(Scalar dist_tr, Scalar th_tr, Scalar th_total, Scalar radius) {
      return Optional.of(Tensors.of( //
          radius.multiply(th_tr), //
          dist_tr, //
          radius.multiply(StaticHelper.principalValue(th_total.subtract(th_tr)))));
    }
  },
  STEER_3_TURNS {
    private static final Scalar FOUR = RealScalar.of(4.0);

    @Override // from DubinsSteer
    public Optional<Tensor> steer(Scalar dist_tr, Scalar th_tr, Scalar th_total, Scalar radius) {
      Scalar aux = dist_tr.divide(FOUR).divide(radius);
      if (StaticHelper.greaterThanOne(aux))
        return Optional.empty();
      Scalar th_aux = ArcCos.FUNCTION.apply(aux);
      Scalar th_pha = Pi.HALF.add(th_aux);
      return Optional.of(Tensors.of( //
          StaticHelper.principalValue(th_tr.add(th_pha)), //
          Pi.VALUE.add(th_aux).add(th_aux), //
          StaticHelper.principalValue(th_total.subtract(th_tr).add(th_pha))).multiply(radius));
    }
  };

  /** @param dist_tr non-negative
   * @param th_tr in the interval [0, 2*pi)
   * @param th_total in the interval [0, 2*pi)
   * @param radius positive
   * @return vector with 3 entries as length of dubins path segments */
  public abstract Optional<Tensor> steer(Scalar dist_tr, Scalar th_tr, Scalar th_total, Scalar radius);
}
