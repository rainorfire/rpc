package com.soa.ssf.core.server.bean;

import java.net.InetSocketAddress;

/**
 * SSF 服务器配置
 * @author cyj
 *
 */
public class SsfServerConfig {
	
	private String host;
	
	private Integer port;
	
	private volatile Integer serverStatus=SERVER_INIT;
	
	public static final Integer SERVER_INIT = 0;
	public static final Integer SERVER_RUNNING = 1;
	public static final Integer SERVER_STOP = 1;
	
	public InetSocketAddress getServerAddress(){
		return new InetSocketAddress(host, port);
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public Integer getServerStatus() {
		return serverStatus;
	}

	public void setServerStatus(Integer serverStatus) {
		this.serverStatus = serverStatus;
	}
	
}
