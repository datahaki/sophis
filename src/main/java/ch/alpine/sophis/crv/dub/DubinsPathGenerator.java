// code by jph
package ch.alpine.sophis.crv.dub;

import java.util.stream.Stream;

@FunctionalInterface
public interface DubinsPathGenerator {
  /** @return stream */
  Stream<DubinsPath> stream();
}
