// code by jph
package ch.alpine.sophis.dv;

import java.io.Serializable;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.SymmetricMatrixQ;
import ch.alpine.tensor.mat.pi.LagrangeMultiplier;
import ch.alpine.tensor.mat.pi.PseudoInverse;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityUnit;
import ch.alpine.tensor.red.EqualsReduce;
import ch.alpine.tensor.sca.var.ExponentialVariogram;
import ch.alpine.tensor.sca.var.PowerVariogram;
import ch.alpine.tensor.sca.var.SphericalVariogram;

/** implementation of kriging for homogeneous spaces
 * 
 * Reference:
 * "Biinvariant Distance Vectors"
 * by Jan Hakenberg, 2020
 * 
 * <p>Quote:
 * "Kriging is a technique named for South African mining engineer D.G. Krige. It is basically
 * a form of linear prediction, also known in different communities as Gauss-Markov estimation
 * or Gaussian process regression."
 * 
 * <p>Reference:
 * "Interpolation on Scattered Data in Multidimensions" in NR, 2007
 * 
 * @see PowerVariogram
 * @see ExponentialVariogram
 * @see SphericalVariogram */
public record Kriging(Sedarim sedarim, Scalar one, Tensor weights, Tensor inverse) implements Serializable {
  /** Gaussian process regression
   * 
   * @param sedarim
   * @param sequence
   * @param values vector or matrix
   * @param covariance symmetric matrix
   * @return */
  public static Kriging regression(Sedarim sedarim, Tensor sequence, Tensor values, Tensor covariance) {
    return of(sedarim, sequence, values, covariance);
  }

  /** @param sedarim
   * @param sequence of points
   * @param values vector or matrix associated to points in given sequence
   * @return */
  public static Kriging interpolation(Sedarim sedarim, Tensor sequence, Tensor values) {
    int n = values.length();
    // TODO SOPHUS UNIT Array.zeros(n, n) may not be sufficiently generic
    return regression(sedarim, sequence, values, Array.zeros(n, n));
  }

  /** uses unit vectors as target values
   * 
   * @param sedarim
   * @param sequence of points
   * @return */
  public static Kriging barycentric(Sedarim sedarim, Tensor sequence) {
    return interpolation(sedarim, sequence, IdentityMatrix.of(sequence.length()));
  }

  /** @param sedarim
   * @param sequence
   * @param values vector or matrix
   * @param covariance
   * @return */
  public static Kriging of(Sedarim sedarim, Tensor sequence, Tensor values, Tensor covariance) {
    // symmetric distance matrix eq (3.7.13)
    Tensor vardst = sedarim.sunder().slash(sequence);
    SymmetricMatrixQ.INSTANCE.requireMember(vardst);
    Tensor matrix = vardst.subtract(SymmetricMatrixQ.INSTANCE.requireMember(covariance));
    // TODO SOPHUS IMPL probably can be simplified
    Scalar one = Quantity.of(RealScalar.ONE, QuantityUnit.of(EqualsReduce.zero(matrix)));
    int n = matrix.length();
    Tensor rhs = Tensors.of(values.get(0).maps(Scalar::zero));
    LagrangeMultiplier lagrangeMultiplier = //
        new LagrangeMultiplier(matrix, ConstantArray.of(one, 1, n));
    Tensor inverse = PseudoInverse.of(lagrangeMultiplier.matrix());
    Tensor weights = inverse.dot(lagrangeMultiplier.b(values, rhs));
    return new Kriging(sedarim, one, weights, inverse);
  }

  private Tensor vs(Tensor point) {
    return sedarim.sunder(point).append(one);
  }

  /** @param point
   * @return estimate at given point */
  public Tensor estimate(Tensor point) {
    // TODO SOPHIS need to figure out whether weights should add up to 1
    return vs(point).dot(weights);
  }

  /** @param point
   * @return variance of estimate at given point */
  public Scalar variance(Tensor point) {
    Tensor vs = vs(point);
    return (Scalar) inverse.dot(vs).dot(vs);
  }
}
