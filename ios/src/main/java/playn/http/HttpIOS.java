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

import java.util.HashMap;
import java.util.Map;

import playn.core.util.Callback;
import playn.http.Http;
import playn.http.HttpErrorType;
import playn.http.HttpException;
import playn.http.HttpMethod;
import playn.http.HttpRequest;
import playn.http.HttpResponse;
import playn.ios.IOSPlatform;
import cli.System.AsyncCallback;
import cli.System.IAsyncResult;
import cli.System.IO.StreamReader;
import cli.System.IO.StreamWriter;
import cli.System.Net.HttpWebResponse;
import cli.System.Net.WebHeaderCollection;
import cli.System.Net.WebRequest;
import cli.System.Net.WebResponse;
import cli.System.Net.NetworkInformation.NetworkInterface;

/**
 * iOS implementation for {@link Http}.
 *
 * @author Inderjeet Singh
 */
public class HttpIOS extends Http {

  private final IOSPlatform platform;

  public HttpIOS(IOSPlatform platform) {
    this.platform = platform;
  }

  @Override
  public void doSend(HttpRequest request, Callback<HttpResponse> callback) {
    HttpMethod method = request.getMethod();
    switch (method) {
    case GET:
      get(request, callback);
      break;
    case POST:
    case PUT:
      postOrPut(request, callback);
      break;
    default: throw new UnsupportedOperationException(method.toString());
    }
  }

  private void get(HttpRequest request, Callback<HttpResponse> callback) {
    try {
      final WebRequest req = buildRequest(request);
      req.BeginGetResponse(gotResponse(req, callback), null);
    } catch (Throwable t) {
      callback.onFailure(t);
    }
  }

  public void postOrPut(final HttpRequest request, final Callback<HttpResponse> callback) {
    try {
      final WebRequest req = buildRequest(request);
      req.set_Method(request.getMethod().toString());
      req.BeginGetRequestStream(new AsyncCallback(new AsyncCallback.Method() {
        @Override
        public void Invoke(IAsyncResult result) {
          try {
            StreamWriter out = new StreamWriter(req.GetRequestStream());
            out.Write(request.getBody());
            out.Close();
            req.BeginGetResponse(gotResponse(req, callback), null);
          } catch (Throwable t) {
            platform.notifyFailure(callback, t);
          }
        }
      }), null);
    } catch (Throwable t) {
      callback.onFailure(t);
    }
  }

  private WebRequest buildRequest(final HttpRequest request) {
    final WebRequest req = WebRequest.Create(request.getUrl());
    WebHeaderCollection headers = req.get_Headers();
    req.set_ContentType("application/json");
    for (Map.Entry<String, String> header : request.getHeaders()) {
      String name = header.getKey();
      if (name.equalsIgnoreCase("Content-Type")) continue;
      String value = header.getValue();
      if (headers.Get(name) != null) {
        headers.Set(name,  value);
      } else {
        headers.Add(name, value);
      }
    }
    return req;
  }

  private AsyncCallback gotResponse(final WebRequest req, final Callback<HttpResponse> callback) {
    return new AsyncCallback(new AsyncCallback.Method() {
      @Override
      public void Invoke(IAsyncResult result) {
        if (!NetworkInterface.GetIsNetworkAvailable()) {
          HttpException error = new HttpException(500, "", "", null, HttpErrorType.NETWORK_FAILURE);
          platform.notifyFailure(callback, error);
          return;
        }
        StreamReader reader = null;
        int statusCode = -1;
        String statusLineMessage = null;
        Map<String, String> responseHeaders = new HashMap<String, String>();
        String responseBody = null;
        try {
          WebResponse rsp = req.EndGetResponse(result);
          reader = new StreamReader(rsp.GetResponseStream());
          HttpWebResponse httpResponse = (HttpWebResponse) rsp;
          statusCode = httpResponse.get_StatusCode().Value;
          statusLineMessage = httpResponse.get_StatusDescription();
          WebHeaderCollection respHeaders = rsp.get_Headers();
          for (String headerName : respHeaders.get_AllKeys()) {
            String value = respHeaders.Get(headerName);
            responseHeaders.put(headerName, value);
          }
          responseBody = reader.ReadToEnd();
          HttpResponse response = new HttpResponse(
              statusCode, statusLineMessage, responseHeaders, responseBody);
          platform.notifySuccess(callback, response);
        } catch (Throwable t) {
          HttpException error = new HttpException(
              statusCode, statusLineMessage, responseBody, t, HttpErrorType.SERVER_ERROR);
          platform.notifyFailure(callback, error);
        } finally {
          if (reader != null)
            reader.Close();
        }
      }
    });
  }
}
