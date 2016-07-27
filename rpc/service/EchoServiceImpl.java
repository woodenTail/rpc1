package rpc.service;

/**
 * @author lili 2016/7/2
 */
public class EchoServiceImpl implements EchoService {

    @Override
    public String echo(String ping) {
        System.out.println(ping);
        return "result " + ping;
    }
}
