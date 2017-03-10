package com.soa.ssf.core.server.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import com.soa.ssf.core.SsfRpcResponse;
import com.soa.ssf.core.enums.SsfServerType;
import com.soa.ssf.core.server.AbstractSsfServer;
import com.soa.ssf.core.server.IChannelReader;
import com.soa.ssf.core.server.ServerProxyInvoker;
import com.soa.ssf.core.server.bean.SsfServerConfig;
import com.soa.ssf.core.server.bean.SsfServerInputWrapper;
import com.soa.ssf.core.server.bean.SsfServerWorker;
import com.soa.ssf.core.util.SerializeUtil;

public class SsfNioServer extends AbstractSsfServer {
	
	private static Selector nioSelector;
	
	private static ServerSocketChannel serverSocketChannel;
	
	private static ExecutorService threadPool = Executors.newFixedThreadPool(4);
	
	/**
	 * 任务队列
	 */
	public static final BlockingQueue<Object> TASKQUEUE = new LinkedBlockingQueue<Object>();
	
	public SsfNioServer(SsfServerConfig ssfServerConfig) {
		super(SsfServerType.NIO, ssfServerConfig);
	}

	@Override
	public void doStartService() {
		try {
			nioSelector = Selector.open();
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(false);
			serverSocketChannel.register(nioSelector, SelectionKey.OP_ACCEPT);
			serverSocketChannel.bind(this.getSsfServerConfig().getServerAddress());
			
			SsfServerWorker worker = new SsfServerWorker(nioSelector,new IChannelReader() {
				
				@Override
				public void readChannel(SelectionKey selectionKey) {
					SocketChannel channel = (SocketChannel) selectionKey.channel();
					SsfServerInputWrapper wrapper = new SsfServerInputWrapper(selectionKey);
					SsfRpcResponse rpcResponse = ServerProxyInvoker.invoke(wrapper.getSsfRpcRequest());
					
					byte[] rpcResponseSerialize = null;
					try {
						rpcResponseSerialize = SerializeUtil.serialize(rpcResponse);
					} catch (IOException e) {
						e.printStackTrace();
					}
					if(rpcResponseSerialize != null && rpcResponseSerialize.length > 0){
						ByteBuffer byteBuffer = ByteBuffer.allocate(4 + (rpcResponseSerialize.length));
						byteBuffer.putInt(rpcResponseSerialize.length);
						byteBuffer.put(rpcResponseSerialize);
						byteBuffer.flip();
						try {
							channel.write(byteBuffer);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			});
			threadPool.execute(worker);
		} catch (IOException e) {
			if(serverSocketChannel != null){
				try {
					serverSocketChannel.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if(nioSelector != null){
				try {
					nioSelector.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			throw new RuntimeException("启动SSF服务器异常");
		}

	}

	@Override
	public void doStopService() {

	}
	
}
