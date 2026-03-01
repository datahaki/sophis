// code by jph
package ch.alpine.sophis.reg;

import ch.alpine.sophis.api.DistanceFunction;
import ch.alpine.sophis.api.Region;

/** Interface for region with distance function that indicates proximity of a given point to the region.
 * The distance function returns 0 for members of the region, and positive values for non-members. */
public interface RegionWithDistance<T> extends Region<T>, DistanceFunction<T> {
  // ---
}
