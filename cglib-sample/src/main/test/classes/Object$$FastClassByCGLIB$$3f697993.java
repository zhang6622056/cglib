//package classes;
//
//import java.lang.reflect.InvocationTargetException;
//import net.sf.cglib.reflect.FastClass;
//
//public class Object$$FastClassByCGLIB$$3f697993 extends FastClass{
//    public Object$$FastClassByCGLIB$$3f697993(Class paramClass)
//    {
//        super(paramClass);
//    }
//
//    /* Error */
//    public int getIndex(net.sf.cglib.core.Signature arg1)
//    {
//        // Byte code:
//        //   0: aload_1
//        //   1: invokevirtual 17	java/lang/Object:toString	()Ljava/lang/String;
//        //   4: dup
//        //   5: invokevirtual 21	java/lang/Object:hashCode	()I
//        //   8: lookupswitch	default:+66->74, 1826985398:+36->44, 1913648695:+46->54, 1984935277:+56->64
//        //   44: ldc 23
//        //   46: invokevirtual 27	java/lang/Object:equals	(Ljava/lang/Object;)Z
//        //   49: ifeq +26 -> 75
//        //   52: iconst_0
//        //   53: ireturn
//        //   54: ldc 29
//        //   56: invokevirtual 27	java/lang/Object:equals	(Ljava/lang/Object;)Z
//        //   59: ifeq +16 -> 75
//        //   62: iconst_1
//        //   63: ireturn
//        //   64: ldc 31
//        //   66: invokevirtual 27	java/lang/Object:equals	(Ljava/lang/Object;)Z
//        //   69: ifeq +6 -> 75
//        //   72: iconst_2
//        //   73: ireturn
//        //   74: pop
//        //   75: iconst_m1
//        //   76: ireturn
//    }
//
//    /* Error */
//    public int getIndex(String arg1, Class[] arg2)
//    {
//        // Byte code:
//        //   0: aload_1
//        //   1: aload_2
//        //   2: swap
//        //   3: dup
//        //   4: invokevirtual 21	java/lang/Object:hashCode	()I
//        //   7: lookupswitch	default:+151->158, -1776922004:+33->40, -1295482945:+67->74, 147696667:+117->124
//        //   40: ldc 37
//        //   42: invokevirtual 27	java/lang/Object:equals	(Ljava/lang/Object;)Z
//        //   45: ifeq +114 -> 159
//        //   48: dup
//        //   49: arraylength
//        //   50: tableswitch	default:+21->71, 0:+18->68
//        //   68: pop
//        //   69: iconst_1
//        //   70: ireturn
//        //   71: goto +91 -> 162
//        //   74: ldc 38
//        //   76: invokevirtual 27	java/lang/Object:equals	(Ljava/lang/Object;)Z
//        //   79: ifeq +80 -> 159
//        //   82: dup
//        //   83: arraylength
//        //   84: tableswitch	default:+37->121, 1:+20->104
//        //   104: dup
//        //   105: iconst_0
//        //   106: aaload
//        //   107: invokevirtual 43	java/lang/Class:getName	()Ljava/lang/String;
//        //   110: ldc 45
//        //   112: invokevirtual 27	java/lang/Object:equals	(Ljava/lang/Object;)Z
//        //   115: ifeq +47 -> 162
//        //   118: pop
//        //   119: iconst_0
//        //   120: ireturn
//        //   121: goto +41 -> 162
//        //   124: ldc 46
//        //   126: invokevirtual 27	java/lang/Object:equals	(Ljava/lang/Object;)Z
//        //   129: ifeq +30 -> 159
//        //   132: dup
//        //   133: arraylength
//        //   134: tableswitch	default:+21->155, 0:+18->152
//        //   152: pop
//        //   153: iconst_2
//        //   154: ireturn
//        //   155: goto +7 -> 162
//        //   158: pop
//        //   159: goto +3 -> 162
//        //   162: pop
//        //   163: iconst_m1
//        //   164: ireturn
//    }
//
//    /* Error */
//    public int getIndex(Class[] arg1)
//    {
//        // Byte code:
//        //   0: aload_1
//        //   1: dup
//        //   2: arraylength
//        //   3: tableswitch	default:+20->23, 0:+17->20
//        //   20: pop
//        //   21: iconst_0
//        //   22: ireturn
//        //   23: goto +3 -> 26
//        //   26: pop
//        //   27: iconst_m1
//        //   28: ireturn
//    }
//
//    /* Error */
//    public Object invoke(int arg1, Object arg2, Object[] arg3)
//            throws InvocationTargetException
//    {
//        // Byte code:
//        //   0: aload_2
//        //   1: iload_1
//        //   2: tableswitch	default:+57->59, 0:+26->28, 1:+41->43, 2:+45->47
//        //   28: aload_3
//        //   29: iconst_0
//        //   30: aaload
//        //   31: invokevirtual 27	java/lang/Object:equals	(Ljava/lang/Object;)Z
//        //   34: new 55	java/lang/Boolean
//        //   37: dup_x1
//        //   38: swap
//        //   39: invokespecial 58	java/lang/Boolean:<init>	(Z)V
//        //   42: areturn
//        //   43: invokevirtual 17	java/lang/Object:toString	()Ljava/lang/String;
//        //   46: areturn
//        //   47: invokevirtual 21	java/lang/Object:hashCode	()I
//        //   50: new 60	java/lang/Integer
//        //   53: dup_x1
//        //   54: swap
//        //   55: invokespecial 63	java/lang/Integer:<init>	(I)V
//        //   58: areturn
//        //   59: goto +12 -> 71
//        //   62: new 53	java/lang/reflect/InvocationTargetException
//        //   65: dup_x1
//        //   66: swap
//        //   67: invokespecial 68	java/lang/reflect/InvocationTargetException:<init>	(Ljava/lang/Throwable;)V
//        //   70: athrow
//        //   71: new 70	java/lang/IllegalArgumentException
//        //   74: dup
//        //   75: ldc 72
//        //   77: invokespecial 75	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
//        //   80: athrow
//        // Exception table:
//        //   from	to	target	type
//        //   2	62	62	java/lang/Throwable
//    }
//
//    public Object newInstance(int paramInt, Object[] paramArrayOfObject)
//            throws InvocationTargetException
//    {
//        try
//        {
//            switch (paramInt)
//            {
//                case 0:
//                    return new Object();
//            }
//        }
//        catch (Throwable localThrowable)
//        {
//            throw new InvocationTargetException(localThrowable);
//        }
//        throw new IllegalArgumentException("Cannot find matching method/constructor");
//    }
//
//    public int getMaxIndex()
//    {
//        return 2;
//    }
//}
