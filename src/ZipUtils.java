import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

public class ZipUtils {
	
	/** 
	 * 读取属性文件中相应键的值 
	 *  
	 * @param key 
	 *            主键 
	 * @return String 
	 */  
	public static String getKeyValue(String key,String path) {  
	    Properties props = new Properties();  
	    try {  
	        // 读取文件  
	        FileInputStream fis = new FileInputStream(path);  
	        props.load(fis);  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	        System.exit(-1);  
	    }  
	    return props.getProperty(key);  
	}  
	
	/** 
	 * 更新properties文件的键值对 如果该主键已经存在，更新该主键的值； 如果该主键不存在，则插件一对键值。 
	 *  
	 * @param keyname 
	 *            键名 
	 * @param keyvalue 
	 *            键值 
	 */  
	public static void updateProperties(String keyname, String keyvalue,String picPath) {  
	    // 如果文件夹不存在就创建  
	    File folder = new File(picPath);  
	    if (!folder.exists() && !folder.isDirectory()) {  
	        System.out.println("//不存在");  
	        folder.mkdirs();  
	    }  
	    // 如果文件不存在就创建  
	    File file = new File(picPath + "/information.properties");  
	    if (!file.exists()) {  
	        try {  
	            file.createNewFile();  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	    }  
	    Properties props = new Properties();  
	    try {  
	        // 读取文件  
	        FileInputStream fis = new FileInputStream(picPath + "/information.properties");  
	        props.load(fis);  
	        OutputStream fos = new FileOutputStream(picPath + "/information.properties");  
	        props.setProperty(keyname, keyvalue);  
	        props.store(fos, "Update '" + keyname + "' value");  
	    } catch (IOException e) {  
	        System.err.println("属性文件更新错误");  
	    }  
	}  
	
	
	public static String setIPAndZipPic(String ip,String port,String picPath,String zipPassword,String outfileName) {
		long startTime = System.currentTimeMillis(); 
		updateProperties("ip",ip,picPath);
		updateProperties("port",port,picPath);
		long endTime = System.currentTimeMillis(); 
		System.out.println("写入配置文件完成,耗时"+ (endTime - startTime));
		String outPutPath = "F:\\" + outfileName + ".zip";
		CompressUtil.zip(picPath, outPutPath, zipPassword);
		long endTime2 = System.currentTimeMillis();
		System.out.println("压缩文件完成,耗时"+ (endTime2 - endTime));
		return outPutPath;
	}
	
	
	
}
