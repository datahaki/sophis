// code by jph
package ch.alpine.sophis.srf;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.opt.nd.Dbscan;
import ch.alpine.tensor.opt.nd.NdCenters;
import ch.alpine.tensor.red.Mean;

/** uses Dbscan */
public record ReduceMesh(Scalar radius) {
  public SurfaceMesh of(SurfaceMesh surfaceMesh) {
    SurfaceMesh result = new SurfaceMesh();
    if (Tensors.nonEmpty(surfaceMesh.vrt)) {
      Integer[] labels = Dbscan.of(surfaceMesh.vrt, NdCenters.VECTOR_2_NORM, radius, 1);
      List<Tensor> list = new LinkedList<>();
      for (int index = 0; index < labels.length; ++index) {
        int label = labels[index];
        if (list.size() <= label)
          list.add(Tensors.empty());
        list.get(label).append(surfaceMesh.vrt.get(index));
      }
      result.vrt = Tensor.of(list.stream().map(Mean::of));
      for (int[] face : surfaceMesh.faces()) {
        int[] values = IntStream.of(face).map(i -> labels[i]).distinct().toArray();
        if (1 < values.length)
          result.addFace(values);
      }
    }
    return result;
  }
}
