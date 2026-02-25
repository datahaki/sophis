// code by jph
package ch.alpine.sophis.win;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.chq.MemberQ;
import ch.alpine.tensor.chq.ZeroDefectArrayQ;
import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.sca.Chop;

/** Careful: requires Scalar substraction therefore does not work
 * for vectors with scalars of instance {@link StringScalar}
 * 
 * symmetric vectors are of the form
 * <pre>
 * {a}
 * {a, a}
 * {a, b, a}
 * {a, b, b, a}
 * {a, b, c, b, a}
 * ...
 * </pre>
 * 
 * @param tensor
 * @return true if given tensor is a vector invariant under mirroring */
public class SymmetricVectorQ extends ZeroDefectArrayQ {
  public static final MemberQ INSTANCE = new SymmetricVectorQ();

  private SymmetricVectorQ() {
    super(1, Chop.NONE);
  }

  @Override // from ZeroDefectArrayQ
  public Tensor defect(Tensor tensor) {
    return Reverse.of(tensor).subtract(tensor);
  }
}
