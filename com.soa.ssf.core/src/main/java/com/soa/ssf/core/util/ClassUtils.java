package com.soa.ssf.core.util;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassUtils implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(ClassUtils.class);
	
	/**
	 * 加载指定包下的类
	 * @param packagePath
	 * @return
	 */
	public static Set<Class<?>> packageClassScanner(String packagePath){
		Set<Class<?>> classSet = null;
		try {
			classSet = packageClassScanner(packagePath, Thread.currentThread().getContextClassLoader(),null);
		} catch (IOException e) {
			logger.error("", e);
		}
		return classSet;
	}
	
	public static Set<Class<?>> packageClassScanner(String packagePath,IClassScannerFilter scannerFilter){
		Set<Class<?>> classSet = null;
		try {
			classSet = packageClassScanner(packagePath, Thread.currentThread().getContextClassLoader(),(scannerFilter == null) ? new IClassScannerFilter(){

				@Override
				public Boolean filter(Class<?> clazz) {
					return true;
				}
				
			} : scannerFilter );
		} catch (IOException e) {
			logger.error("", e);
		}
		return classSet;
	}
	
	/**
	 * 加载包下的类
	 * @param packagePath
	 * @param classLoader
	 * @return
	 * @throws IOException
	 */
	private static Set<Class<?>> packageClassScanner(String packagePath,ClassLoader classLoader,IClassScannerFilter scannerFilter) throws IOException{
		Set<Class<?>> classSet = new HashSet<Class<?>>();
		String packagePathTmp = packagePath.replace(".", "/");
		Enumeration<URL> resources = classLoader.getResources(packagePathTmp);
		if(resources != null){
			while(resources.hasMoreElements()){
				URL resourceURL = resources.nextElement();
				String protocol = resourceURL.getProtocol();
				
				String filePath = resourceURL.getFile();
				if(filePath.startsWith("/")){
					filePath = filePath.substring(1);
				}
				
				File file = new File(filePath);
				if("file".equals(protocol)){
					Set<Class<?>> fileClassScanner = fileClassScanner(file,packagePath,scannerFilter);
					classSet.addAll(fileClassScanner);
				}else if("jar".equals(protocol)){
					Set<Class<?>> jarClassScanner = jarClassScanner(resourceURL, packagePath);
					classSet.addAll(jarClassScanner);
				}
			}
		}
		if(logger.isDebugEnabled()){
			logger.debug("共加载class {} 个",classSet.size());
		}
		return classSet;
	}
	
	/**
	 * 递归加载文件下类
	 * @param file
	 * @param packagePath
	 * @return
	 */
	private static Set<Class<?>> fileClassScanner(File file,String packagePath,IClassScannerFilter scannerFilter){
		if(!file.exists()){
			return null;
		}
		Set<Class<?>> classSet = new HashSet<Class<?>>();
		if(file.isDirectory()){
			File[] files = file.listFiles();
			for(File f : files){
				String tmpPackagePath = packagePath;
				if(f.isDirectory()){
					tmpPackagePath = (tmpPackagePath != null && !tmpPackagePath.isEmpty())? (tmpPackagePath + "." + f.getName()) : f.getName();
				}
				Set<Class<?>> set = fileClassScanner(f,tmpPackagePath,scannerFilter);
				if(set != null && !set.isEmpty())
					classSet.addAll(set);
			}
		}
		if(file.isFile()){
			Class<?> clazz = loadClassByFile(file, packagePath);
			if(clazz != null && scannerFilter.filter(clazz)){
				classSet.add(clazz);
			}
		}
		
		return classSet;
	}
	
	/**
	 * 加载jar包里面的类
	 * @param jarURL
	 * @param file
	 * @param packagePath
	 * @return
	 */
	private static Set<Class<?>> jarClassScanner(URL jarURL,String packagePath){
		Set<Class<?>> clazzSet = new HashSet<Class<?>>();
		try {
			JarURLConnection jarConnection = (JarURLConnection) jarURL.openConnection();
			JarFile jarFile = jarConnection.getJarFile();
			Enumeration<JarEntry> jarentries = jarFile.entries();
			if(jarentries != null){
				while(jarentries.hasMoreElements()){
					JarEntry jarEntry = jarentries.nextElement();
					Set<Class<?>> loadJarClasses = loadJarClass(jarEntry);
					if(loadJarClasses != null && !loadJarClasses.isEmpty())
						clazzSet.addAll(loadJarClasses);
				}
			}
		} catch (IOException e) {
			logger.error("", e);
		}
		return clazzSet;
	}
	
	private static Set<Class<?>> loadJarClass(JarEntry jarEntry) {
		if(jarEntry == null){
			return null;
		}
		Set<Class<?>> clazzSet = new HashSet<Class<?>>();
		
		String entryPath = jarEntry.getName().replace("/", ".");
		if(jarEntry.getName().endsWith(".class")){
			Class<?> clazz = null;
			try {
				clazz = Thread.currentThread().getContextClassLoader().loadClass(entryPath.substring(0, entryPath.length()-6));
			} catch (Exception e) {
				logger.error("", e);
			}
			if(clazz != null){
				clazzSet.add(clazz);
			}
		}
		return clazzSet;
	}
	
	/**
	 * 加载类
	 * @param f
	 * @param packagePath
	 * @return
	 */
	private static Class<?> loadClassByFile(File f,String packagePath){
		Class<?> clazz = null;
		if(f.isFile() && "class".equals(obtainFileSuffix(f))){
			try {
				clazz = Thread.currentThread().getContextClassLoader().loadClass(packagePath+ "." + obtainFileNameWithoutSuffix(f));
			} catch (ClassNotFoundException e) {
				logger.error("", e);
			}
		}
		return clazz;
	}
	
	/**
	 * 获取文件后缀
	 * @param file
	 * @return
	 */
	private static String obtainFileSuffix(File file){
		String suffix = "";
		if(file != null && file.exists()){
			String fileName = file.getName();  
			 suffix = fileName.substring(fileName.lastIndexOf(".") + 1);  
		}
        return suffix;
	}
	
	/**
	 * 获取class文件名，无后缀
	 * @param file
	 * @return
	 */
	private static String obtainFileNameWithoutSuffix(File file){
		String returnName = "";
		if(file != null){
			String name = file.getName();
			if(name != null){
				returnName = name.substring(0,name.length() - 6);
			}
		}
		return returnName;
	}
	
	public static void main(String[] args) throws IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
//		Set<Class<?>> set = ClassUtils.packageClassScanner("org.springframework.jdbc",classLoader);
//		for(Class<?> clazz : set){
//			logger.info("className={}",clazz.getName());
//		}
		
//		Set<Class<?>> set = ClassUtils.fileClassScanner("com\\soa\\ssf\\core\\util", classLoader);
//		logger.info(JsonUtils.object2Json(set));
	}
}
