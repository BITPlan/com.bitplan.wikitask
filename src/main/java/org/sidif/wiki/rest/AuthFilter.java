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
package org.sidif.wiki.rest;


import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

/**
 * Filter requests to demand authentication for certain resources
 * 
 * @author msf
 * @author wf
 * @see <a href="http://simplapi.wordpress.com/2013/01/24/jersey-jax-rs-implements-a-http-basic-auth-decoder/">Jersey (JAX-RS) implements a HTTP Basic Auth decoder</a>     
 * @since 10.07.2013
 * @version 1.1
 * 
 * allows AccessRight setting
 */
public class AuthFilter implements ContainerRequestFilter {
  protected static Logger LOGGER = Logger
      .getLogger("org.sidif.wiki.rest");
  
  public static enum AccessRight {
    read, write, none
  };

  /**
   * the map of access rights
   */
  public static Map<String, AccessRight> accessRights = new LinkedHashMap<String, AccessRight>();

  /**
   * get the accessRight for the given address
   * 
   * @param addr
   * @return
   */
  public AccessRight getAccessRight(String addr) {
    AccessRight accessRight = accessRights.get(addr);
    if (accessRight == null) {
      accessRight = AccessRight.none;
    }
    return accessRight;
  }
  
  /**
   * grant the given addr the given accessRight
   * @param addr
   * @param accessRight
   */
  public static void grant(String addr,AccessRight accessRight) {
    accessRights.put(addr, accessRight);
  }

  /*
   * all requests are filtered through this method
   * 
   * @see
   * com.sun.jersey.spi.container.ContainerRequestFilter#filter(com.sun.jersey
   * .spi.container.ContainerRequest)
   */
  @Override
  public ContainerRequest filter(ContainerRequest httpRequest)
      throws WebApplicationException {
    String addr = httpRequest.getHeaderValue("remote_addr");
    AccessRight accessRight = getAccessRight(addr);
    // allow login requests
    String requestUri=httpRequest.getRequestUri().toASCIIString();
    if (requestUri.contains("/task/login?")){
      return httpRequest;
    }
    switch (accessRight) {
    case read:
    case write:
      return httpRequest;
    default:
      /**
      for (String key:httpRequest.getRequestHeaders().keySet()) {
        String value=httpRequest.getHeaderValue(key);
        LOGGER.log(Level.INFO,""+key+"="+value);  
        LOGGER.log(Level.INFO,httpRequest.getRequestUri().toASCIIString());
      }
      */
      String reason = "Requests from (" + addr + ")   are not allowed.<br>\n";
      reason+="If you are already authorized please login via the Wikitask template.<br>\n";
      reason+="otherwise  contact the site administrator if you'd like to get access.";
      throw getUnauthorizedException(reason);
    }
  }

  /**
   * get an unauthorized exception
   * 
   * @param reason
   * @return
   */
  private WebApplicationException getUnauthorizedException(String reason) {
    return new WebApplicationException(Response.status(Status.UNAUTHORIZED)
        .entity(reason).build());
  }
}