// code by jph
package ch.alpine.sophis.flt.ga;

import ch.alpine.sophis.flt.CenterFilter;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.itp.BinaryAverage;

public enum GeodesicMeanFilter {
  ;
  /** @param binaryAverage
   * @param radius non-negative
   * @return */
  public static TensorUnaryOperator of(BinaryAverage binaryAverage, int radius) {
    return new CenterFilter(GeodesicMean.of(binaryAverage), radius);
  }
}
