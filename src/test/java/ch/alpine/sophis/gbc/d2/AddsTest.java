// code by jph
package ch.alpine.sophis.gbc.d2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Rescale;
import ch.alpine.tensor.alg.Rotate;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.ev.RealEigensystem;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.red.Diagonal;
import ch.alpine.tensor.red.Total;

class AddsTest {
  @Test
  void testSimple() {
    assertEquals(Adds.forward(Tensors.vector(1, 2, 3)), Tensors.vector(3, 5, 4).multiply(Rational.HALF));
    assertEquals(Adds.reverse(Tensors.vector(1, 2, 3)), Tensors.vector(4, 3, 5).multiply(Rational.HALF));
  }

  @RepeatedTest(10)
  void testRandom(RepetitionInfo repetitionInfo) {
    Distribution distribution = DiscreteUniformDistribution.of(-10, 10);
    int n = repetitionInfo.getCurrentRepetition();
    Tensor tensor = RandomVariate.of(distribution, n);
    Tensor forward = Adds.forward(tensor);
    Tensor reverse = Adds.reverse(tensor);
    assertEquals(Rotate.PULL.of(forward, -1), reverse);
    Tolerance.CHOP.requireClose(Total.of(forward), Total.of(tensor));
    Tolerance.CHOP.requireClose(Total.of(reverse), Total.of(tensor));
  }

  @Test
  void testMatrix() {
    assertEquals(Adds.matrix_reverse(3).multiply(RealScalar.TWO), //
        Tensors.fromString("{{1, 1, 0}, {0, 1, 1}, {1, 0, 1}}"));
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 5, 10, 20 })
  void testMatrixS(int n) {
    Tensor f = Adds.matrix_forward(n);
    Tensor r = Adds.matrix_reverse(n);
    assertEquals(f, Transpose.of(r));
  }

  @Test
  void testNegFail() {
    assertThrows(Exception.class, () -> Adds.matrix_forward(-1));
    assertThrows(Exception.class, () -> Adds.matrix_reverse(-1));
    assertThrows(Exception.class, () -> Adds.matrix_forward(0));
    assertThrows(Exception.class, () -> Adds.matrix_reverse(0));
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 5 })
  void testEigenvalue(int n) {
    Tensor matrix = Adds.matrix_forward(n);
    RealEigensystem realEigensystem = RealEigensystem.of(matrix);
    // IO.println(Pretty.of(realEigensystem.diagonalMatrix().maps(Round._3)));
    Tensor diag = Diagonal.of(realEigensystem.diagonalMatrix());
    Rescale rescale = new Rescale(diag);
    Tolerance.CHOP.requireClose(rescale.clip().max(), RealScalar.ONE);
  }
}
