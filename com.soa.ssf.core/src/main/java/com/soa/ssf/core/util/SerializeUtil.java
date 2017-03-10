package com.soa.ssf.core.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializeUtil {
	
	/**
	 * 通用反序列化
	 * @param <T>
	 * @param objByteArray
	 * @param clazz
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static <T> Object deserialize(byte[] objByteArray,T clazz) throws IOException{
		
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(objByteArray);
        ObjectInputStream ois = new ObjectInputStream(byteArrayInputStream);  
        T objT = null;
		try {
			objT = (T) ois.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}finally {
			if(ois != null){
				ois.close();
			}
			if(byteArrayInputStream != null){
				byteArrayInputStream.close();
			}
		}
        return objT;
	}
	
	/**
	 * 普通序列化
	 * @param obj
	 * @return
	 * @throws IOException
	 */
	public static byte[] serialize(Object obj) throws IOException{
		byte[] byteArray = null;
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//		byteArrayOutputStream.
        ObjectOutputStream ous = new ObjectOutputStream(byteArrayOutputStream);  
        ous.writeObject(obj);
        ous.flush();
        byteArray = byteArrayOutputStream.toByteArray();
        if(ous != null){
        	ous.close();
        }
        if(byteArrayOutputStream != null){
        	byteArrayOutputStream.close();
        }
        return byteArray;
	}

}
