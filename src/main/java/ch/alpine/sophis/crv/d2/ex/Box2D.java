// code by jph
package ch.alpine.sophis.crv.d2.ex;

import ch.alpine.sophis.crv.d2.PolygonArea;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

public enum Box2D {
  ;
  public static final Tensor UNIT_SQUARE = polygon(xy(Clips.unit())).unmodifiable();
  public static final Tensor ABSOLUTE_ONE = polygon(xy(Clips.absoluteOne())).unmodifiable();

  /** @param clip
   * @return */
  public static CoordinateBoundingBox xy(Clip clip) {
    return CoordinateBoundingBox.of(clip, clip);
  }

  /** @param coordinateBoundingBox
   * @return polygon defined by the corners of the first two dimensions of given
   * coordinateBoundingBox starting with the min,min corner and visiting ccw
   * so that {@link PolygonArea} gives a non-negative number */
  public static Tensor polygon(CoordinateBoundingBox coordinateBoundingBox) {
    Clip clipX = coordinateBoundingBox.clip(0);
    Clip clipY = coordinateBoundingBox.clip(1);
    return Unprotect.byRef( //
        Tensors.of(clipX.min(), clipY.min()), //
        Tensors.of(clipX.max(), clipY.min()), //
        Tensors.of(clipX.max(), clipY.max()), //
        Tensors.of(clipX.min(), clipY.max()));
  }
}
