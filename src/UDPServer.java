import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

  
  
/**
 * 接收文件的服务器端
 * @author:  Asami
 * @ClassName:  UDPServer
 * @date:  2019年4月3日 上午11:01:23
 */
public class UDPServer {  
      
    public static void main(String[] args) {  
          
    	try {
			ServerSocket serviceSocket = new ServerSocket(55555);
			System.out.println("服务端端已经启动");
			Socket socket = null;
			while(true){//循环侦听新的客户端的连接
			    //调用accept（）方法侦听，等待客户端的连接以获取Socket实例
			    socket = serviceSocket.accept();
			    //创建新线程
			    Thread thread = new Thread(new ServerThread(socket,UDPUtils.zipPassword,"E://"));
			    thread.start();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}            
    }  
}  
