// code by jph
package ch.alpine.sophis.ref.d1h;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.lie.rn.RGroup;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.sca.Chop;

class Hermite2SubdivisionsTest {
  @Test
  void testSimple() {
    new HermiteSubdivisionQ(RnHermite2Subdivisions.standard()).check(Hermite2Subdivisions.standard(RGroup.INSTANCE));
    new HermiteSubdivisionQ(RnHermite2Subdivisions.manifold()).check(Hermite2Subdivisions.manifold(RGroup.INSTANCE));
  }

  static final List<HermiteSubdivision> LIST = Arrays.asList( //
      Hermite2Subdivisions.standard(RGroup.INSTANCE), //
      Hermite2Subdivisions.manifold(RGroup.INSTANCE));

  @Test
  void testStringReverseRn() {
    Tensor cp1 = RandomVariate.of(NormalDistribution.standard(), 7, 2, 3);
    Tensor cp2 = cp1.copy();
    cp2.set(Tensor::negate, Tensor.ALL, 1);
    for (HermiteSubdivision hermiteSubdivision : LIST) {
      TensorIteration tensorIteration1 = hermiteSubdivision.string(RealScalar.ONE, cp1);
      TensorIteration tensorIteration2 = hermiteSubdivision.string(RealScalar.ONE, Reverse.of(cp2));
      for (int count = 0; count < 3; ++count) {
        Tensor result1 = tensorIteration1.iterate();
        Tensor result2 = Reverse.of(tensorIteration2.iterate());
        result2.set(Tensor::negate, Tensor.ALL, 1);
        Chop._12.requireClose(result1, result2);
      }
    }
  }

  @Test
  void testNullA1Fail() {
    assertThrows(Exception.class, () -> Hermite2Subdivisions.standard(null));
  }

  @Test
  void testNullA2Fail() {
    assertThrows(Exception.class, () -> Hermite2Subdivisions.manifold(null));
  }
}
