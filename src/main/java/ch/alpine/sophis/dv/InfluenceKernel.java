// code by jph
package ch.alpine.sophis.dv;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.mat.gr.InfluenceMatrix;
import ch.alpine.tensor.mat.gr.Mahalanobis;
import ch.alpine.tensor.nrm.NormalizeTotal;

// TODO class name is not ideal since normalization happens -> weights?, or even coordinates
/* package */ record InfluenceKernel(InfluenceMatrix influenceMatrix) implements TensorUnaryOperator {
  /** function returns a vector vnull that satisfies
   * vnull . design == 0
   * 
   * @param design matrix
   * @param vector
   * @return mapping of vector to solution of barycentric equation */
  public static TensorUnaryOperator of(Tensor design) {
    return new InfluenceKernel(new Mahalanobis(design));
  }

  @Override
  public Tensor apply(Tensor vector) {
    return NormalizeTotal.FUNCTION.apply(influenceMatrix.kernel(vector));
  }
}
