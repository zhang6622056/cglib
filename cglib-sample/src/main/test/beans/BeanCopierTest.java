package beans;

import net.sf.cglib.beans.BeanCopier;
import org.junit.Test;
import proxybean.People;
import proxybean.Product;
import proxybean.Sku;

import java.util.HashMap;
import java.util.Map;

public class BeanCopierTest {


    /****
     * 相同类别之间的copy
     */
    @Test
    public void testCopySameClass(){
        Product product = new Product();
        product.setName("lv");
        product.setWidth(1);
        product.setHeight(2);
        Product product1 = new Product();
        BeanCopier beanCopier = BeanCopier.create(Product.class,Product.class,false);
        beanCopier.copy(product,product1,null);
        System.out.println(product1.toString());
    }










    /****
     * 不同的类copy
     * 该测试的==为什么为false ？
     */
    @Test
    public void testCopyDiffClass(){
        Product product = new Product();
        product.setName("lv");
        product.setWidth(1);
        product.setHeight(2);
        Sku sku = new Sku();

        BeanCopier beanCopier = BeanCopier.create(Product.class,Sku.class,false);
        BeanCopier beanCopier1 = BeanCopier.create(Product.class,Sku.class,false);

        beanCopier.copy(product,sku,null);
        System.out.println(sku.toString());
    }















}
