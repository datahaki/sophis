// code by jph
package ch.alpine.sophis.fit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ext.Serialization;

class IntUndirectedEdgeTest {
  @Test
  void testSimple() {
    IntUndirectedEdge undirectedEdge = new IntUndirectedEdge(2, 3);
    assertEquals(undirectedEdge.i(), 2);
    assertEquals(undirectedEdge.j(), 3);
    IntUndirectedEdge rev = new IntUndirectedEdge(3, 2);
    assertEquals(undirectedEdge, rev);
    // IO.println(rev);
    assertEquals(rev.toString(), "IntUndirectedEdge[i=2, j=3]");
  }

  @Test
  void testCorrect() throws ClassNotFoundException, IOException {
    IntUndirectedEdge undirectedEdge = Serialization.copy(new IntUndirectedEdge(3, 2));
    assertEquals(undirectedEdge.i(), 2);
    assertEquals(undirectedEdge.j(), 3);
  }
}
