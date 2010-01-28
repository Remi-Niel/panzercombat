package com.gampire.pc.json;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

public class JSONBeanFactory {

	static public Object createBean(Class<?> beanClass, JSONObject jsonObject)
			throws JSONException {

		Object bean = null;
		try {
			bean = beanClass.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
			PropertyDescriptor[] propertyDescriptors = beanInfo
					.getPropertyDescriptors();
			for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
				String propertyName = propertyDescriptor.getName();
				if (propertyName.equals("class")) {
					continue;
				}
				Iterator<String> jsonPropertyNamesIterator = jsonObject.keys();
				boolean foundJSONProperty = false;
				while (jsonPropertyNamesIterator.hasNext()) {
					String jsonPropertyName = jsonPropertyNamesIterator.next();
					if (propertyName.compareTo(jsonPropertyName) == 0) {
						Method writeMethod = propertyDescriptor
								.getWriteMethod();
						if (writeMethod != null) {
							writeMethod.invoke(bean, jsonObject
									.get(jsonPropertyName));
							foundJSONProperty = true;
							break;
						} else {
							throw new Error("write not allowed for property "
									+ jsonPropertyName);
						}
					}
				}
				if (!foundJSONProperty) {
					throw new JSONException(
							"the following object has no property "
									+ propertyName + ":\n"
									+ jsonObject.toString(3));
				}
			}

		} catch (IntrospectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return bean;
	}

	static public Object[] createBeans(Class<?> beanClass, JSONArray array)
			throws JSONException {
		if (array == null) {
			return null;
		}
		Object[] beans = (Object[]) Array
				.newInstance(beanClass, array.length());
		for (int i = 0; i < array.length(); i++) {
			JSONObject jsonObject = null;
			try {
				jsonObject = array.getJSONObject(i);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.out.println("jsonObject :"+jsonObject);
			beans[i] = JSONBeanFactory.createBean(beanClass, jsonObject);
		}
		return beans;
	}

	static public Object[] createBeans(Class<?> beanClass, Reader reader)
			throws JSONException {
		JSONTokener tokener = new JSONTokener(reader);
		JSONArray array = new JSONArray(tokener);
		//System.out.println(array.toString(3));
		return JSONBeanFactory.createBeans(beanClass, array);
	}

	static public Object[] createBeans(Class<?> beanClass, String filename) {
		System.out.println("Reading file '"+filename+"' ...");
		FileReader reader;
		try {
			reader = new FileReader(filename);
		} catch (FileNotFoundException e1) {
			throw new Error("Missing file \"" + filename + "\"");
		}
		Object[] beans;
		try {
			beans = JSONBeanFactory.createBeans(beanClass, reader);
		} catch (JSONException e) {
			throw new Error("Error reading file \"" + filename + "\": "
					+ e.getMessage());
		}
		return beans;
	}
}
