// code by jph
package ch.alpine.sophis.hull.d3;

import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class VertexListTest {
  @Test
  void test() {
    Integer a = 3;
    Integer b = 5;
    a = b = null;
    assertNull(a);
    assertNull(b);
  }
}
