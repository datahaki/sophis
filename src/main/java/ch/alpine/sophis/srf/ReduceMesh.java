// code by jph
package ch.alpine.sophis.srf;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;
import ch.alpine.tensor.opt.nd.CoordinateBounds;
import ch.alpine.tensor.opt.nd.NdCenters;
import ch.alpine.tensor.opt.nd.NdCollectRadius;
import ch.alpine.tensor.opt.nd.NdMap;
import ch.alpine.tensor.opt.nd.NdMatch;
import ch.alpine.tensor.opt.nd.NdTreeMap;

public record ReduceMesh(Scalar radius) {
  public SurfaceMesh process(SurfaceMesh surfaceMesh) {
    SurfaceMesh result = new SurfaceMesh();
    if (Tensors.nonEmpty(surfaceMesh.vrt)) {
      CoordinateBoundingBox cbb = CoordinateBounds.of(surfaceMesh.vrt);
      NdMap<Integer> ndMap = NdTreeMap.of(cbb);
      {
        int index = -1;
        for (Tensor p : surfaceMesh.vrt)
          ndMap.insert(p, ++index);
      }
      int n = surfaceMesh.vrt.length();
      int[] array = IntStream.range(0, n).toArray();
      int count = -1;
      for (int index = 0; index < n; ++index) {
        int fi = index;
        Tensor p = surfaceMesh.vrt.get(index);
        Collection<NdMatch<Integer>> collection = NdCollectRadius.of(ndMap, NdCenters.VECTOR_2_NORM.apply(p), radius);
        List<NdMatch<Integer>> list = collection.stream() //
            .filter(n1 -> n1.value() < fi) //
            .sorted((n1, n2) -> Integer.compare(n1.value(), n2.value())) //
            .toList();
        Optional<NdMatch<Integer>> optional = list.stream().findFirst();
        if (optional.isPresent()) {
          NdMatch<Integer> ndMatch = optional.orElseThrow();
          array[index] = array[ndMatch.value()];
          Scalar d = Vector2Norm.between(result.vrt.get(array[index]), p);
          Throw.unless(Scalars.lessEquals(d, radius));
        } else {
          array[index] = ++count;
          int vert = result.addVert(p);
          Throw.unless(vert == count);
        }
      }
      // IO.println(surfaceMesh.vrt.length() + " -> " + result.vrt.length());
      for (int[] face : surfaceMesh.faces()) {
        int[] values = IntStream.of(face).map(i -> array[i]).distinct().toArray();
        if (1 < values.length)
          result.addFace(values);
      }
    }
    return result;
  }
}
