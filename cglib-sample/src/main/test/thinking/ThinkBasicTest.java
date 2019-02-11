package thinking;


import net.sf.cglib.core.Constants;
import org.junit.Test;


public class ThinkBasicTest {


    @Test
    public void test(){
       // System.out.println(~2);



        System.out.println(1 & ~Constants.ACC_ABSTRACT
                & ~Constants.ACC_NATIVE
                & ~Constants.ACC_SYNCHRONIZED);
    }







}
