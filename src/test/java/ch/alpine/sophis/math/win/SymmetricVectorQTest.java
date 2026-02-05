// code by jph
package ch.alpine.sophis.math.win;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensors;

class SymmetricVectorQTest {
  @Test
  void testSimple() {
    assertTrue(SymmetricVectorQ.INSTANCE.isMember(Tensors.empty()));
    assertTrue(SymmetricVectorQ.INSTANCE.isMember(Tensors.vector(1, 2, 2, 1)));
    assertTrue(SymmetricVectorQ.INSTANCE.isMember(Tensors.vector(1, 2, 1)));
    assertFalse(SymmetricVectorQ.INSTANCE.isMember(Tensors.vector(1, 2, 3)));
  }

  @Test
  void testRequire() {
    SymmetricVectorQ.INSTANCE.requireMember(Tensors.vector(1, 2, 1));
    SymmetricVectorQ.INSTANCE.requireMember(Tensors.vector(1, 1, 3, 3, 1, 1));
  }

  @Test
  void testNonVector() {
    assertTrue(SymmetricVectorQ.INSTANCE.isMember(Tensors.empty()));
    assertFalse(SymmetricVectorQ.INSTANCE.isMember(Tensors.fromString("{{1}}")));
    assertFalse(SymmetricVectorQ.INSTANCE.isMember(Tensors.fromString("{1, {2}, 1}")));
  }

  @Test
  void testThrow() {
    assertThrows(Exception.class, () -> SymmetricVectorQ.INSTANCE.requireMember(Tensors.vector(1, 1, 3, 1, 1, 1)));
  }
}
