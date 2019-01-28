package callbacks;

import net.sf.cglib.proxy.FixedValue;


/*****
 * 该callback相当于重写了相应的函数实现。并不会调用原函数
 *
 *
 */
public class FixValueCallback implements FixedValue {


    /*****
     * 被代理方法的指定函数将会无条件的返回改object，动态的变更返回值
     * @return
     * @throws Exception
     */
    public Object loadObject() throws Exception {
        System.out.println("overwrite the code....");
        return true;
    }
}
