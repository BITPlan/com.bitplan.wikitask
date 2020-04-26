/**
 *
 * This file is part of the https://github.com/BITPlan/com.bitplan.wikitask open source project
 *
 * Copyright 2015-2020 BITPlan GmbH https://github.com/BITPlan
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
package org.sidif.wiki;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.jaxb.MarshallerProperties;

/**
 * A WikiTask result
 * 
 * @author wf
 *
 */
public class WikiTaskResult {
	public String html;
	public Throwable throwable;
	public Response response;
	public WikiTask wikiTask;

	/**
	 * create me from the given WikiTask
	 * 
	 * @param wikiTask
	 */
	public WikiTaskResult(WikiTask wikiTask) {
		this.wikiTask = wikiTask;
	}

	/**
	 * if there was an error it can be retrieved
	 * 
	 * @return any error that might have occured
	 */
	public Throwable getError() {
		return throwable;
	}

	/**
	 * get the stack trace
	 * 
	 * @return the stack trace
	 */
	public String getStackTrace() {
		StringWriter sw = new StringWriter();
		throwable.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}

	/**
	 * a JSON result
	 * 
	 * @author wf
	 *
	 */
	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class JsonResult {
		String targetUrl;
		String errorMsg;
		String text;
		String id;

		/**
		 * get the json equivalent of this result
		 * 
		 * @return
		 * @throws JAXBException
		 */
		public String asJson() throws JAXBException {
			JAXBContext jc = JAXBContext.newInstance(JsonResult.class);
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.setProperty(MarshallerProperties.MEDIA_TYPE,
					"application/json");
			marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
			StringWriter sw = new StringWriter();
			marshaller.marshal(this, sw);
			String result = sw.toString();
			return result;
		}
	}

	/**
	 * get my response based on the media type
	 * 
	 * @param mediaType - e.g. application/json
	 * 
	 * @return
	 * @throws Throwable
	 */
	public Response getResponse(String mediaType) throws Throwable {
		String result = null;
		if (MediaType.APPLICATION_JSON.equals(mediaType)) {
			JsonResult jsonResult = new JsonResult();
			jsonResult.id=this.wikiTask.getId();
			if (getError() == null) {
				if (response != null && response.getStatus()!=303) {		
					 jsonResult.errorMsg+="there was a response with status "+response.getStatus()+" for targetpage "+this.wikiTask.getTargetpage();
				} else {
					try {
						jsonResult.targetUrl = this.wikiTask.targetLink.getUrl();
					} catch (Throwable th) {
						jsonResult.errorMsg = "couldn't get url for "
								+ this.wikiTask.targetpage;
					}
				}
			} else {
				String error = getError().getClass().getName();
				String msg = getError().getMessage();
				String stacktrace = getStackTrace();
				jsonResult.errorMsg = "error " + error + ":" + msg + "\n";
				jsonResult.errorMsg += "stacktrace: " + stacktrace;
			}
			result = jsonResult.asJson();
		} else {
			if (getError() == null) {
				if (response != null) {
					return response;
				} else {
					result = html;
				}
			} else {
				String error = getError().getClass().getName();
				String msg = getError().getMessage();
				String stacktrace = getStackTrace();
				String html = wikiTask.error(error, msg, stacktrace);
				result = html;
			}
		}
		// FIXME - implement security
		String allowed = "*";
		return Response.ok(result).header("Access-Control-Allow-Origin", allowed)
				.build();
	}
}