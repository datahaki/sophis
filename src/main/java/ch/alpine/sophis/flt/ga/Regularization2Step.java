// code by mh, jph
package ch.alpine.sophis.flt.ga;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ch.alpine.sophis.ref.d1.CurveSubdivision;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.Last;
import ch.alpine.tensor.chq.ScalarQ;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.ext.PackageTestAccess;
import ch.alpine.tensor.itp.BinaryAverage;

/** geodesic average between 3 points for symmetric weight mask
 * {factor/2, 1-factor, factor/2}
 * implemented in 2-steps
 * 
 * a factor of 0 results in the identity operator
 * typically the factor is in the interval [0, 1]
 * 
 * @param geodesicSpace
 * @param factor for instance 2/3 */
public record Regularization2Step(BinaryAverage geodesicSpace, Scalar factor) implements CurveSubdivision, Serializable {
  /** @param prev
   * @param curr
   * @param next
   * @return [curr, [prev, next]_1/2]_factor */
  @PackageTestAccess
  Tensor average(Tensor prev, Tensor curr, Tensor next) {
    return geodesicSpace.split(curr, geodesicSpace.midpoint(prev, next), factor);
  }

  @Override
  public Tensor cyclic(Tensor tensor) {
    if (tensor.length() < 2) {
      ScalarQ.thenThrow(tensor);
      return tensor.copy();
    }
    List<Tensor> list = new ArrayList<>(tensor.length());
    Iterator<Tensor> iterator = tensor.iterator();
    Tensor prev = iterator.next();
    Tensor curr = iterator.next();
    list.add(average(Last.of(tensor), prev, curr));
    while (iterator.hasNext()) {
      Tensor next = iterator.next();
      list.add(average(prev, curr, next));
      prev = curr;
      curr = next;
    }
    list.add(average(prev, curr, tensor.get(0)));
    Integers.requireEquals(list.size(), tensor.length());
    return Unprotect.using(list);
  }

  @Override
  public Tensor string(Tensor tensor) {
    if (tensor.length() < 2) {
      ScalarQ.thenThrow(tensor);
      return tensor.copy();
    }
    List<Tensor> list = new ArrayList<>(tensor.length());
    Iterator<Tensor> iterator = tensor.iterator();
    Tensor prev = iterator.next();
    Tensor curr = iterator.next();
    list.add(prev.copy());
    while (iterator.hasNext()) {
      Tensor next = iterator.next();
      list.add(average(prev, curr, next));
      prev = curr;
      curr = next;
    }
    list.add(curr.copy());
    Integers.requireEquals(list.size(), tensor.length());
    return Unprotect.using(list);
  }
}
