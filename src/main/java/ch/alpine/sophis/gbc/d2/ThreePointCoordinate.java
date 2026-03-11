// code by jph
package ch.alpine.sophis.gbc.d2;

import java.io.Serializable;
import java.util.Objects;

import ch.alpine.sophis.api.Genesis;
import ch.alpine.tensor.nrm.NormalizeTotal;

/** Three-point coordinates are also referred to as "Complete family of coordinates"
 * 
 * References:
 * "Generalized Barycentric Coordinates in Computer Graphics and Computational Mechanics"
 * by Kai Hormann, N. Sukumar, 2017
 * 
 * "Power Coordinates: A Geometric Construction of Barycentric Coordinates on Convex Polytopes"
 * by Max Budninskiy, Beibei Liu, Yiying Tong, Mathieu Desbrun, 2016
 * 
 * @see InsidePolygonCoordinate */
public enum ThreePointCoordinate {
  ;
  /** @param threePointScaling
   * @return */
  public static Genesis of(ThreePointScaling threePointScaling) {
    Objects.requireNonNull(threePointScaling);
    Genesis genesis = new ThreePointWeighting(threePointScaling);
    return (Genesis & Serializable) //
    levers -> NormalizeTotal.FUNCTION.apply(genesis.origin(levers));
  }
}
