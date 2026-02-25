// code by jph
package ch.alpine.sophis.noise;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.red.ScalarSummaryStatistics;
import ch.alpine.tensor.sca.Clips;

class ColoredNoiseTest {
  @Test
  void testSimple() {
    Distribution distribution = ColoredNoise.of(RealScalar.of(1.2));
    ScalarSummaryStatistics doubleSummaryStatistics = //
        RandomVariate.stream(distribution).limit(1000).collect(ScalarSummaryStatistics.collector());
    Scalar average = doubleSummaryStatistics.getAverage();
    Scalar min = doubleSummaryStatistics.getMin();
    Scalar max = doubleSummaryStatistics.getMax();
    Clips.absoluteOne().requireInside(average);
    Clips.interval(-20, 0).requireInside(min);
    Clips.interval(0, +20).requireInside(max);
  }
}
