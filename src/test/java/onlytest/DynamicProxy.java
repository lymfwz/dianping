package onlytest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @AUTHOR: qiukui
 * @CREATE: 2023-09-09-17:52
 */
interface MyInterface {
    void doSomething();
}

class MyRealObject implements MyInterface {
    public void doSomething() {
        System.out.println("Real object is doing something.");
    }
}

class MyProxyHandler implements InvocationHandler {
    private Object realObject;

    public MyProxyHandler(Object realObject) {
        this.realObject = realObject;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("Proxy is doing something before invoking the real object.");
        Object result = method.invoke(realObject, args);
        System.out.println("Proxy is doing something after invoking the real object.");
        return result;
    }
}

public class DynamicProxy {
    public static void main(String[] args) {
        MyInterface realObject = new MyRealObject();
        MyProxyHandler handler = new MyProxyHandler(realObject);
        MyInterface proxyObject = (MyInterface) Proxy.newProxyInstance(
                MyInterface.class.getClassLoader(),
                new Class[] { MyInterface.class },
                handler
        );
        proxyObject.doSomething();
    }
}

