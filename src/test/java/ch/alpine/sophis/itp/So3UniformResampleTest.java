// code by jph
package ch.alpine.sophis.itp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import ch.alpine.sophis.ref.d1.CurveSubdivision;
import ch.alpine.sophus.lie.so.So3Group;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.pdf.RandomSample;

class So3UniformResampleTest {
  @Disabled
  @Test
  void testSimple() {
    CurveSubdivision curveSubdivision = //
        UniformResample.of(So3Group.INSTANCE, So3Group.INSTANCE, RealScalar.ONE);
    Tensor vector = RandomSample.of(So3Group.INSTANCE, 20);
    Tensor string = curveSubdivision.string(vector);
    assertEquals(Dimensions.of(string).subList(1, 3), Arrays.asList(3, 3));
  }
}
