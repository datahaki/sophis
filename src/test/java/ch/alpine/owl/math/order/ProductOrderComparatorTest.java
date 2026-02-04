// code by astoll
package ch.alpine.owl.math.order;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

class ProductOrderComparatorTest {
  @Test
  void testSimple() {
    List<OrderComparator<? extends Object>> comparators = Arrays.asList( //
        ScalarTotalOrder.INSTANCE, //
        ScalarTotalOrder.INSTANCE); //
    ProductOrderComparator productOrderComparator = new ProductOrderComparator(comparators);
    List<Scalar> list = Arrays.asList(RealScalar.ONE, RealScalar.of(3));
    OrderComparison orderComparison = productOrderComparator.compare(list, list);
    assertEquals(orderComparison, OrderComparison.INDIFFERENT);
  }

  @Test
  void testTensor() {
    BinaryRelation<Tensor> relation1 = (x, y) -> x.length() <= y.length();
    List<OrderComparator<? extends Object>> comparators = Arrays.asList( //
        new Order<>(relation1), //
        ScalarTotalOrder.INSTANCE); //
    ProductOrderComparator genericProductOrder = new ProductOrderComparator(comparators);
    Tensor tensorX = Tensors.fromString("{{1, 2, 3}, 10}");
    Tensor tensorY = Tensors.fromString("{{2, 3, 4, 5}, 7}");
    OrderComparison orderComparison = genericProductOrder.compare(tensorX, tensorY);
    assertEquals(orderComparison, OrderComparison.INCOMPARABLE);
  }
}
