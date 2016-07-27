package rpc;

import rpc.consumer.RpcImpoter;
import rpc.provider.RpcExpoter;
import rpc.service.EchoService;
import rpc.service.EchoServiceImpl;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @author lili 2016/7/2
 */
public class RpcTest {

    public static void main(String[] args) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    RpcExpoter.expoter("127.0.0.1",8089);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


        RpcImpoter<EchoService>impoter= new RpcImpoter<EchoService>();
        EchoService service = impoter.impoter(EchoServiceImpl.class, new InetSocketAddress("localhost", 8089));
        String echo = service.echo("hello world");
        System.out.println("echo" +echo);
        // System.exit(0);
    }
}
