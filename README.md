playn-http
=========

Http extensions for the PlayN Framework. The PlayN framework's net package provides mechanisms to make HTTP calls. However, it is limited to GET and POST, and you can not pass HTTP headers or read response headers. That shortcoming is addressed by this project. You can create full HTTP requests with headers, read response status code or response headers.

Currently, Android, iOS, HTML and Java backends are supported. We have used it successfully in our game, <a href="https://www.facebook.com/appcenter/unscramblethis">Unscramble This</a>, for its <a href="https://play.google.com/store/apps/details?id=com.applimobile.unscramble">Android</a>, <a href="https://itunes.apple.com/us/app/unscramble-this-word/id570632973?mt=8">iOS</a> and <a href="https://apps.facebook.com/unscramblethis/">HTML</a> versions. You are welcome to submit patches for other PlayN backends.

Here is an example of how to use playn-http:

    HttpRequest.Builder requestBuilder = new HttpRequest.Builder(HttpMethod.POST, "http://myposturl.com/");
    requestBuilder.header("Header1", "Value1");
    requestBuilder.header("Content-Type", "application/json; charset=utf-8");
    requestBuilder.setBody("{'foo':'bar'}");
    HttpRequest request = requestBuilder.build();
    Http.send(request, new Callback<HttpResponse>() {
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
