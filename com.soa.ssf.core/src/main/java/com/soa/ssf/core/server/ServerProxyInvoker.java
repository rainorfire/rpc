package com.soa.ssf.core.server;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.soa.ssf.core.SsfRpcRequest;
import com.soa.ssf.core.SsfRpcResponse;
import com.soa.ssf.core.annotation.SsfRpcService;
import com.soa.ssf.core.util.ClassUtils;
import com.soa.ssf.core.util.IClassScannerFilter;

public class ServerProxyInvoker {
	
	private static final Logger logger = LoggerFactory.getLogger(ServerProxyInvoker.class);  
	
	private static final ConcurrentHashMap<Class<?>,Object> SERVICEMAP = new ConcurrentHashMap<Class<?>,Object>();
	
	static{
		initInvoker();
	}
	
	private static void initInvoker(){
		ClassUtils.packageClassScanner("",new IClassScannerFilter(){

			@Override
			public Boolean filter(Class<?> clazz) {
				SsfRpcService annotation = clazz.getAnnotation(SsfRpcService.class);
	    		if(annotation != null){
	    			String interfaceClass = annotation.interfaceClass();
	    			try {
						Class<?> annInterface = Thread.currentThread().getContextClassLoader().loadClass(interfaceClass);
						SERVICEMAP.put(annInterface, annInterface.newInstance());
						logger.info("annInterfaceName={},value={}",annInterface.getName(),annInterface.isInterface());
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
	    		}
				return true;
			}
    		
    	});
	}
	
	public static SsfRpcResponse invoke(SsfRpcRequest request){
		SsfRpcResponse responce = new SsfRpcResponse();
		Class<?> interfaceClazz = request.getInterfaceClazz();
		String requestId = request.getRequestId();
		Object[] argValue = request.getArgValue();
		Method invokeMethod = request.getInvokeMethod();
		Object object = SERVICEMAP.get(interfaceClazz);
		responce.setRequestId(requestId);
		try {
			Object invokeResult = invokeMethod.invoke(object, argValue);
			responce.setResultData(invokeResult);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			responce.setResultData(e);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			responce.setResultData(e);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			responce.setResultData(e);
		}
		return responce;
	}

}
