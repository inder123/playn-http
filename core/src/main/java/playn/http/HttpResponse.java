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

import java.util.Map;

/**
 * Http response.
 *
 * @author Inderjeet Singh
 */
public class HttpResponse {

  private final int statusCode;
  private final String statusLineMessage;
  private final Map<String, String> headers;
  private final String body;
  // TODO: Add support for cookies

  public HttpResponse(int statusCode, String statusLineMessage, Map<String, String> headers,
      String body) {
    this.statusCode = statusCode;
    this.statusLineMessage = statusLineMessage;
    this.headers = headers;
    this.body = body;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public String getStatusLineMessage() {
    return statusLineMessage;
  }

  public String getHeader(String headerName) {
    return headers.get(headerName);
  }

  public String getBody() {
    return body;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("HTTP Response Status Code ").append(statusCode).append(" ").append(statusLineMessage);
    sb.append("\nResponse Headers: ").append(headers);
    sb.append("\nResponse Body: ").append(body).append("\n");
    return sb.toString();
  }
}
