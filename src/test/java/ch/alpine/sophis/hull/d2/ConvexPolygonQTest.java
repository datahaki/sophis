// code by jph
package ch.alpine.sophis.hull.d2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.lie.rot.CirclePoints;
import ch.alpine.tensor.mat.IdentityMatrix;

class ConvexPolygonQTest {
  @Test
  void test() {
    assertTrue(ConvexPolygonQ.INSTANCE.test(IdentityMatrix.of(2)));
  }

  @ParameterizedTest
  @ValueSource(ints = { 3, 4, 5, 6 })
  void testCircle(int n) {
    Tensor poly = CirclePoints.of(n);
    assertTrue(ConvexPolygonQ.INSTANCE.test(poly));
    assertTrue(ConvexPolygonQ.INSTANCE.test(Reverse.of(poly)));
  }
}
