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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.ws.rs.Consumes;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.io.IOUtils;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

/**
 * upload handler
 *
 */
public class FileResource {

  @OPTIONS
  @Path("upload")
  public Response getOptions(@Context UriInfo uri, @Context HttpHeaders headers) {
    String allowed = "*";
    Response response = WikiTaskResource.getCorsAllow(headers, allowed);
    return response;
  }

  @Path("uploadtest")
  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.TEXT_PLAIN)
  public Response uploadFileToWiki(
      @FormDataParam("uploadfile") final InputStream uploadedInputStream,
      @FormDataParam("uploadfile") final FormDataContentDisposition fileDetail)
      throws Exception {
    File tmpFile = File.createTempFile(fileDetail.getFileName(), ".tmp");
    FileOutputStream outputStream = new FileOutputStream(tmpFile);
    // http://stackoverflow.com/questions/4919690/how-to-read-one-stream-into-another
    // this is the non progress version
    IOUtils.copy(uploadedInputStream, outputStream);
    String msg = fileDetail.getFileName() + " uploaded";
    return Response.ok(msg).build();
  }

  @Path("upload")
  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.TEXT_PLAIN)
  public Response uploadFile(
      @FormDataParam("uploadfile") final InputStream uploadedInputStream,
      @FormDataParam("uploadfile") final FormDataContentDisposition fileDetail) {
    final String fileName = fileDetail.getFileName();
    if (fileName == null || "".equals(fileName)) {
      return Response.noContent().build();
    } else {
      StreamingOutput stream = new StreamingOutput() {
        @Override
        public void write(OutputStream os) throws IOException,
            WebApplicationException {
          File tmpFile = File.createTempFile(fileName, ".tmp");
          FileOutputStream outputStream = new FileOutputStream(tmpFile);
          Writer writer = new BufferedWriter(new OutputStreamWriter(os));
          writer.write("path=" + tmpFile.getAbsolutePath() + "\n");
          writer.write("length=" + tmpFile.length() + "\n");
          // http://stackoverflow.com/questions/4919690/how-to-read-one-stream-into-another
          // this is the non progress version
          // IOUtils.copy(uploadedInputStream, outputStream);
          byte[] buffer = new byte[1024 * 16]; // Adjust if you want
          long totalRead = 0;
          int bytesRead;
          int currentProgress = 0;
          long fileSize = fileDetail.getSize();
          while ((bytesRead = uploadedInputStream.read(buffer)) != -1) // test
                                                                       // for
                                                                       // EOF
          {
            outputStream.write(buffer, 0, bytesRead);
            totalRead += bytesRead;
            int progress = (int) (totalRead * 100 / fileSize);
            if (progress > currentProgress) {
              writer.write("progress=" + progress + "\n");
              writer.flush();
            }
            currentProgress = progress;
          }
          outputStream.close();
          writer.flush();
        }
      };
      return Response.ok(stream).build();
    }
  }
}
