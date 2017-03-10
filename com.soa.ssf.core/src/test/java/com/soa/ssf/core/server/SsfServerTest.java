package com.soa.ssf.core.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.soa.ssf.core.annotation.SsfRpcService;
import com.soa.ssf.core.server.bean.SsfServerConfig;
import com.soa.ssf.core.server.nio.SsfNioServer;
import com.soa.ssf.core.util.ClassUtils;
import com.soa.ssf.core.util.IClassScannerFilter;

public class SsfServerTest {
	
	private static final Logger logger = LoggerFactory.getLogger(SsfServerTest.class); 
	
	Map<Class<?>,Object> serviceMap = new HashMap<Class<?>,Object>();
	
	public SocketChannel createClient() throws IOException{
		SocketChannel socketChannel = SocketChannel.open();
		InetSocketAddress address = new InetSocketAddress("127.0.0.1",9096);
//		socketChannel.configureBlocking(false);
		socketChannel.connect(address);
		return socketChannel;
	}
	
	@Test
	public void startService(){
		ClassUtils.packageClassScanner("",new IClassScannerFilter(){

			@Override
			public Boolean filter(Class<?> clazz) {
				SsfRpcService annotation = clazz.getAnnotation(SsfRpcService.class);
	    		if(annotation != null){
	    			String interfaceClass = annotation.interfaceClass();
	    			try {
						Class<?> annInterface = Thread.currentThread().getContextClassLoader().loadClass(interfaceClass);
						serviceMap.put(annInterface, clazz);
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
	
	@Test
	public void ssfServerTest(){
		try {
			SocketChannel socketChannel = createClient();
			if(socketChannel.finishConnect()){
				System.out.println("socketChannel have connect server.");
				ByteBuffer buffer = ByteBuffer.allocate(512);
				buffer.put("Helo world!".getBytes());
				buffer.flip();
				socketChannel.write(buffer);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
