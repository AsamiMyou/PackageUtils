import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;  


/**
 * 发送文件测试端
 * @author:  Asami
 * @ClassName:  UDPClient
 * @date:  2019年4月3日 上午11:01:37
 */
public class UDPClient {  

	private static final String zipPassword = "Asami";

	private static final String serviceIp = "192.168.1.139";
	
	private static final int Dsport = 50000;//服务器 DatagramSocket 端口 初始设定好 客户端端口 接收方地址
	
	private static final String recIp = "192.168.1.139"; //第二阶段发送ip
	
	private static final String recPort = "50000"; //第二阶段发送端口号
	
	public static void main(String[] args){ 
		
		
		long startTime = System.currentTimeMillis();  
		long endTime = 0;
		long endTime2 = 0;
		byte[] buf = new byte[UDPUtils.BUFFER_SIZE];  
		byte[] receiveBuf = new byte[9];  

	
		RandomAccessFile accessFile = null;  
		DatagramPacket dpk = null;  
		DatagramSocket dsk = null;  
		int servicesPort = 0;
		int readSize = -1;  
		try {  
			Socket socket = new Socket(serviceIp, 55555);
			System.out.println("与服务器socket连接成功");
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			while(true) {
				String message = in.readObject().toString();
				System.out.println("message == " + message);
				String[] messages = message.split(":");
				servicesPort = Integer.valueOf(messages[1]);//获得服务器端可用端口
				break;
			}
				//写入第二阶段发送文件的端口号以及ip地址
				String outputName = ZipUtils.setIPAndZipPic(recIp, recPort, 
						"F:\\Pic", zipPassword, System.currentTimeMillis()+"");
				accessFile = new RandomAccessFile(outputName,"r");  
				dpk = new DatagramPacket(buf, buf.length,new InetSocketAddress(InetAddress.getByName(serviceIp), servicesPort)); // 绑定接收方地址  
				dsk = new DatagramSocket(Dsport, socket.getLocalAddress());  //创建一个Data实例 确保可以传输过来
				while((readSize = accessFile.read(buf,0,buf.length)) != -1){  
					dpk.setData(buf, 0, readSize);  
					dsk.send(dpk);  
					while(true){  
						dpk.setData(receiveBuf, 0, receiveBuf.length);  
						dsk.receive(dpk);  
						if(!UDPUtils.isEqualsByteArray(UDPUtils.successData,receiveBuf,dpk.getLength())){ 
							//未成功接收到数据 重新发送
							dpk.setData(buf, 0, readSize);  
							dsk.send(dpk);  
						}else  {
							break;  
						}
					}  
				}  
				
				while(true){  
					dpk.setData(UDPUtils.exitData,0,UDPUtils.exitData.length);  
					dsk.send(dpk);  
					dpk.setData(receiveBuf,0,receiveBuf.length);  
					dsk.receive(dpk);  
					if(new String(receiveBuf).equals("我很好")) {
						endTime = System.currentTimeMillis();
						System.out.println("传输文件完成,耗时"+ (endTime - startTime));
						break; 
					} else {
						dpk.setData(UDPUtils.exitData,0,UDPUtils.exitData.length);  
						dsk.send(dpk);
					}
				}   
				while(true){  
					byte[] receiveBuf2 = new byte[21]; 
					dpk.setData(receiveBuf2,0,receiveBuf2.length);  
					dsk.receive(dpk);  
					if(new String(receiveBuf2).equals("我还要发出呐喊")) {
						endTime2 = System.currentTimeMillis();
						System.out.println("收到服务器返回数据,耗时"+ (endTime2 - endTime));
						socket.close();
						break; 
					}
				}
		}catch (Exception e) {  
			e.printStackTrace();  
		} finally{  
			try {  
				if(accessFile != null)  
					accessFile.close();  
				if(dsk != null)  
					dsk.close();  
			} catch (IOException e) {  
				e.printStackTrace();  
			}  
			System.out.println("最终耗时"+ (endTime2 - startTime));
		}  
	}  
}  