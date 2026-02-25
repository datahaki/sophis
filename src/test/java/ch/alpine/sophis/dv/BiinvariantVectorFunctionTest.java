// code by jph
package ch.alpine.sophis.dv;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.api.TensorMetric;
import ch.alpine.sophus.lie.rn.RGroup;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.nrm.FrobeniusNorm;

class BiinvariantVectorFunctionTest {
  @Test
  void testSimple() throws ClassNotFoundException, IOException {
    BiinvariantVectorFunction influenceBiinvariantVector = new BiinvariantVectorFunction( //
        RGroup.INSTANCE, Tensors.empty(), (TensorMetric & Serializable) (x, y) -> FrobeniusNorm.of(x.subtract(y)));
    Serialization.copy(influenceBiinvariantVector);
  }

  @Test
  void testNonPublic() {
    assertFalse(Modifier.isPublic(BiinvariantVectorFunction.class.getModifiers()));
  }
}
