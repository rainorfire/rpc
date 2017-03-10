package com.soa.ssf.core;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.soa.ssf.core.annotation.SsfRpcService;
import com.soa.ssf.core.server.bean.SsfServerConfig;
import com.soa.ssf.core.server.nio.SsfNioServer;
import com.soa.ssf.core.util.ClassUtils;
import com.soa.ssf.core.util.IClassScannerFilter;

/**
 * Hello world!
 *
 */

public class App implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(App.class);  

    public static void main( String[] args )
    {	
    	ClassUtils.packageClassScanner("",new IClassScannerFilter(){

			@Override
			public Boolean filter(Class<?> clazz) {
				SsfRpcService annotation = clazz.getAnnotation(SsfRpcService.class);
	    		if(annotation != null){
	    			String interfaceClass = annotation.interfaceClass();
	    			try {
						Class<?> annInterface = Thread.currentThread().getContextClassLoader().loadClass(interfaceClass);
						logger.info("annInterfaceName={},value={}",annInterface.getName(),annInterface.isInterface());
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
	    		}
				return true;
			}
    		
    	});
    	logger.info( "Hello World!" );
    	
    	SsfServerConfig ssfServerConfig = new SsfServerConfig();
    	ssfServerConfig.setHost("127.0.0.1");
    	ssfServerConfig.setPort(9096);
    	SsfNioServer ssfNioServer  = new SsfNioServer(ssfServerConfig);
    	ssfNioServer.startService();
    }
}
