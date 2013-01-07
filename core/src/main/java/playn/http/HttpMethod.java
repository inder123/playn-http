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

public enum HttpMethod {
  GET, POST, PUT;

  /**
   * This header is used to indicate the real method that is channeled through the current
   * request. For example, you can use it to send PUT requests under a POST.
   * You can do this by setting a Request header on HttpURLConnection:
   * {@code conn.setRequestProperty(HttpMethod.SIMULATED_METHOD_HEADER, HttpMethod.PUT.toString());}
   * You should then send the request method to POST: {@code conn.setRequestMethod("POST");}
   */
  public static final String HTTP_METHOD_OVERRIDE = "X-HTTP-Method-Override";
}
