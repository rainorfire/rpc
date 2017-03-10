package com.soa.ssf.core.client.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import com.soa.ssf.core.util.SerializeUtil;

public class SsfClient{
	private static SocketChannel socketChannel;
	
	private static Selector selector;
	
	private String serverHost;
	
	private Integer port;
	
	public SsfClient(String serverHost,Integer port){
		this.serverHost = serverHost;
		this.port = port;
		try {
			socketChannel = createClient();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("连接SSF服务器异常");
		}
	}
	
	/**
	 * 发送消息
	 * @param obj
	 * @throws IOException
	 */
	public void send(Object obj) throws IOException{
		byte[] serializeByteArray = SerializeUtil.serialize(obj);
		int arrayLength = serializeByteArray.length;
		ByteBuffer allocate = ByteBuffer.allocate((4+arrayLength));
		allocate.putInt(arrayLength);
		allocate.put(serializeByteArray);
		allocate.flip();
		socketChannel.write(allocate);
	}
	
	/**
	 * 关闭连接
	 */
	public void closeConnect(){
		try {
			selector.close();
			socketChannel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	class ClientWorker implements Runnable{

		@Override
		public void run() {
			while(true){
				try {
					int select = SsfClient.selector.select();
					Set<SelectionKey> selectedKeys = SsfClient.selector.selectedKeys();
					Iterator<SelectionKey> iterator = selectedKeys.iterator();
					while(iterator.hasNext()){
						SelectionKey selectionKey = iterator.next();
						if(selectionKey.isAcceptable()){
							if((SelectionKey.OP_READ & selectionKey.interestOps()) > 0){
								socketChannel.register(selector, SelectionKey.OP_READ);
							}else{
								selectionKey.interestOps(SelectionKey.OP_READ & selectionKey.interestOps());
							}
						}else if(selectionKey.isReadable()){
							
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	private SocketChannel createClient() throws IOException{
		selector = Selector.open();
		SocketChannel tmpChannel = SocketChannel.open();
		InetSocketAddress address = new InetSocketAddress(serverHost,port);
		socketChannel.configureBlocking(false);
		socketChannel.register(selector, SelectionKey.OP_ACCEPT);
		tmpChannel.connect(address);
		return tmpChannel;
	}
	
	public String getServerHost() {
		return serverHost;
	}

	public void setServerHost(String serverHost) {
		this.serverHost = serverHost;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

}
