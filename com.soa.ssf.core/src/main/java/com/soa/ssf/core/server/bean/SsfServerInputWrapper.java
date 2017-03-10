package com.soa.ssf.core.server.bean;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import com.soa.ssf.core.SsfRpcRequest;
import com.soa.ssf.core.util.SerializeUtil;

public class SsfServerInputWrapper {
	
	private SelectionKey selectionKey;
	
	private ByteBuffer readBuffer = ByteBuffer.allocate(1024);
	
	public SsfServerInputWrapper(SelectionKey selectionKey){
		this.selectionKey = selectionKey;
	}
	
	public SsfRpcRequest getSsfRpcRequest(){
		SsfRpcRequest request = null;
		SocketChannel channel = (SocketChannel) selectionKey.channel();
		try {
			Integer dataLength = 0;
			while(channel.read(readBuffer) > 0){
				if(readBuffer.hasRemaining() && readBuffer.array().length >=4){
					dataLength = readBuffer.getInt();
				}
				if(dataLength > 0 && readBuffer.array().length >= (dataLength + 4)){
					byte[] requestByteArray = new byte[dataLength];
					readBuffer.get(requestByteArray, 4, dataLength);
					request = (SsfRpcRequest) SerializeUtil.deserialize(requestByteArray, SsfRpcRequest.class);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return request;
	}
	
	public ByteBuffer getReadBuffer(){
		return readBuffer;
	}
	
}
