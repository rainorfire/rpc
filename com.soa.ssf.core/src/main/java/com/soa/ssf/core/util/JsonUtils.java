package com.soa.ssf.core.util;

import java.io.Serializable;

import com.google.gson.Gson;

public class JsonUtils implements Serializable{
	
	private static Gson gson = new Gson();
	
	/**
	 * 对象转Json
	 * @param obj
	 * @return
	 */
	public static String object2Json(Object obj){
		return gson.toJson(obj);
	}
	
	/**
	 * json转对象
	 * @param json
	 * @param clazz
	 * @return
	 */
	public static <T> T json2Object(String json,Class<T> clazz){
		T obj = gson.fromJson(json, clazz);
		return obj;
	}
}
