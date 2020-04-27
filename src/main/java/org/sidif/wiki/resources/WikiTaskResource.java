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
package org.sidif.wiki.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.sidif.wiki.WikiTask;
import org.sidif.wiki.WikiTaskManager;
import org.sidif.wiki.WikiTaskResult;

import com.sun.jersey.multipart.FormDataParam;

@Path("/task")
public class WikiTaskResource {
  @Context
  protected javax.ws.rs.core.HttpHeaders httpHeaders;
  
  /**
   * reply to an Options call (e.g. by a javascript ajax query)
   * http://stackoverflow.com/questions/18234366/restful-webservice-how-to-set-headers-in-java-to-accept-xmlhttprequest-allowed
   * @param allowed - the filter which sites to allow
   * @return the response
   */
  public static Response getCorsAllow(HttpHeaders headers,String allowed) {
    String customHeader="Header-Custom-WikitaskCORS";
    String token = headers.getRequestHeaders().getFirst(customHeader);
    Response response = Response.ok()
      .header(customHeader, token)
      .header("Access-Control-Allow-Origin", allowed)
      .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
      .header("Access-Control-Allow-Headers", customHeader+",Content-Type, Accept, X-Requested-With").build();
    return response;
  }

  @OPTIONS
  @Path("{cmd}")
  public Response getOptions(@Context UriInfo uri,@Context HttpHeaders headers,
      @PathParam("cmd") String cmd) {
    String allowed="*";
    Response response = getCorsAllow(headers,allowed);
    return response;
  }
  
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Path("{cmd}")
  public Response runCommandViaJson(@Context UriInfo uri,
  		@Context HttpHeaders headers,
  		 @PathParam("cmd") String cmd,
       @FormDataParam("server") String server,
       @FormDataParam("scriptpath") String scriptpath,
       @FormDataParam("contentlanguage") String contentlanguage,    
       @FormDataParam("page") String pageTitle,
       @FormDataParam("input") String input,
       @FormDataParam("dialog") String dialog,
       @FormDataParam("template") String template,
       @FormDataParam("targetpage") String targetpage,
       @FormDataParam("id") String id, 
       @FormDataParam("params") String params,    
       @FormDataParam("engine") @DefaultValue( "Rythm" ) String templateEngine,
       @FormDataParam("logo") String logo) throws Throwable {
    WikiTask wikiTask=new WikiTask(httpHeaders,server,scriptpath,contentlanguage,pageTitle,cmd,input,params,dialog,template,templateEngine,targetpage,logo);
    wikiTask.setId(id);
    WikiTaskResult taskResult=WikiTaskManager.getInstance().run(wikiTask);
    return taskResult.getResponse(MediaType.APPLICATION_JSON);

  }
  
  /**
   * run the given command on the given wiki page
   * 
   * @param cmd
   *          - the command to run
   * @param pageTitle
   *          - the page to work on
   * @throws Throwable 
   */
  @GET
  @Produces({ "text/xml", "text/plain", "text/html" })
	@Consumes("text/plain")
  @Path("{cmd}")
  public Response runCommand(@Context UriInfo uri,
  		@Context HttpHeaders headers,
      @PathParam("cmd") String cmd,
      @QueryParam("server") String server,
      @QueryParam("scriptpath") String scriptpath,
      @QueryParam("contentlanguage") String contentlanguage,    
      @QueryParam("page") String pageTitle,
      @QueryParam("input") String input,
      @QueryParam("dialog") String dialog,
      @QueryParam("template") String template,
      @QueryParam("targetpage") String targetpage,
      @QueryParam("params") String params,    
      @QueryParam("engine") @DefaultValue( "Rythm" ) String templateEngine,
      @QueryParam("logo") String logo
      )
      throws Throwable {
    WikiTask wikiTask=new WikiTask(httpHeaders,server,scriptpath,contentlanguage,pageTitle,cmd,input,params,dialog,template,templateEngine,targetpage,logo);
    WikiTaskResult taskResult=WikiTaskManager.getInstance().run(wikiTask);
    return taskResult.getResponse(MediaType.TEXT_HTML);
  }
}
