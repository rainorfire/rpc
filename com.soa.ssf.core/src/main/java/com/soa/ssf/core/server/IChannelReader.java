package com.soa.ssf.core.server;

import java.nio.channels.SelectionKey;

import com.soa.ssf.core.SsfRpcResponse;

public interface IChannelReader {
	
	void readChannel(SelectionKey selectionKey);

}
