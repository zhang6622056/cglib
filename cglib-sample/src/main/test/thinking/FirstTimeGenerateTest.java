package thinking;

import generateprocess.FirstTimeGenerate;
import net.sf.cglib.core.CollectionUtils;
import net.sf.cglib.core.Predicate;
import net.sf.cglib.core.VisibilityPredicate;
import org.junit.Test;
import proxybean.People;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;

public class FirstTimeGenerateTest{



    @Test
    public void test(){
        FirstTimeGenerate firstTimeGenerate = new FirstTimeGenerate();
        firstTimeGenerate.setSuperClass(People.class);
        firstTimeGenerate.create();
    }







}
