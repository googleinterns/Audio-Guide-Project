// Copyright 2020 Google LLC

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at

//     https://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.sps.data.FormType;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * When the fetch() function requests the /blobstore-upload-url URL, the content of the response is
 * the URL that allows a user to upload files(blobs) through a form(by setting the form's action to
 * that url).
 *
 * @param request provides as parameter the form's type. The form handler servlet is defined based
 *     on the formtype.
 */
@WebServlet("/blobstore-upload-url")
public class BlobstoreUploadUrlServlet extends HttpServlet {
  public static final String FORM_TYPE_PARAMETER = "formType";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String formType = request.getParameter(FORM_TYPE_PARAMETER);
    String targetServletName = FormType.valueOf(formType).getFormHandlerServletName();
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    String uploadUrl = blobstoreService.createUploadUrl(targetServletName);
    response.setContentType("text/html");
    response.getWriter().println(uploadUrl);
  }
}
