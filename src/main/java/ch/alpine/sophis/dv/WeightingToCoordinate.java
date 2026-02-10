// code by jph
package ch.alpine.sophis.dv;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.mat.gr.InfluenceMatrix;
import ch.alpine.tensor.mat.gr.Mahalanobis;
import ch.alpine.tensor.nrm.NormalizeTotal;

/** Reference:
 * https://vixra.org/abs/2007.0043 */
/* package */ record WeightingToCoordinate(InfluenceMatrix influenceMatrix) implements TensorUnaryOperator {
  /** function returns a vector vnull that satisfies
   * vnull . levers == 0
   * 
   * @param levers matrix
   * @return mapping of vector to solution of barycentric equation */
  public static TensorUnaryOperator of(Tensor levers) {
    return new WeightingToCoordinate(new Mahalanobis(levers));
  }

  @Override
  public Tensor apply(Tensor vector) {
    return NormalizeTotal.FUNCTION.apply(influenceMatrix.kernel(vector));
  }
}
