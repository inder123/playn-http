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

/**
 * An exception thrown on an Http call failure. It encapsulate the full Http response.
 *
 * @author Inderjeet Singh
 */
public class HttpException extends IOException {
  private static final long serialVersionUID = 6876951484475455549L;
  private final int responseStatusCode;
  private final String statusLineMessage;
  private final String responseBody;
  private final HttpErrorType errorType;

  public HttpException(int responseStatusCode, String statusLineMessage, String responseBody,
      Throwable cause, HttpErrorType errorType) {
    this.responseStatusCode = responseStatusCode;
    this.statusLineMessage = statusLineMessage;
    this.responseBody = responseBody;
    this.errorType = errorType;
  }

  public int getResponseStatusCode() {
    return responseStatusCode;
  }

  public String getStatusLineMessage() {
    return statusLineMessage;
  }

  public String getResponseBody() {
    return responseBody;
  }

  public HttpErrorType getErrorType() {
    return errorType;
  }
}
