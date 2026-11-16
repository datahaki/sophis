// code by jph
package ch.alpine.sophis.srf;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ext.Serialization;

class IntDirectedEdgeTest {
  @Test
  void testSimple() {
    IntDirectedEdge directedEdge = Serialization.copy(new IntDirectedEdge(2, 3));
    assertEquals(directedEdge.reverse(), new IntDirectedEdge(3, 2));
  }
}
