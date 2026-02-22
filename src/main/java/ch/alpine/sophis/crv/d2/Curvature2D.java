// code by jph
package ch.alpine.sophis.crv.d2;

import java.util.ArrayList;
import java.util.List;

import ch.alpine.sophis.ref.d1.CurveSubdivision;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.Last;
import ch.alpine.tensor.ext.Integers;

/** @see CurvatureComb */
public class Curvature2D extends TripleReduceExtrapolation implements CurveSubdivision {
  public static final Curvature2D INSTANCE = new Curvature2D();

  private Curvature2D() {
  }

  @Override
  protected Scalar reduce(Tensor p, Tensor q, Tensor r) {
    return SignedCurvature2D.orElseZero(p, q, r);
  }

  @Override
  public Tensor cyclic(Tensor tensor) {
    int length = tensor.length();
    List<Tensor> list = new ArrayList<>(length);
    if (0 < length) {
      Tensor p = Last.of(tensor);
      Tensor q = tensor.get(0);
      for (int index = 1; index <= length; ++index) {
        Tensor r = tensor.get(index % length);
        list.add(SignedCurvature2D.orElseZero(p, q, r));
        p = q;
        q = r;
      }
    }
    Integers.requireEquals(list.size(), length);
    return Unprotect.using(list);
  }

  /** @param points of the form {{p1x, p1y}, {p2x, p2y}, ..., {pNx, pNy}}
   * @return vector with same length as points and entries containing
   * values of {@link SignedCurvature2D} */
  @Override
  public Tensor string(Tensor points) {
    return INSTANCE.apply(points);
  }
  // TODO cyclic
}
