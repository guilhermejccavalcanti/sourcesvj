  private InputStream loadDataWithRedirects(URL url, int redirects, URL lastUrl, Map<String, String> headers) throws IOException {
    if (redirects >= MAXIMUM_REDIRECTS) {
      throw new IOException("Too many (> " + MAXIMUM_REDIRECTS + ") redirects!");
    }
    else {
      try {
        if (lastUrl != null && url.toURI().equals(lastUrl.toURI())) {
          throw new IOException("In re-direct loop");
        }
      }
      catch (URISyntaxException e) {
      }
    }
    urlConnection = connectionFactory.build(url);
    for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
      urlConnection.addRequestProperty(headerEntry.getKey(), headerEntry.getValue());
    }
    if (TextUtils.isEmpty(urlConnection.getRequestProperty(ENCODING_HEADER))) {
      urlConnection.setRequestProperty(ENCODING_HEADER, DEFAULT_ENCODING);
    }
    urlConnection.setConnectTimeout(timeout);
    urlConnection.setReadTimeout(timeout);
    urlConnection.setUseCaches(false);
    urlConnection.setDoInput(true);
    urlConnection.connect();
    if (isCancelled) {
      return null;
    }
    final int statusCode = urlConnection.getResponseCode();
    if (statusCode / 100 == 2) {
      String contentLength = urlConnection.getHeaderField(CONTENT_LENGTH_HEADER);
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\glide\revisions\rev_e161ca9_7e0f873\rev_left_e161ca9\library\src\main\java\com\bumptech\glide\load\data\HttpUrlFetcher.java
return getStreamForSuccessfulRequest(urlConnection);
=======
stream = ContentLengthInputStream.obtain(urlConnection.getInputStream(), contentLength);
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\glide\revisions\rev_e161ca9_7e0f873\rev_right_7e0f873\library\src\main\java\com\bumptech\glide\load\data\HttpUrlFetcher.java

    }
  }


