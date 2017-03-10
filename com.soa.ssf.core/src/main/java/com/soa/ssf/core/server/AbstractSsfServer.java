package com.soa.ssf.core.server;

import com.soa.ssf.core.enums.SsfServerType;
import com.soa.ssf.core.server.bean.SsfServerConfig;

/**
 * 服务抽象类
 * @author lenovo
 *
 */
public abstract class AbstractSsfServer implements ISsfServer {
	
	private SsfServerConfig ssfServerConfig;
	
	private SsfServerType ssfServerType;
	
	private static final Object STARTSERVICELOCKER = new Object();
	
	private static final Object STOPSERVICELOCKER = new Object();
	
	/**
	 * 构造函数
	 * @param ssfServerConfig
	 * @param ssfServerType
	 */
	public AbstractSsfServer(SsfServerType ssfServerType,SsfServerConfig ssfServerConfig){
		this.ssfServerConfig = ssfServerConfig;
		this.ssfServerType = ssfServerType;
	}
	
	/**
	 * 实际启动服务
	 */
	public abstract void doStartService();
	
	/**
	 * 实际停止服务
	 */
	public abstract void doStopService();
	
//	protected abstract void initServiceBeforeStart();

	@Override
	public void startService() {
		System.out.println("正在启动SSF服务器。。。");
		if(ssfServerConfig.getServerStatus() != SsfServerConfig.SERVER_RUNNING){
			synchronized(STARTSERVICELOCKER){
				if(ssfServerConfig.getServerStatus() != SsfServerConfig.SERVER_RUNNING){
					try{
						doStartService();
						ssfServerConfig.setServerStatus(SsfServerConfig.SERVER_RUNNING);
					}catch (Exception e) {
						ssfServerConfig.setServerStatus(SsfServerConfig.SERVER_INIT);
					}
				}
			}
		}
		if(ssfServerConfig.getServerStatus() == SsfServerConfig.SERVER_RUNNING){
			System.out.println("成功启动SSF服务器！");
		}
	}

	@Override
	public void stopService() {
		if(ssfServerConfig.getServerStatus() == SsfServerConfig.SERVER_RUNNING){
			synchronized(STOPSERVICELOCKER){
				if(ssfServerConfig.getServerStatus() == SsfServerConfig.SERVER_RUNNING){
					try{
						doStopService();
					}finally {
						ssfServerConfig.setServerStatus(SsfServerConfig.SERVER_STOP);
					}
				}
			}
		}
	}

	public SsfServerConfig getSsfServerConfig() {
		return ssfServerConfig;
	}

	public void setSsfServerConfig(SsfServerConfig ssfServerConfig) {
		this.ssfServerConfig = ssfServerConfig;
	}

	public SsfServerType getSsfServerType() {
		return ssfServerType;
	}

	public void setSsfServerType(SsfServerType ssfServerType) {
		this.ssfServerType = ssfServerType;
	}

	
}
