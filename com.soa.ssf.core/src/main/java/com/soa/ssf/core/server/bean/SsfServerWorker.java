package com.soa.ssf.core.server.bean;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import com.soa.ssf.core.server.IChannelReader;

public class SsfServerWorker implements Runnable {
	
	private Selector nioSelector;
	
	private IChannelReader iChannelReader;
	
	public SsfServerWorker(Selector nioSelector,IChannelReader iChannelReader){
		this.nioSelector = nioSelector;
		this.iChannelReader = iChannelReader;
	}

	@Override
	public void run() {
		while(true){
			try {
				int select = nioSelector.select();
				dispatchHandler(nioSelector);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 事件分发处理程序
	 * @param selector
	 * @throws IOException 
	 */
	private void dispatchHandler(final Selector selector) throws IOException{
		Set<SelectionKey> keys = selector.selectedKeys();
		Iterator<SelectionKey> keyIt = keys.iterator();
		SelectionKey selectionKey = null;
		
		while(keyIt.hasNext()){
			selectionKey = keyIt.next();
			keyIt.remove();
			if(selectionKey.isAcceptable()){
				System.out.println("NIo accept事件->");
				ServerSocketChannel channel = (ServerSocketChannel) selectionKey.channel();
				SocketChannel socket = channel.accept();
				socket.configureBlocking(false);
				if((selectionKey.interestOps() & SelectionKey.OP_READ) > 0){	//如果已经注册过读事件了
					selectionKey.interestOps(SelectionKey.OP_READ);
				}else{
					socket.register(selector, SelectionKey.OP_READ);
				}
			}else if(selectionKey.isReadable()){
				iChannelReader.readChannel(selectionKey);
				selectionKey.interestOps(selectionKey.interestOps() & SelectionKey.OP_ACCEPT);
				
			}else if(selectionKey.isWritable()){
				 
			}
		}
	}

}
