// code by jph
package ch.alpine.sophis.api;

import ch.alpine.sophus.lie.so.So3Exponential;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.UnitSystem;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/GeoPosition.html">GeoPosition</a> */
public enum GeoPosition {
  ;
  /** @param string for example "38°20′44″N 00°46′8″W"
   * @return */
  public static Scalar fromString(String string) {
    throw new UnsupportedOperationException();
  }

  /** @param lat_lon for example {38.2543[deg], -153.55[deg]}
   * @return vector of length 3 as point on unit sphere S^2 */
  public static Tensor of(Tensor lat_lon) {
    VectorQ.requireLength(lat_lon, 2);
    lat_lon = lat_lon.maps(UnitSystem.SI());
    Scalar lat = lat_lon.Get(0);
    Tensor rot1 = So3Exponential.vectorExp(Tensors.of(lat.zero(), lat.negate(), lat.zero()));
    Scalar lon = lat_lon.Get(1);
    Tensor rot2 = So3Exponential.vectorExp(Tensors.of(lon.zero(), lon.zero(), lon));
    return rot2.dot(rot1.get(Tensor.ALL, 0));
  }

  /** consistent with mathematica
   * 
   * @param lat_lon
   * @return */
  public static Tensor xyz(Tensor lat_lon) {
    // TODO SOPHIS use ellipsoid model
    return of(lat_lon).multiply(Quantity.of(6_378_137, "m"));
  }
}
