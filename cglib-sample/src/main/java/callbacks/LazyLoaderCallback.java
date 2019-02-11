package callbacks;

import net.sf.cglib.proxy.LazyLoader;
import proxybean.People;


/****
 *
 * 延迟加载初始化
 * 类似于spring prototype的singleton ，在第一次调用的时候进行初始化，并且将此实例存储起来，之后都将返回改实例
 * 可参考资料：
 * https://shensy.iteye.com/blog/1881277
 */
public class LazyLoaderCallback implements LazyLoader {
    public Object loadObject() throws Exception {
        People people = new People();
        people.setLazy(true);
        return people;
    }
}
