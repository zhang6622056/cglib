package callbacks;

import net.sf.cglib.proxy.Dispatcher;
import proxybean.People;


/****
 * 与lazy不同的是，每一次调用代理方法的时候，都会调用一次Dispatcher的loadObject获取对象
 * 而lazy则会缓存下来。
 *
 */
public class DispatcherCallBack implements Dispatcher {


    public Object loadObject() throws Exception {
        People people = new People();
        return people;
    }
}
