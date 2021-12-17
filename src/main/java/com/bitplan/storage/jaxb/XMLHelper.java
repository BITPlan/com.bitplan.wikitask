/**
 *
 * This file is part of the https://github.com/BITPlan/com.bitplan.wikitask open source project
 *
 * Copyright 2015-2022 BITPlan GmbH https://github.com/BITPlan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *  You may obtain a copy of the License at
 *
 *  http:www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bitplan.storage.jaxb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
//import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.util.JAXBResult;
//import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.eclipse.persistence.jaxb.JAXBContextFactory;

import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.api.json.JSONJAXBContext;
import com.sun.jersey.api.json.JSONMarshaller;

/**
 * 
 * @author wf XML Helper for JaxB
 */
public class XMLHelper {

	/**
	 * listener
	 */
	javax.xml.bind.Unmarshaller.Listener unmarshallerListener;
	@SuppressWarnings("rawtypes")
	List<XmlAdapter> adapterList = new ArrayList<XmlAdapter>();
	@SuppressWarnings("rawtypes")
	List<Class<? extends XmlAdapter>> adapterClassList = new ArrayList<Class<? extends XmlAdapter>>();

	// optional xlst transformation for XMLHelper
	String xsl;
	public boolean debug;
	public static boolean moxy = false;
	private Map<String, String> params;

	/**
	 * get the transformer for the given xsl source
	 * 
	 * @param params
	 * @return the Transformer
	 * @throws TransformerConfigurationException
	 * @throws TransformerFactoryConfigurationError
	 * @throws UnsupportedEncodingException
	 */
	public Transformer getTransformer(Map<String, String> params)
			throws TransformerConfigurationException,
			TransformerFactoryConfigurationError, UnsupportedEncodingException {
		// System.setProperty("javax.xml.transform.TransformerFactory","org.apache.xalan.processor.TransformerFactoryImpl");
		Transformer transformer = TransformerFactory.newInstance().newTransformer(
				this.getXslt());
		if (debug)
			transformer.setParameter("debug", debug);
		transformer.clearParameters();
		if (params != null) {
			for (String key : params.keySet()) {
				transformer.setParameter(key, params.get(key));
			}
		}
		// transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		return transformer;
	}

	/**
	 * convert the given xml using the xsl set
	 * 
	 * @param xml
	 * @param pEncoding
	 * @return the string resulting from the conversion
	 * @throws Exception
	 */
	public String transform(String xml, String pEncoding,
			Map<String, String> params) throws Exception {
		// check result e.g. with
		// http://www.w3schools.com/css/tryit.asp?filename=trycss_color
		Source xmlInput = new StreamSource(new ByteArrayInputStream(
				xml.getBytes(pEncoding)));
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		Result xmlOutput = new StreamResult(result);

		Transformer lTransformer = getTransformer(params);
		lTransformer.transform(xmlInput, xmlOutput);
		String resultString = result.toString(pEncoding);
		return resultString;
	}

	/**
	 * get the Xslt Source
	 * 
	 * @return the Source
	 * @throws UnsupportedEncodingException
	 */
	public Source getXslt() throws UnsupportedEncodingException {
		Source result = new StreamSource(new ByteArrayInputStream(
				xsl.getBytes("UTF-8")));
		return result;
	}

	/**
	 * @param xslt
	 *          the xslt to set
	 * @throws IOException
	 */
	public void setXslt(File xslt) throws IOException {
		setXslt(FileUtils.readFileToString(xslt, "UTF-8"));
	}

	/**
	 * set xsl
	 * 
	 * @param pxsl
	 */
	public void setXslt(String pxsl) {
		xsl = pxsl;
	}

	/**
	 * set the xslt params
	 * 
	 * @param params
	 */
	public void setXsltParams(Map<String, String> params) {
		this.params = params;
	}

	/**
	 * add an adapter
	 * 
	 * @param adaptertype
	 * @param adapter
	 */
	@SuppressWarnings("rawtypes")
	public <T extends XmlAdapter> void addAdapter(Class<T> adaptertype, T adapter) {
		adapterList.add(adapter);
		adapterClassList.add(adaptertype);
	}

	/**
	 * set a listener for unmarshalling
	 * 
	 * @param listener
	 */
	public void setUnmarshallerListener(
			javax.xml.bind.Unmarshaller.Listener listener) {
		this.unmarshallerListener = listener;
	}

	/**
	 * convert xml to Object
	 * 
	 * @param xml
	 * @param type
	 * @return the Object converted from XML
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object convertXMLToObject(String xml, java.lang.Class<?> type)
			throws Exception {
		JAXBContext context = getContext(type);
		Unmarshaller u = context.createUnmarshaller();

		// u.setProperty("com.sun.xml.bind.ObjectFactory",new ObjectFactoryEx());
		if (unmarshallerListener != null) {
			u.setListener(unmarshallerListener);
		}
		for (int i = 0; i < adapterList.size(); i++) {
			XmlAdapter adapter = adapterList.get(i);
			Class adapterClass = adapterClassList.get(i);
			u.setAdapter(adapterClass, adapter);
		}
		Object result = null;
		// unmarshal
		StreamSource source = new StreamSource(new StringReader(xml));
		if (this.xsl == null) {
			result = u.unmarshal(source, type).getValue();
		} else {
			JAXBResult jaxbResult = new JAXBResult(u);
			getTransformer(params).transform(source, jaxbResult);
			result = jaxbResult.getResult();
		}
		// workaround
		/*
		 * if (result instanceof JAXBElement) { JAXBElement element =
		 * (JAXBElement)result; result=element.getValue(); }
		 */
		return result;
	}

	/**
	 * get an Object of the given class type from the given xml
	 * 
	 * @param xml
	 * @param clazz
	 * @return the object converted from XML
	 * @throws Exception
	 */
	public Object fromXMLString(String xml, Class<?> clazz) throws Exception {
		Object o = convertXMLToObject(xml, clazz);
		return o;
	}

	/**
	 * read an Object of the given type from the given XML file
	 * 
	 * @param file
	 * @param clazz
	 * @return the object converted from XML
	 * @throws Exception
	 */
	public Object fromXMLFile(File file, Class<?> clazz) throws Exception {
		String xml = FileUtils.readFileToString(file,"utf-8");
		Object o = null;
		if (xml.trim().length() > 0) {
			o = fromXMLString(xml, clazz);
		}
		return o;
	}

	/**
	 * get the JaxB Context for the given class
	 * 
	 * @param clazz
	 * @return the JAXBContext
	 * @throws JAXBException
	 */
	@SuppressWarnings("rawtypes")
	public JAXBContext getContext(Class clazz) throws JAXBException {
		// String packageName=target.getClass().getPackage().getName();
		// FIXME cache JAXBContext ...
		JAXBContext context;
		if (moxy) {
			Class[] classes = { clazz };
			Map<String, Object> properties = new HashMap<String, Object>();
			context = JAXBContextFactory.createContext(classes, properties);
		} else {
			context = JAXBContext.newInstance(clazz);
		}
		return context;
	}

	/**
	 * convert the target to XML Format
	 * 
	 * @param target
	 * @return the XML string serialization of the given target 
	 * @throws Exception
	 */
	public String asXML(Object target) throws Exception {
		JAXBContext jaxbContext = getContext(target.getClass());
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		// output pretty printed
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		StringWriter sw = new StringWriter();
		jaxbMarshaller.marshal(target, sw);
		String result = sw.toString();
		return result;
	}

	/**
	 * convert the target to JSon Format
	 * 
	 * @param target
	 * @return the JSON string serialization of the given target
	 * @throws Exception
	 */
	public String asJson(Object target) throws Exception {
		StringWriter sw = new StringWriter();
		if (!moxy) {
			JSONConfiguration config = JSONConfiguration.natural().build();
			@SuppressWarnings("rawtypes")
			Class[] types = { target.getClass() };
			JSONJAXBContext context = new JSONJAXBContext(config, types);
			JSONMarshaller marshaller = context.createJSONMarshaller();
			marshaller.marshallToJSON(target, sw);
		} else {
			JAXBContext jaxbContext = getContext(target.getClass());
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty("eclipselink.media-type",
					MediaType.APPLICATION_JSON);
			marshaller.setProperty("eclipselink.json.include-root", false);
			marshaller.marshal(target, sw);
		}
		return sw.toString();
	}

}
