package rpc.consumer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author lili 2016/7/2
 */
public class RpcImpoter<S> {

    public S impoter(final Class<?>serviceClass, final InetSocketAddress addr){

        Class<?>[] interfaces = new Class<?>[]{serviceClass.getInterfaces()[0]};
        return (S) Proxy.newProxyInstance(serviceClass.getClassLoader(),
                interfaces,
                new InvocationHandler () {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        {
                            System.out.println("client start");
                            Socket socket = null;
                            ObjectInputStream input = null;
                            ObjectOutputStream output = null;

                            try {
                                socket = new Socket();
                                socket.connect(addr);
                                output = new ObjectOutputStream(socket.getOutputStream());
                                System.out.println("client method " +method.getName());
                                output.writeUTF(serviceClass.getName());
                                output.writeUTF(method.getName());
                                output.writeObject(method.getParameterTypes());
                                output.writeObject(args);
                                input = new ObjectInputStream(socket.getInputStream());
                                Object result = input.readObject();
                                System.out.println("client input readObject  " + result);
                                return result;
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            } finally {
                                try {
                                    if (input != null) {
                                        input.close();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    if (output != null) {
                                        output.close();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    if (socket != null) {
                                        socket.close();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        return null;
                        }
                    }
                });
    }


}
