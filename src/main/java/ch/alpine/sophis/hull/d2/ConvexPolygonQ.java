// code by jph
package ch.alpine.sophis.hull.d2;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.chq.MemberQ;
import ch.alpine.tensor.sca.Sign;

public enum ConvexPolygonQ implements MemberQ {
  INSTANCE;

  @Override
  public boolean test(Tensor tensor) {
    tensor.forEach(vertex -> VectorQ.requireLength(vertex, 2));
    int n = tensor.length();
    if (n <= 2)
      return true;
    Tensor ccw = Tensors.reserve(n);
    Tensor p = tensor.get(n - 2);
    Tensor q = tensor.get(n - 1);
    for (Tensor r : tensor) {
      ccw.append(StaticHelper.ccw(p, q, r));
      p = q;
      q = r;
    }
    long count = ccw.stream() //
        .map(Scalar.class::cast) //
        .map(Sign.FUNCTION) //
        .filter(Scalars::nonZero) //
        .distinct().limit(2).count();
    return count == 1;
  }
}
