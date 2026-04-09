// code by jph
package ch.alpine.sophis.itp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import ch.alpine.sophis.api.CurveOperator;
import ch.alpine.sophus.lie.so.So3Group;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.pdf.RandomSample;

class So3UniformResampleTest {
  @Test
  void testSimple() {
    CurveOperator curveOperator = UniformResample.of(So3Group.INSTANCE, So3Group.INSTANCE, RealScalar.ONE);
    Tensor vector = RandomSample.of(So3Group.INSTANCE.randomSampleInterface(), 20);
    Tensor string = curveOperator.string(vector);
    assertEquals(Dimensions.of(string).subList(1, 3), Arrays.asList(3, 3));
  }
}
