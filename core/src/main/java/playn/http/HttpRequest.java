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
import java.util.Map.Entry;
import java.util.Set;

/**
 * A representation of an HTTP request
 *
 * @author Inderjeet Singh
 */
public class HttpRequest {
  private final HttpMethod method;
  private final String url;
  private final Map<String, String> headers;
  private final String body;

  public static class Builder {
    private final HttpMethod method;
    private final String url;
    private final Map<String, String> headers = new HashMap<String, String>();
    private String body;

    public Builder(HttpMethod method, String url) {
      this(method, false, url);
    }

    public Builder(HttpMethod method, boolean useHttpMethodOverride, String url) {
      if (useHttpMethodOverride) {
        this.method = HttpMethod.POST;
        headers.put(HttpMethod.HTTP_METHOD_OVERRIDE, method.toString());
      } else {
        this.method = method;
      }
      this.url = url;
    }
    public void header(String name, String value) {
      headers.put(name, value);
    }
    public void setBody(String body) {
      this.body = body;
    }
    public HttpRequest build() {
      return new HttpRequest(method, url, headers, body);
    }
  }

  public HttpRequest(HttpMethod method, String url, Map<String, String> headers, String body) {
    this.method = method;
    this.url = url;
    this.headers = headers;
    this.body = body;
  }

  public HttpMethod getMethod() {
    return method;
  }

  public String getUrl() {
    return url;
  }

  public Set<Entry<String,String>> getHeaders() {
    return headers.entrySet();
  }

  public String getBody() {
    return body;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("HTTP ").append(method).append(" ").append(url).append("\n");
    sb.append("Request Headers: ").append(headers).append("\n");
    if (body != null) sb.append("Request Body: ").append(body).append("\n");
    return sb.toString();
  }
}
