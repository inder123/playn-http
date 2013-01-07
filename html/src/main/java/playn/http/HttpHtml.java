/**
 * Copyright 2010 The PlayN Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package playn.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import playn.core.PlayN;
import playn.core.util.Callback;

import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;

/**
 * XML Http Request implementation of {@link Http}.
 *
 * @author Inderjeet Singh
 */
public class HttpHtml extends Http {

  @Override
  protected void doSend(HttpRequest request, Callback<HttpResponse> callback) {
    HttpMethod method = request.getMethod();
    switch (method) {
    case GET:
    case POST:
    case PUT:
      invokeHttpMethod(request, callback);
      break;
    default: throw new UnsupportedOperationException(method.toString());
    }
  }

  public void invokeHttpMethod(final HttpRequest request, final Callback<HttpResponse> callback) {
    try {
      XMLHttpRequest xhr = XMLHttpRequest.create();
      xhr.open(request.getMethod().toString(), request.getUrl());
      for (Map.Entry<String, String> header : request.getHeaders()) {
        xhr.setRequestHeader(header.getKey(), header.getValue());
      }
      xhr.setOnReadyStateChange(new ReadyStateChangeHandler() {
        @Override
        public void onReadyStateChange(XMLHttpRequest xhr) {
          if (xhr.getReadyState() == XMLHttpRequest.DONE) {
            try {
              gotResponse(request, xhr, xhr.getResponseText(), callback);
            } catch (IOException e) {
              callback.onFailure(e);
            }
          }
        }
      });
      xhr.send(request.getBody());
    } catch (Exception e) {
      callback.onFailure(e);
    }
  }

  private void gotResponse(final HttpRequest req, XMLHttpRequest xhr, String responseBody,
      final Callback<HttpResponse> callback) throws IOException {
    int statusCode = -1;
    String statusLineMessage = null;
    Map<String, String> responseHeaders = new HashMap<String, String>();
    try {
      statusCode = xhr.getStatus();
      statusLineMessage = xhr.getStatusText();
      for (String headerName : getAllResponseHeaderNames(xhr)) {
        String value = xhr.getResponseHeader(headerName);
        responseHeaders.put(headerName, value);
      }
      HttpResponse response = new HttpResponse(
          statusCode, statusLineMessage, responseHeaders, responseBody);
      callback.onSuccess(response);
    } catch (Throwable t) {
      throw new HttpException(statusCode, statusLineMessage, responseBody, t, HttpErrorType.SERVER_ERROR);
    }
  }

  private List<String> getAllResponseHeaderNames(XMLHttpRequest xhr) {
    List<String> headers = new ArrayList<String>();
    String all = xhr.getAllResponseHeaders();
    for (String line : all.split("\n")) { // Can't use BufferedReader because of GWT
      try {
        String[] tokens = line.split(":");
        // ignore the actual header value, just add the header name
        if (tokens.length > 0) headers.add(tokens[0]);
      } catch (Exception e) {
        PlayN.log().warn("Error while processing a response header", e);
        // ignore this and continue reading other response headers
      }
    }
    return headers;
  }
}
