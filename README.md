playn-http
=========

Http extensions for the PlayN Framework. The PlayN framework's net package provides mechanisms to make HTTP calls. However, it is limited to GET and POST, and you can not pass HTTP headers or read response headers. That shortcoming is addressed by this project. You can create full HTTP requests with headers, read response status code or response headers.

Currently, Android, iOS, HTML and Java backends are supported. You are welcome to submit patches for other PlayN backends.

Here is an example of how to use playn-http:

    HttpRequest.Builder requestBuilder = new HttpRequest.Builder(HttpMethod.POST, "http://myposturl.com/");
    requestBuilder.header("Header1", "Value1");
    requestBuilder.header("Content-Type", "application/json; charset=utf-8");
    requestBuilder.setBody("{'foo':'bar'}");
    Http.send(request.new Callback<HttpResponse>() {
      @Override public void onSuccess(HttpResponse response) {
        int httpStatusCode = response.getStatusCode();
        String responseBody = response.getBody();
        PlayN.log().info(response.toString());
        if (httpStatusCode >= 300) {
          onFailure(new RuntimeException("StatusCode: " + httpStatusCode + "responseBody: " + responseBody); 
        } else {
          // Yay successful response.
          PlayN.log().info("Response Header: " + response.getHeader("myResponseHeader"));
        }
      }
      @Override public void onFailure(Throwable cause) {
      }
    });
