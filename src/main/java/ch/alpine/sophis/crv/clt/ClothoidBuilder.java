// code by jph
package ch.alpine.sophis.crv.clt;

import ch.alpine.sophus.math.api.GeodesicSpace;
import ch.alpine.tensor.Tensor;

/** The constructed curve between the point p = {px, py, pa} and q = {qx, qy, qa} is of type {@link Clothoid} */
public interface ClothoidBuilder extends GeodesicSpace {
  @Override // from GeodesicSpace
  Clothoid curve(Tensor p, Tensor q);
}
