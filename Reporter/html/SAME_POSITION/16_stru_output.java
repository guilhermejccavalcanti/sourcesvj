package com.bumptech.glide.load.data;
import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.Headers;
import com.bumptech.glide.testutil.TestUtil;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.mockito.ArgumentCaptor;
import org.robolectric.annotation.Config;
import org.mockito.Mock;
import java.io.IOException;
import org.mockito.MockitoAnnotations;
import java.io.InputStream;
import org.robolectric.RobolectricTestRunner;
import java.net.HttpURLConnection;
import org.robolectric.annotation.Config;
import java.net.SocketTimeoutException;
import java.io.IOException;
import java.net.URL;
import java.io.InputStream;
import java.util.HashMap;
import java.net.HttpURLConnection;
import java.util.Map;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RunWith(value = RobolectricTestRunner.class) @Config(manifest = Config.NONE, emulateSdk = 18) public class HttpUrlFetcherServerTest {
  private static final String DEFAULT_PATH = "/fakepath";
  private static final int TIMEOUT_TIME_MS = 300;
  @Mock DataFetcher.DataCallback<InputStream> callback;
  private MockWebServer mockWebServer;
  private boolean defaultFollowRedirects;
  private ArgumentCaptor<InputStream> streamCaptor;
  @Before public void setUp() throws IOException {
    MockitoAnnotations.initMocks(this);
    defaultFollowRedirects = HttpURLConnection.getFollowRedirects();
    HttpURLConnection.setFollowRedirects(false);
    mockWebServer = new MockWebServer();
    mockWebServer.play();
    streamCaptor = ArgumentCaptor.forClass(InputStream.class);
  }
  @After public void tearDown() throws IOException {
    HttpURLConnection.setFollowRedirects(defaultFollowRedirects);
    mockWebServer.shutdown();
  }
  @Test public void testReturnsInputStreamOnStatusOk() throws Exception {
    String expected = "fakedata";
    mockWebServer.enqueue(new MockResponse().setBody(expected).setResponseCode(200));
    HttpUrlFetcher fetcher = getFetcher();
    fetcher.loadData(Priority.HIGH, callback);
    verify(callback).onDataReady(streamCaptor.capture());
    TestUtil.assertStreamOf(expected, streamCaptor.getValue());
  }
  @Test public void testHandlesRedirect301s() throws Exception {
    String expected = "fakedata";
    mockWebServer.enqueue(new MockResponse().setResponseCode(301).setHeader("Location", mockWebServer.getUrl("/redirect")));
    mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(expected));
    getFetcher().loadData(Priority.LOW, callback);
    verify(callback).onDataReady(streamCaptor.capture());
    TestUtil.assertStreamOf(expected, streamCaptor.getValue());
  }
  @Test public void testHandlesRedirect302s() throws Exception {
    String expected = "fakedata";
    mockWebServer.enqueue(new MockResponse().setResponseCode(302).setHeader("Location", mockWebServer.getUrl("/redirect")));
    mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(expected));
    getFetcher().loadData(Priority.LOW, callback);
    verify(callback).onDataReady(streamCaptor.capture());
    TestUtil.assertStreamOf(expected, streamCaptor.getValue());
  }
  @Test public void testHandlesRelativeRedirects() throws Exception {
    String expected = "fakedata";
    mockWebServer.enqueue(new MockResponse().setResponseCode(301).setHeader("Location", "/redirect"));
    mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(expected));
    getFetcher().loadData(Priority.NORMAL, callback);
    verify(callback).onDataReady(streamCaptor.capture());
    TestUtil.assertStreamOf(expected, streamCaptor.getValue());
    mockWebServer.takeRequest();
    RecordedRequest second = mockWebServer.takeRequest();
    assertThat(second.getPath()).endsWith("/redirect");
  }
  @Test public void testHandlesUpToFiveRedirects() throws Exception {
    int numRedirects = 4;
    String expected = "redirectedData";
    String redirectBase = "/redirect";
    for (int i = 0; i < numRedirects; i++) {
      mockWebServer.enqueue(new MockResponse().setResponseCode(301).setHeader("Location", mockWebServer.getUrl(redirectBase + i)));
    }
    mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(expected));
    getFetcher().loadData(Priority.NORMAL, callback);
    verify(callback).onDataReady(streamCaptor.capture());
    TestUtil.assertStreamOf(expected, streamCaptor.getValue());
    assertThat(mockWebServer.takeRequest().getPath()).contains(DEFAULT_PATH);
    for (int i = 0; i < numRedirects; i++) {
      assertThat(mockWebServer.takeRequest().getPath()).contains(redirectBase + i);
    }
  }
  @Test public void testFailsOnRedirectLoops() throws Exception {
    mockWebServer.enqueue(new MockResponse().setResponseCode(301).setHeader("Location", mockWebServer.getUrl("/redirect")));
    mockWebServer.enqueue(new MockResponse().setResponseCode(301).setHeader("Location", mockWebServer.getUrl("/redirect")));
    getFetcher().loadData(Priority.IMMEDIATE, callback);
    verify(callback).onDataReady(isNull(InputStream.class));
  }
  @Test public void testFailsIfRedirectLocationIsNotPresent() throws Exception {
    mockWebServer.enqueue(new MockResponse().setResponseCode(301));
    getFetcher().loadData(Priority.NORMAL, callback);
    verify(callback).onDataReady(isNull(InputStream.class));
  }
  @Test public void testFailsIfRedirectLocationIsPresentAndEmpty() throws Exception {
    mockWebServer.enqueue(new MockResponse().setResponseCode(301).setHeader("Location", ""));
    getFetcher().loadData(Priority.NORMAL, callback);
    verify(callback).onDataReady(isNull(InputStream.class));
  }
  @Test public void testFailsIfStatusCodeIsNegativeOne() throws Exception {
    mockWebServer.enqueue(new MockResponse().setResponseCode(-1));
    getFetcher().loadData(Priority.LOW, callback);
    verify(callback).onDataReady(isNull(InputStream.class));
  }
  @Test public void testFailsAfterTooManyRedirects() throws Exception {
    for (int i = 0; i < 10; i++) {
      mockWebServer.enqueue(new MockResponse().setResponseCode(301).setHeader("Location", mockWebServer.getUrl("/redirect" + i)));
    }
    getFetcher().loadData(Priority.NORMAL, callback);
    verify(callback).onDataReady(isNull(InputStream.class));
  }
  @Test public void testFailsIfStatusCodeIs500() throws Exception {
    mockWebServer.enqueue(new MockResponse().setResponseCode(500));
    getFetcher().loadData(Priority.NORMAL, callback);
    verify(callback).onDataReady(isNull(InputStream.class));
  }
  @Test public void testFailsIfStatusCodeIs400() throws Exception {
    mockWebServer.enqueue(new MockResponse().setResponseCode(400));
    getFetcher().loadData(Priority.LOW, callback);
    verify(callback).onDataReady(isNull(InputStream.class));
  }
  @Test(expected = SocketTimeoutException.class) public void testSetsReadTimeout() throws Exception {
    MockWebServer tempWebServer = new MockWebServer();
    tempWebServer.enqueue(new MockResponse().setBody("test").throttleBody(1, TIMEOUT_TIME_MS, TimeUnit.MILLISECONDS));
    tempWebServer.play();
    try {
      getFetcher().loadData(Priority.HIGH, callback);
    }
    finally {
      tempWebServer.shutdown();
      mockWebServer.enqueue(new MockResponse().setResponseCode(200));
    }
    verify(callback).onDataReady(isNull(InputStream.class));
  }
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
    getFetcher(headers).loadData(Priority.HIGH, callback);
    assertThat(mockWebServer.takeRequest().getHeader(headerField)).isEqualTo(headerValue);
  }
  private HttpUrlFetcher getFetcher() {
    return getFetcher(Headers.NONE);
  }
  private HttpUrlFetcher getFetcher(Headers headers) {
    URL url = mockWebServer.getUrl(DEFAULT_PATH);
    return new HttpUrlFetcher(new GlideUrl(url, headers), TIMEOUT_TIME_MS, HttpUrlFetcher.DEFAULT_CONNECTION_FACTORY);
  }
}

