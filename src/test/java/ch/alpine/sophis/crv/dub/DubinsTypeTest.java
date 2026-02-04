package ch.alpine.sophis.crv.dub;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.sophis.crv.dub.DubinsType;

class DubinsTypeTest {
  @Test
  void test() {
    assertEquals(DubinsType.values().length, 6);
  }
}
