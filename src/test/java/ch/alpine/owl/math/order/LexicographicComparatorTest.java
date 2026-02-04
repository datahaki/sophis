// code by jph
package ch.alpine.owl.math.order;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

class LexicographicComparatorTest {
  @Test
  void testEmpty() {
    LexicographicComparator genericLexicographicOrder = new LexicographicComparator(Arrays.asList());
    List<Scalar> list = Arrays.asList();
    assertEquals(genericLexicographicOrder.compare(list, list), OrderComparison.INDIFFERENT);
  }

  @Test
  void testSimple() {
    List<OrderComparator<? extends Object>> comparators = Arrays.asList( //
        ScalarTotalOrder.INSTANCE, //
        ScalarTotalOrder.INSTANCE); //
    LexicographicComparator genericLexicographicOrder = new LexicographicComparator(comparators);
    List<Scalar> list = Arrays.asList(RealScalar.ONE, RealScalar.of(3));
    OrderComparison orderComparison = genericLexicographicOrder.compare(list, list);
    assertEquals(orderComparison, OrderComparison.INDIFFERENT);
  }

  @Test
  void testTensorAsIterable() {
    BinaryRelation<Tensor> relation1 = (x, y) -> x.length() <= y.length();
    List<OrderComparator<? extends Object>> comparators = Arrays.asList( //
        new Order<>(relation1), //
        ScalarTotalOrder.INSTANCE); //
    LexicographicComparator genericLexicographicOrder = new LexicographicComparator(comparators);
    Tensor tensorX = Tensors.fromString("{{1, 2, 3}, 2}");
    Tensor tensorY = Tensors.fromString("{{2, 3, 4, 5}, -2}");
    OrderComparison orderComparison = genericLexicographicOrder.compare(tensorX, tensorY);
    assertEquals(orderComparison, OrderComparison.STRICTLY_PRECEDES);
  }
}
