  @Test public void testAppliesHeadersInGlideUrl() throws Exception {
    mockWebServer.enqueue(new MockResponse().setResponseCode(200));
    String headerField = "field";
    String headerValue = "value";
    Map<String, String> headersMap = 
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\glide\revisions\rev_e161ca9_7e0f873\rev_left_e161ca9\library\src\androidTest\java\com\bumptech\glide\load\data\HttpUrlFetcherServerTest.java
new HashMap<String, String>()
=======
new HashMap<>()
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\glide\revisions\rev_e161ca9_7e0f873\rev_right_7e0f873\library\src\androidTest\java\com\bumptech\glide\load\data\HttpUrlFetcherServerTest.java
;
    headersMap.put(headerField, headerValue);
    Headers headers = mock(Headers.class);
    when(headers.getHeaders()).thenReturn(headersMap);
    getFetcher(headers).loadData(Priority.HIGH);
    assertThat(mockWebServer.takeRequest().getHeader(headerField)).isEqualTo(headerValue);
  }


