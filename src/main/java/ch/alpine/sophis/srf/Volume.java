// code by jph
package ch.alpine.sophis.srf;

import java.util.Arrays;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.re.Det;

public enum Volume {
  ;
  public static Scalar of(SurfaceMesh surfaceMesh) {
    return TriangulateMesh.faces(surfaceMesh.faces()).stream() //
        .map(triangle -> Tensor.of(Arrays.stream(triangle).mapToObj(surfaceMesh.vrt::get))) //
        .map(Det::of) //
        .reduce(Scalar::add) //
        .orElseThrow().multiply(Rational.of(1, 6));
  }
}
