// code by jph
package ch.alpine.sophis.fit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.qty.Quantity;

class MinimumSpanningTreeTest {
  @Test
  void testSimple() {
    List<IntUndirectedEdge> list = MinimumSpanningTree.of(Tensors.fromString("{{0, 1}, {1, 0}}"));
    assertEquals(list.size(), 1);
  }

  @Test
  void testHilbert() {
    Tensor tensor = HilbertMatrix.of(10);
    List<IntUndirectedEdge> list = MinimumSpanningTree.of(tensor);
    assertEquals(list.size(), 9);
    Scalar scalar = list.stream().map(edge -> edge.Get(tensor)).reduce(Scalar::add).orElseThrow();
    assertEquals(scalar, RationalScalar.of(1632341, 2450448));
  }

  @Test
  void testHilbertQuantity() {
    List<IntUndirectedEdge> list = MinimumSpanningTree.of(HilbertMatrix.of(10).maps(s -> Quantity.of(s, "m")));
    assertEquals(list.size(), 9);
  }
}
