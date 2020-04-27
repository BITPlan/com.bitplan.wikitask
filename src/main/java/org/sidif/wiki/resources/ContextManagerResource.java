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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bitplan.topic.ContextFactory;
import com.bitplan.topic.ContextSetting;
import com.bitplan.topic.TopicStatic.ContextManager;

@Path("/contexts")
public class ContextManagerResource extends FileResource {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getContextManager( @QueryParam("contextsetting")  String contextsetting) throws Exception {
    ContextFactory contextFactory=ContextFactory.getInstance();
    ContextSetting cs=ContextSetting.fromParams(contextsetting);
  	ContextManager cm=contextFactory.getContextManager(cs);
  	String json=cm.toJson();
  	Response result=Response.ok().entity(json).build();
  	return result;
  }
}
