

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Base64;
import java.util.Properties;

/**
 * 服务器线程启动端
 * @author:  Asami
 * @ClassName:  ServerThread
 * @date:  2019年4月3日 上午11:01:59
 */
public class ServerThread implements Runnable{

    private Socket socket = null;//和本线程相关的Socket
    
    private String zipPassword = null;
    
    private ObjectOutputStream out = null;
    
	private ObjectInputStream in = null;
	
	private int servicesPort = 0; 
      
	private String saveUrl = "";
    public ServerThread(Socket socket,String zipPassword,String saveUrl) {
    	this.socket = socket;
    	this.zipPassword = zipPassword;
    	System.out.println("与客户端socket已连接");
    	this.saveUrl = saveUrl;
    	servicesPort = UDPUtils.returnEmptyPort();//寻找未占用端口号
    }

    @Override
    public void run() {
    	long systemTime = System.currentTimeMillis();
    	String savePath = saveUrl + systemTime + ".zip";
    	String outputPath = saveUrl + systemTime + "/";
        byte[] buf = new byte[UDPUtils.BUFFER_SIZE];  
        DatagramPacket dpk = null;  
        DatagramSocket dsk = null;  
        BufferedOutputStream bos = null;  
        try {  
			this.out = new ObjectOutputStream(this.socket.getOutputStream());
			String message = "servicePort:" + servicesPort; //将服务器端可用端口发送过去
			this.out.writeObject(message);
			this.out.flush();
			System.out.println("message=" + message);
			System.out.println("发送message请求");
            dpk = new DatagramPacket(buf, buf.length,new InetSocketAddress(socket.getInetAddress(), UDPUtils.ClientPORT));  // 绑定接收方地址  
            dsk = new DatagramSocket(servicesPort, InetAddress.getByName(UDPUtils.ServicesIp));  //创建一个DatagramSocket实例，并将该对象绑定到指定IP地址、指定端口
            dsk.receive(dpk);  
            bos = new BufferedOutputStream(new FileOutputStream(savePath));  
            long startTime = System.currentTimeMillis();  
            int readSize = 0;  
            int flushSize = 0;  
            while((readSize = dpk.getLength()) != 0){  
                if(UDPUtils.isEqualsByteArray(UDPUtils.exitData, buf, readSize)){   
                	byte[] bytes = "我很好".getBytes();
                    String encoded = Base64.getEncoder().encodeToString(bytes);
                    byte[] decoded = Base64.getDecoder().decode(encoded);
                    dpk.setData(decoded, 0, decoded.length);  
                    dsk.send(dpk);  
                    break;  
                }  
                bos.write(buf, 0, readSize);  
                if(++flushSize % 1000 == 0){   
                    flushSize = 0;  
                    bos.flush();  
                }  
                dpk.setData(UDPUtils.successData, 0, UDPUtils.successData.length);  
                dsk.send(dpk);  //成功接收到数据 发送
                dpk.setData(buf,0, buf.length);  
                dsk.receive(dpk);  
            }  
            bos.flush();  
            long endTime = System.currentTimeMillis(); 
            System.out.println("接收文件完成,耗时"+ (endTime - startTime));
            CompressUtil.unzip(savePath, outputPath, zipPassword);
            long endTime2 = System.currentTimeMillis();
            System.out.println("解压文件完成,耗时"+ (endTime2 - endTime));
            
            //第二部分 读取配置文件发送信息
            String properties = outputPath +"/Pic/"+ "information.properties";
            Properties props = new Properties(); 
            String ip = "";
            int port = 0;
    	    try {  
    	        // 读取文件  
    	        FileInputStream fis = new FileInputStream(properties);  
    	        props.load(fis);  
    	        long endTime3 = System.currentTimeMillis();
    	        System.out.println("读取配置文件完成,耗时"+ (endTime3 - endTime2));
    	        ip = props.getProperty("ip"); 
    	        port = Integer.valueOf(props.getProperty("port")); 
    	        fis.close();
    	    } catch (IOException e) {  
    	        e.printStackTrace();  
    	        System.exit(-1);  
    	    }
    	    
            dpk = new DatagramPacket(buf, buf.length,new InetSocketAddress(InetAddress.getByName(ip), port));  
            byte[] bytes = "我还要发出呐喊".getBytes();
            String encoded = Base64.getEncoder().encodeToString(bytes);
            byte[] decoded = Base64.getDecoder().decode(encoded);
            dpk.setData(decoded, 0, decoded.length);  
            dsk.send(dpk);  
            System.out.println("发送最终结果！");
            
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally{  
            try {  
                if(bos != null)  
                    bos.close();  
                if(dsk != null)  
                    dsk.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        } 
        
    }

}
