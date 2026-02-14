// code by jph
package ch.alpine.sophis.decim;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.lie.LieGroup;
import ch.alpine.sophus.lie.se.SeNGroup;
import ch.alpine.sophus.lie.se3.Se3Matrix;
import ch.alpine.sophus.lie.so.So3Exponential;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.ext.Serialization;

class Se3CurveDecimationTest {
  private static final LieGroup LIE_GROUP = new SeNGroup(3);

  @Test
  void testSimple() throws ClassNotFoundException, IOException {
    CurveDecimation curveDecimation = Serialization.copy(Se3CurveDecimation.of(RealScalar.of(0.3)));
    Tensor p = Se3Matrix.of(So3Exponential.vectorExp(Tensors.vector(0.1, -.2, -.3)), Tensors.vector(4, 3, 7));
    // Se3GroupElement pe = new Se3GroupElement(p);
    Tensor q = Se3Matrix.of(So3Exponential.vectorExp(Tensors.vector(0.2, .3, -.1)), Tensors.vector(1, 2, 5));
    // Se3GroupElement qe = new Se3GroupElement(q);
    ScalarTensorFunction scalarTensorFunction = LIE_GROUP.curve(p, q);
    Tensor m1 = scalarTensorFunction.apply(RealScalar.of(0.3));
    Tensor m2 = scalarTensorFunction.apply(RealScalar.of(0.8));
    Tensor curve = Tensors.of(p, m1, m2, q);
    assertEquals(Dimensions.of(curve), List.of(4, 4, 4));
    assumeTrue(false);
    Tensor tensor = curveDecimation.apply(curve);
    assertEquals(tensor.length(), 2);
    assertEquals(tensor, Tensors.of(p, q));
  }
}
