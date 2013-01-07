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

import playn.core.util.Callback;

/**
 * A class similar to PlayN for supporting HTTP related functionality.
 *
 * @author Inderjeet Singh
 */
public abstract class Http {
  private static Http instance;

  public static void register(Http http) {
    instance = http;
  }

  public static void send(final HttpRequest request, final Callback<HttpResponse> callback) {
    if (instance == null) {
      throw new IllegalStateException("Call Http.register() once before invoking Http.send().");
    }
    instance.doSend(request, callback);
  }

  protected abstract void doSend(HttpRequest request, Callback<HttpResponse> callback);
}
