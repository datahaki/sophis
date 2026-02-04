// code by jph
package ch.alpine.sophis.ref.d1h;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.Serializable;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import ch.alpine.sophis.ref.d1h.Hermite1Subdivisions;
import ch.alpine.sophis.ref.d1h.HermiteLoConfig;
import ch.alpine.sophus.lie.rn.RGroup;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;

class Hermite1SubdivisionsTest {
  @Test
  void testSimple() {
    new HermiteSubdivisionQ(RnHermite1Subdivisions.instance()).check( //
        Hermite1Subdivisions.standard(RGroup.INSTANCE));
  }

  @Test
  void testParams() {
    Scalar lambda = RationalScalar.of(-1, 16);
    Scalar mu = RationalScalar.of(-1, 3);
    new HermiteSubdivisionQ(RnHermite1Subdivisions.of(lambda, mu)).check( //
        Hermite1Subdivisions.of(RGroup.INSTANCE, new HermiteLoConfig(lambda, mu)));
  }

  @Test
  void testSerializableCast() throws ClassNotFoundException, IOException {
    @SuppressWarnings("unchecked")
    Function<Integer, Tensor> function = (Function<Integer, Tensor> & Serializable) _ -> Tensors.empty();
    assertEquals(Serialization.copy(function).apply(3), Tensors.empty());
  }
}
