package rpc.provider;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author lili 2016/7/2
 */
public class RpcExpoter {
    //static Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    static Executor executor = Executors.newFixedThreadPool(2);

    public static void expoter(String host, int port) throws IOException {
        ServerSocket server = new ServerSocket();
        server.bind(new InetSocketAddress(host, port));

        try {
            int i =0;
            while(true){
                System.out.println("i: "+i);
                ExpoterTask expoterTask = new ExpoterTask(server.accept());
                executor.execute(expoterTask);
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    private static class ExpoterTask implements Runnable {

        Socket client = null;

        public ExpoterTask(Socket client) {
            this.client = client;
        }
        public ExpoterTask() {
        }

        @Override
        public void run() {
            ObjectInputStream input = null;
            ObjectOutputStream output = null;
            try {
                System.out.println("server start run");
                input = new ObjectInputStream(client.getInputStream());
                String interfaceName = input.readUTF();
                Class<?> service = Class.forName(interfaceName);

                String methodName = input.readUTF();
                Class<?>[] paramTypes = (Class<?>[]) input.readObject();
                Object[] params = (Object[]) input.readObject();

                System.out.println("server methodName "+ methodName);
                Method method = service.getMethod(methodName, paramTypes);
                Object result = method.invoke(service.newInstance(), params);
                output = new ObjectOutputStream(client.getOutputStream());
                System.out.println("server output writeObject  " + result);
                output.writeObject(result);
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                try {
                    if (input != null){
                        input.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (output != null){
                        output.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (client != null){
                        client.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}





