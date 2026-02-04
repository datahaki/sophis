// code by ob
package ch.alpine.sophis.flt.ga;

import java.io.Serializable;
import java.util.function.Supplier;

import ch.alpine.sophis.flt.CausalFilter;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.itp.BinaryAverage;

public enum GeodesicIIRnFilter {
  ;
  /** @param extrapolation
   * @param binaryAverage
   * @param radius
   * @param alpha
   * @return */
  public static TensorUnaryOperator of(TensorUnaryOperator extrapolation, BinaryAverage binaryAverage, int radius, Scalar alpha) {
    return CausalFilter.of( //
        (Supplier<TensorUnaryOperator> & Serializable) //
        () -> GeodesicIIRn.of(extrapolation, binaryAverage, radius, alpha));
  }
}
