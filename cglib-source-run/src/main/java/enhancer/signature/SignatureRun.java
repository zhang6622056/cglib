package enhancer.signature;

import net.sf.cglib.core.Signature;
import net.sf.cglib.core.TypeUtils;

public class SignatureRun {

    public static void main(String[] args) {
        Signature CSTRUCT_NULL = TypeUtils.parseConstructor("");
        System.out.println(CSTRUCT_NULL);

    }








}
