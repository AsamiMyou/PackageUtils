import java.io.IOException;
import java.net.ServerSocket;
import java.net.UnknownHostException;

public class UDPUtils {  
	private UDPUtils(){}  

	/** transfer file byte buffer **/  
	public static final int BUFFER_SIZE = 50 * 1024;  

	/** controller port  **/  
	public static final int ServicesPORT = 50001;  
	public static final int ClientPORT = 50000;  

	public static final String zipPassword = "Asami";

	public static final int ServicesSocketPORT = 45678;  
	public static final String ServicesIp = "192.168.1.139";//服务器地址


	/** mark transfer success **/  
	public static final byte[] successData = "success data mark".getBytes();  

	/** mark transfer exit **/  
	public static final byte[] exitData = "exit data mark".getBytes();   

	public static boolean isEqualsByteArray(byte[] compareBuf,byte[] buf){  
		if (buf == null || buf.length == 0)  
			return false;  

		boolean flag = true;  
		if(buf.length == compareBuf.length){  
			for (int i = 0; i < buf.length; i++) {  
				if(buf[i] != compareBuf[i]){  
					flag = false;  
					break;  
				}  
			}  
		}else  
			return false;  
		return flag;  
	}  


	public static boolean isEqualsByteArray(byte[] compareBuf,byte[] buf,int len){  
		if (buf == null || buf.length == 0 || buf.length < len || compareBuf.length < len)  
			return false;  

		boolean flag = true;  

		int innerMinLen = Math.min(compareBuf.length, len);  
		for (int i = 0; i < innerMinLen; i++) {    
			if(buf[i] != compareBuf[i]){  
				flag = false;  
				break;  
			}  
		}  
		return flag;  
	}

	public static int returnEmptyPort() {
		int port = 0;
		int begin = 8000;
		int end = 49999;
		while(port == 0) {
			for(int i=begin;i<end;i++) {
				try {
					ServerSocket sskt = new ServerSocket(i);
					port = i;
					break;
				}
				catch (IOException e) {
					continue;
				}
			}//END FOR
		}
		System.out.println("已经找到未占用端口");
		return port;
	}

}  