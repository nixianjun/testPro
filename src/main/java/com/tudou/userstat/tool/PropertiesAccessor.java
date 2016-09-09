package com.tudou.userstat.tool;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

public class PropertiesAccessor extends PropertyPlaceholderConfigurer {

	private static Logger logger = Logger.getLogger(PropertyAccessor.class);

	/** 存储applicationContext中配置的属性文件中的所有属性 */
	private static Properties props = null;

	/** 是否检查系统属性 */
	private static boolean lookupSystemProperties = true;

	/** 是否检查系统环境变量 */
	private static boolean lookupSystemEnvironment = true;

	@Override
	public void postProcessBeanFactory(
			ConfigurableListableBeanFactory beanFactory) throws BeansException {
		super.postProcessBeanFactory(beanFactory);
		try {
			props = mergeProperties();
		} catch (IOException e) {
			props = new Properties();
			throw new BeanInitializationException("Could not load properties",
					e);
		}
	}

	public static String getProperty(String key, boolean lookupSystemProps) {
		String value = props.getProperty(key);
		if (value == null && lookupSystemProps) {
			value = lookupSystemProperty(key);
		}
		return value;
	}

	public static String getProperty(String key) {
		return getProperty(key, lookupSystemProperties);
	}

	public static long getLongProperty(String key) {
		String s = getProperty(key, lookupSystemProperties);

		if (s != null) {
			return Long.valueOf(s);
		}

		return 0l;
	}

	public static String getProperty(String key, String defValue) {
		return getProperty(key, defValue, lookupSystemProperties);
	}

	public static String getProperty(String key, String defValue,
			boolean lookupSystemProps) {
		String value = getProperty(key, lookupSystemProps);
		if (value == null) {
			return defValue;
		} else {
			return value;
		}
	}

	private static String lookupSystemProperty(String key) {
		try {
			String value = System.getProperty(key);
			if (value == null && lookupSystemEnvironment) {
				value = System.getenv(key);
			}
			return value;
		} catch (Throwable ex) {
			if (logger.isDebugEnabled()) {
				logger.debug("Could not access system property '" + key + "': "
						+ ex);
			}
			return null;
		}
	}

	public static boolean isLookupSystemProperties() {
		return lookupSystemProperties;
	}

	/**
	 * 设置是否当找不到指定键时查找系统属性（包括环境变量），默认是查找
	 * 
	 * @param lookupSystemProperties
	 */
	public static void setLookupSystemProperties(boolean lookupSystemProperties) {
		PropertiesAccessor.lookupSystemProperties = lookupSystemProperties;
	}

}
