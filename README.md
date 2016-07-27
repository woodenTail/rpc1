##RPC
 简单RPC实现
 rpc是远程过程调用,"过程"在JAVA中就是方法,"远程"是指不同机器上的进程或者不同的进程
 
简单rpc实现过程:

 - 先启动server短,进入一个死循环一直监测是否有客户端连接请求，server.accept没有请求前是阻塞的。
 - 启动客户端，客户端创建代理，向服务端发出请求
 - 服务端接收请求，与客户端建立了连接，并启动线程处理接口请求
 - 服务端处理完请求，使用socket输出流输出结果
 - 客户端返回结果(input.readObject)
 
##客户端代码分析
客户端执行导入对象,创建代理对象 
```java
RpcImpoter<EchoService>impoter= new RpcImpoter<EchoService>();
EchoService service = impoter.impoter(EchoServiceImpl.class, new InetSocketAddress("localhost", 8089));
```
```java
return (S) Proxy.newProxyInstance(serviceClass.getClassLoader(),
        interfaces,
        new InvocationHandler (){
           @Override
            public Object invoke(Object proxy, Method method, Object[] args) {
                    ......
     }
 });
```
代理对象调用远程服务
``` String echo = service.echo("hello world");```

代理对象调用远程服务后,委托给创建代理时的第三个参数InvocationHandler接口中声明的invoke方法,进行远程调用
```java
 new InvocationHandler () {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args)  {
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
            }
```
 
##服务端代码分析
服务端基于socket通信,监测到有客户端连接后,反射接口与方法进行服务端调用,并用socket输出流ObjectInputStream网络传输调用结果;

服务端socket create、bind、listen
 
```java
    static Executor executor = Executors.newFixedThreadPool(2);
     public static void expoter(String host, int port) throws IOException {
        ServerSocket server = new ServerSocket();
        server.bind(new InetSocketAddress(host, port));
        try {
        int i =0;
        while(true){
            System.out.println("i: "+i);
            //(server.accept建立一条TCP连接
            ExpoterTask expoterTask = new ExpoterTask(server.accept());
            executor.execute(expoterTask);
            i++;
        }
        } catch (Exception e) {
             e.printStackTrace();
     } 
```
服务端接收到请求
 
``` java
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
    }
```


