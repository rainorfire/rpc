package com.soa.ssf.core.service.impl;

import com.soa.ssf.core.annotation.SsfRpcService;
import com.soa.ssf.core.service.IPeopleService;

@SsfRpcService(interfaceClass="com.soa.ssf.core.service.IPeopleService")
public class PeopleServiceImpl implements IPeopleService{

	@Override
	public void sayHello() {
		System.out.println("Hello world!");
	}

}
