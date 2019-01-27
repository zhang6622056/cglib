package callbacks;

import net.sf.cglib.proxy.NoOp;


/****
 * 没有直接用途，只是用来标记NoOp的Callback类型。使用可以直接声明为NoOp的INSTANCE
 * noOp callback指的是不做任何事情的callback，
 * 直接调用原生实现
 *
 */
public class NoopCallback implements NoOp {

}
