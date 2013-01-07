/**
 * Copyright 2012 The PlayN Authors
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
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import playn.core.util.Callback;
import playn.http.Http;
import playn.http.HttpErrorType;
import playn.http.HttpException;
import playn.http.HttpMethod;
import playn.http.HttpRequest;
import playn.http.HttpResponse;
import playn.java.JavaPlatform;

/**
 * Pure Java implementation of {@link Http}.
 *
 * @author Inderjeet Singh
 */
public class HttpJava extends Http {

  private static final int BUF_SIZE = 4096;
  private final JavaPlatform platform;

  public HttpJava(JavaPlatform platform) {
    this.platform = platform;
  }

  @Override
  public void doSend(HttpRequest request, Callback<HttpResponse> callback) {
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
    final String urlStr = request.getUrl();
    new Thread("JavaNet.http(" + urlStr + ")") {
      public void run() {
        try {
          URL url = new URL(urlStr);
          HttpURLConnection conn = (HttpURLConnection) url.openConnection();
          HttpMethod method = request.getMethod();
          conn.setRequestMethod(method.toString());
          for (Map.Entry<String, String> header : request.getHeaders()) {
            conn.setRequestProperty(header.getKey(), header.getValue());
          }
          if (method == HttpMethod.POST || method == HttpMethod.PUT) {
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setAllowUserInteraction(false);
          }
          conn.connect();
          if (method == HttpMethod.POST || method == HttpMethod.PUT) {
            conn.getOutputStream().write(request.getBody().getBytes("UTF-8"));
            conn.getOutputStream().close();
          }
          String result = readFully(new InputStreamReader(conn.getInputStream(), "utf-8"));
          conn.disconnect();
          gotResponse(request, conn, result, callback);
        } catch (MalformedURLException e) {
          platform.notifyFailure(callback, e);
        } catch (UnknownHostException e) {
          HttpException he = new HttpException(500, "", "", e, HttpErrorType.NETWORK_FAILURE);
          platform.notifyFailure(callback, he);
        } catch (SocketTimeoutException e) {
          HttpException he = new HttpException(500, "", "", e, HttpErrorType.NETWORK_FAILURE);
          platform.notifyFailure(callback, he);
        } catch (IOException e) {
          platform.notifyFailure(callback, e);
        }
      }
    }.start();
  }

  private String readFully(Reader reader) throws IOException {
    StringBuffer result = new StringBuffer();
    char[] buf = new char[BUF_SIZE];
    int len = 0;
    while (-1 != (len = reader.read(buf))) {
      result.append(buf, 0, len);
    }
    return result.toString();
  }

  private void gotResponse(final HttpRequest req, HttpURLConnection conn, String responseBody,
      final Callback<HttpResponse> callback) throws IOException {
    int statusCode = -1;
    String statusLineMessage = null;
    Map<String, String> responseHeaders = new HashMap<String, String>();
    try {
      statusCode = conn.getResponseCode();
      statusLineMessage = conn.getResponseMessage();
      for (String headerName : conn.getHeaderFields().keySet()) {
        String value = conn.getHeaderField(headerName);
        responseHeaders.put(headerName, value);
      }
      HttpResponse response = new HttpResponse(
          statusCode, statusLineMessage, responseHeaders, responseBody);
      platform.notifySuccess(callback, response);
    } catch (Throwable t) {
      throw new HttpException(statusCode, statusLineMessage, responseBody, t, HttpErrorType.SERVER_ERROR);
    }
  }
}
