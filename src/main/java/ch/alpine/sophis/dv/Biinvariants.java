// code by jph
package ch.alpine.sophis.dv;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import ch.alpine.sophus.api.Manifold;
import ch.alpine.sophus.api.MetricManifold;

public enum Biinvariants {
  METRIC(manifold -> new MetricBiinvariant((MetricManifold) manifold)),
  LEVERAGES(LeveragesBiinvariant::new),
  GARDEN(GardenBiinvariant::new),
  HARBOR(HarborBiinvariant::new),
  CUPOLA(CupolaBiinvariant::new);

  public static final List<Biinvariants> FAST = List.of(METRIC, LEVERAGES, GARDEN);
  private final Function<Manifold, Biinvariant> supplier;

  Biinvariants(Function<Manifold, Biinvariant> supplier) {
    this.supplier = supplier;
  }

  /** @param manifold
   * @return */
  public Biinvariant ofSafe(Manifold manifold) {
    if (equals(METRIC) && !(manifold instanceof MetricManifold))
      return LEVERAGES.supplier.apply(manifold);
    return supplier.apply(manifold);
  }

  // ---
  public static Map<Biinvariants, Biinvariant> all(Manifold manifold) {
    Map<Biinvariants, Biinvariant> map = magic4(manifold);
    if (manifold instanceof MetricManifold metricManifold)
      map.put(METRIC, new MetricBiinvariant(metricManifold));
    return map;
  }

  public static Map<Biinvariants, Biinvariant> magic4(Manifold manifold) {
    Map<Biinvariants, Biinvariant> map = magic3(manifold);
    map.put(CUPOLA, CUPOLA.ofSafe(manifold));
    return map;
  }

  public static Map<Biinvariants, Biinvariant> magic3(Manifold manifold) {
    Map<Biinvariants, Biinvariant> map = new EnumMap<>(Biinvariants.class);
    map.put(LEVERAGES, LEVERAGES.ofSafe(manifold));
    map.put(GARDEN, GARDEN.ofSafe(manifold));
    map.put(HARBOR, HARBOR.ofSafe(manifold));
    return map;
  }

  /** @param manifold
   * @return instances of biinvariant that result in symmetric distance matrices */
  public static Map<Biinvariants, Biinvariant> kriging(Manifold manifold) {
    Map<Biinvariants, Biinvariant> map = new EnumMap<>(Biinvariants.class);
    if (manifold instanceof MetricManifold metricManifold)
      map.put(METRIC, new MetricBiinvariant(metricManifold));
    map.put(HARBOR, new HarborBiinvariant(manifold));
    return map;
  }
}
