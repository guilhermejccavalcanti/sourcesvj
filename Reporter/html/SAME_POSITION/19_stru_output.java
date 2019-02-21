package com.bumptech.glide;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.data.InputStreamRewinder;
import com.bumptech.glide.load.engine.Engine;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.engine.cache.MemoryCache;
import com.bumptech.glide.load.engine.bitmap_recycle.ByteArrayPool;
import com.bumptech.glide.load.engine.prefill.BitmapPreFiller;
import com.bumptech.glide.load.engine.cache.MemoryCache;
import com.bumptech.glide.load.engine.prefill.PreFillType;
import com.bumptech.glide.load.engine.prefill.BitmapPreFiller;
import com.bumptech.glide.load.model.GenericLoaderFactory;
import com.bumptech.glide.load.engine.prefill.PreFillType;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.AssetUriLoader;
import com.bumptech.glide.load.model.ImageVideoWrapper;
import com.bumptech.glide.load.model.ByteArrayLoader;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ByteBufferEncoder;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.ByteBufferFileLoader;
import com.bumptech.glide.load.model.file_descriptor.FileDescriptorFileLoader;
import com.bumptech.glide.load.model.FileLoader;
import com.bumptech.glide.load.model.file_descriptor.FileDescriptorResourceLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.file_descriptor.FileDescriptorStringLoader;
import com.bumptech.glide.load.model.ResourceLoader;
import com.bumptech.glide.load.model.file_descriptor.FileDescriptorUriLoader;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.model.stream.HttpUrlGlideUrlLoader;
import com.bumptech.glide.load.model.StringLoader;
import com.bumptech.glide.load.model.stream.StreamByteArrayLoader;
import com.bumptech.glide.load.model.UnitModelLoader;
import com.bumptech.glide.load.model.stream.StreamFileLoader;
import com.bumptech.glide.load.model.UriLoader;
import com.bumptech.glide.load.model.stream.StreamResourceLoader;
import com.bumptech.glide.load.model.UrlUriLoader;
import com.bumptech.glide.load.model.stream.StreamStringLoader;
import com.bumptech.glide.load.model.stream.HttpGlideUrlLoader;
import com.bumptech.glide.load.model.stream.StreamUriLoader;
import com.bumptech.glide.load.model.stream.HttpUriLoader;
import com.bumptech.glide.load.model.stream.StreamUrlLoader;
import com.bumptech.glide.load.model.stream.MediaStoreImageThumbLoader;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.model.stream.MediaStoreVideoThumbLoader;
import com.bumptech.glide.load.resource.bitmap.FileDescriptorBitmapDataLoadProvider;
import com.bumptech.glide.load.model.stream.UrlLoader;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.BitmapDrawableDecoder;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.bitmap.BitmapDrawableEncoder;
import com.bumptech.glide.load.resource.bitmap.ImageVideoDataLoadProvider;
import com.bumptech.glide.load.resource.bitmap.BitmapEncoder;
import com.bumptech.glide.load.resource.bitmap.StreamBitmapDataLoadProvider;
import com.bumptech.glide.load.resource.bitmap.ByteBufferBitmapDecoder;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.bitmap.Downsampler;
import com.bumptech.glide.load.resource.file.StreamFileDataLoadProvider;
import com.bumptech.glide.load.resource.bitmap.StreamBitmapDecoder;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.load.resource.bitmap.VideoBitmapDecoder;
import com.bumptech.glide.load.resource.gif.GifDrawableLoadProvider;
import com.bumptech.glide.load.resource.bytes.ByteBufferRewinder;
import com.bumptech.glide.load.resource.gifbitmap.GifBitmapWrapper;
import com.bumptech.glide.load.resource.file.FileDecoder;
import com.bumptech.glide.load.resource.gifbitmap.GifBitmapWrapperTransformation;
import com.bumptech.glide.load.resource.gif.ByteBufferGifDecoder;
import com.bumptech.glide.load.resource.gifbitmap.ImageVideoGifDrawableLoadProvider;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.load.resource.transcode.GifBitmapWrapperDrawableTranscoder;
import com.bumptech.glide.load.resource.gif.GifDrawableEncoder;
import com.bumptech.glide.load.resource.transcode.GlideBitmapDrawableTranscoder;
import com.bumptech.glide.load.resource.gif.GifFrameResourceDecoder;
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder;
import com.bumptech.glide.load.resource.gif.StreamGifDecoder;
import com.bumptech.glide.load.resource.transcode.TranscoderRegistry;
import com.bumptech.glide.load.resource.transcode.BitmapBytesTranscoder;
import com.bumptech.glide.manager.RequestManagerRetriever;
import com.bumptech.glide.load.resource.transcode.BitmapDrawableTranscoder;
import com.bumptech.glide.module.GlideModule;
import com.bumptech.glide.load.resource.transcode.GifDrawableBytesTranscoder;
import com.bumptech.glide.module.ManifestParser;
import com.bumptech.glide.manager.RequestManagerRetriever;
import com.bumptech.glide.provider.DataLoadProvider;
import com.bumptech.glide.module.GlideModule;
import com.bumptech.glide.provider.DataLoadProviderRegistry;
import com.bumptech.glide.module.ManifestParser;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ImageViewTargetFactory;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.util.Util;
import com.bumptech.glide.request.target.ViewTarget;
import java.io.File;
import com.bumptech.glide.util.Util;
import java.io.InputStream;
import java.io.File;
import java.net.URL;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@TargetApi(value = Build.VERSION_CODES.ICE_CREAM_SANDWICH) public class Glide implements ComponentCallbacks2 {
  private static final String TAG = "Glide";
  private static volatile Glide glide;
  private final Engine engine;
  private final BitmapPool bitmapPool;
  private final MemoryCache memoryCache;
  private final BitmapPreFiller bitmapPreFiller;
  private final GlideContext glideContext;
  private final Registry registry;
  private final ByteArrayPool byteArrayPool;
  private final List<RequestManager> managers = new ArrayList<>();
  public static File getPhotoCacheDir(Context context) {
    return getPhotoCacheDir(context, DiskLruCacheFactory.DEFAULT_DISK_CACHE_DIR);
  }
  public static File getPhotoCacheDir(Context context, String cacheName) {
    File cacheDir = context.getCacheDir();
    if (cacheDir != null) {
      File result = new File(cacheDir, cacheName);
      if (!result.mkdirs() && (!result.exists() || !result.isDirectory())) {
        return null;
      }
      return result;
    }
    if (Log.isLoggable(TAG, Log.ERROR)) {
      Log.e(TAG, "default disk cache dir is null");
    }
    return null;
  }
  public static Glide get(Context context) {
    if (glide == null) {
      synchronized(Glide.class) {
        if (glide == null) {
          Context applicationContext = context.getApplicationContext();
          List<GlideModule> modules = new ManifestParser(applicationContext).parse();
          GlideBuilder builder = new GlideBuilder(applicationContext);
          for (GlideModule module : modules) {
            module.applyOptions(applicationContext, builder);
          }
          glide = builder.createGlide();
          for (GlideModule module : modules) {
            module.registerComponents(applicationContext, glide.registry);
          }
        }
      }
    }
    return glide;
  }
  static void tearDown() {
    glide = null;
  }
  @TargetApi(value = Build.VERSION_CODES.ICE_CREAM_SANDWICH) Glide(Engine engine, MemoryCache memoryCache, BitmapPool bitmapPool, ByteArrayPool byteArrayPool, Context context, DecodeFormat decodeFormat) {
    this.engine = engine;
    this.bitmapPool = bitmapPool;
    this.byteArrayPool = byteArrayPool;
    this.memoryCache = memoryCache;
    bitmapPreFiller = new BitmapPreFiller(memoryCache, bitmapPool, decodeFormat);
    Resources resources = context.getResources();
    Downsampler downsampler = new Downsampler(resources.getDisplayMetrics(), bitmapPool, byteArrayPool);
    ByteBufferGifDecoder byteBufferGifDecoder = new ByteBufferGifDecoder(context, bitmapPool, byteArrayPool);
    registry = new Registry(context).register(ByteBuffer.class, new ByteBufferEncoder()).register(InputStream.class, new StreamEncoder(byteArrayPool)).append(ByteBuffer.class, Bitmap.class, new ByteBufferBitmapDecoder(downsampler)).append(InputStream.class, Bitmap.class, new StreamBitmapDecoder(downsampler, byteArrayPool)).append(ParcelFileDescriptor.class, Bitmap.class, new VideoBitmapDecoder(bitmapPool)).register(Bitmap.class, new BitmapEncoder()).append(ByteBuffer.class, BitmapDrawable.class, new BitmapDrawableDecoder<>(resources, bitmapPool, new ByteBufferBitmapDecoder(downsampler))).append(InputStream.class, BitmapDrawable.class, new BitmapDrawableDecoder<>(resources, bitmapPool, new StreamBitmapDecoder(downsampler, byteArrayPool))).append(ParcelFileDescriptor.class, BitmapDrawable.class, new BitmapDrawableDecoder<>(resources, bitmapPool, new VideoBitmapDecoder(bitmapPool))).register(BitmapDrawable.class, new BitmapDrawableEncoder(bitmapPool, new BitmapEncoder())).prepend(InputStream.class, GifDrawable.class, new StreamGifDecoder(byteBufferGifDecoder, byteArrayPool)).prepend(ByteBuffer.class, GifDrawable.class, byteBufferGifDecoder).register(GifDrawable.class, new GifDrawableEncoder()).append(GifDecoder.class, GifDecoder.class, new UnitModelLoader.Factory<GifDecoder>()).append(GifDecoder.class, Bitmap.class, new GifFrameResourceDecoder(bitmapPool)).register(new ByteBufferRewinder.Factory()).append(File.class, ByteBuffer.class, new ByteBufferFileLoader.Factory()).append(File.class, InputStream.class, new FileLoader.StreamFactory()).append(File.class, File.class, new FileDecoder()).append(File.class, ParcelFileDescriptor.class, new FileLoader.FileDescriptorFactory()).append(File.class, File.class, new UnitModelLoader.Factory<File>()).register(new InputStreamRewinder.Factory(byteArrayPool)).append(int.class, InputStream.class, new ResourceLoader.StreamFactory()).append(int.class, ParcelFileDescriptor.class, new ResourceLoader.FileDescriptorFactory()).append(Integer.class, InputStream.class, new ResourceLoader.StreamFactory()).append(Integer.class, ParcelFileDescriptor.class, new ResourceLoader.FileDescriptorFactory()).append(String.class, InputStream.class, new StringLoader.StreamFactory()).append(String.class, ParcelFileDescriptor.class, new StringLoader.FileDescriptorFactory()).append(Uri.class, InputStream.class, new HttpUriLoader.Factory()).append(Uri.class, InputStream.class, new AssetUriLoader.StreamFactory()).append(Uri.class, ParcelFileDescriptor.class, new AssetUriLoader.FileDescriptorFactory()).append(Uri.class, InputStream.class, new MediaStoreImageThumbLoader.Factory()).append(Uri.class, InputStream.class, new MediaStoreVideoThumbLoader.Factory()).append(Uri.class, InputStream.class, new UriLoader.StreamFactory()).append(Uri.class, ParcelFileDescriptor.class, new UriLoader.FileDescriptorFactory()).append(Uri.class, InputStream.class, new UrlUriLoader.StreamFactory()).append(URL.class, InputStream.class, new UrlLoader.StreamFactory()).append(GlideUrl.class, InputStream.class, new HttpGlideUrlLoader.Factory()).append(byte[].class, ByteBuffer.class, new ByteArrayLoader.ByteBufferFactory()).append(byte[].class, InputStream.class, new ByteArrayLoader.StreamFactory()).register(Bitmap.class, BitmapDrawable.class, new BitmapDrawableTranscoder(resources, bitmapPool)).register(Bitmap.class, byte[].class, new BitmapBytesTranscoder()).register(GifDrawable.class, byte[].class, new GifDrawableBytesTranscoder());
    ImageViewTargetFactory imageViewTargetFactory = new ImageViewTargetFactory();
    RequestOptions options = new RequestOptions().format(decodeFormat);
    glideContext = new GlideContext(context, registry, imageViewTargetFactory, options, engine, this);
    ImageVideoDataLoadProvider imageVideoDataLoadProvider = new ImageVideoDataLoadProvider(streamBitmapLoadProvider, fileDescriptorLoadProvider);
    dataLoadProviderRegistry.register(ImageVideoWrapper.class, Bitmap.class, imageVideoDataLoadProvider);
    GifDrawableLoadProvider gifDrawableLoadProvider = new GifDrawableLoadProvider(context, bitmapPool);
    dataLoadProviderRegistry.register(InputStream.class, GifDrawable.class, gifDrawableLoadProvider);
    dataLoadProviderRegistry.register(ImageVideoWrapper.class, GifBitmapWrapper.class, new ImageVideoGifDrawableLoadProvider(imageVideoDataLoadProvider, gifDrawableLoadProvider, bitmapPool));
    dataLoadProviderRegistry.register(InputStream.class, File.class, new StreamFileDataLoadProvider());
    register(File.class, ParcelFileDescriptor.class, new FileDescriptorFileLoader.Factory());
    register(File.class, InputStream.class, new StreamFileLoader.Factory());
    register(int.class, ParcelFileDescriptor.class, new FileDescriptorResourceLoader.Factory());
    register(int.class, InputStream.class, new StreamResourceLoader.Factory());
    register(Integer.class, ParcelFileDescriptor.class, new FileDescriptorResourceLoader.Factory());
    register(Integer.class, InputStream.class, new StreamResourceLoader.Factory());
    register(String.class, ParcelFileDescriptor.class, new FileDescriptorStringLoader.Factory());
    register(String.class, InputStream.class, new StreamStringLoader.Factory());
    register(Uri.class, ParcelFileDescriptor.class, new FileDescriptorUriLoader.Factory());
    register(Uri.class, InputStream.class, new StreamUriLoader.Factory());
    register(URL.class, InputStream.class, new StreamUrlLoader.Factory());
    register(GlideUrl.class, InputStream.class, new HttpUrlGlideUrlLoader.Factory());
    register(byte[].class, InputStream.class, new StreamByteArrayLoader.Factory());
    transcoderRegistry.register(Bitmap.class, GlideBitmapDrawable.class, new GlideBitmapDrawableTranscoder(context.getResources(), bitmapPool));
    transcoderRegistry.register(GifBitmapWrapper.class, GlideDrawable.class, new GifBitmapWrapperDrawableTranscoder(new GlideBitmapDrawableTranscoder(context.getResources(), bitmapPool)));
    bitmapCenterCrop = new CenterCrop(bitmapPool);
    drawableCenterCrop = new GifBitmapWrapperTransformation(bitmapPool, bitmapCenterCrop);
    bitmapFitCenter = new FitCenter(bitmapPool);
    drawableFitCenter = new GifBitmapWrapperTransformation(context, registry);
  }
  public BitmapPool getBitmapPool() {
    return bitmapPool;
  }
  public ByteArrayPool getByteArrayPool() {
    return byteArrayPool;
  }
  GlideContext getGlideContext() {
    return glideContext;
  }
  public void preFillBitmapPool(PreFillType.Builder ... bitmapAttributeBuilders) {
    bitmapPreFiller.preFill(bitmapAttributeBuilders);
  }
  public void clearMemory() {
    bitmapPool.clearMemory();
    memoryCache.clearMemory();
    byteArrayPool.clearMemory();
  }
  public void trimMemory(int level) {
    bitmapPool.trimMemory(level);
    memoryCache.trimMemory(level);
    byteArrayPool.trimMemory(level);
  }
  public void clearDiskCache() {
    Util.assertBackgroundThread();
    
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\glide\revisions\rev_e161ca9_7e0f873\rev_left_e161ca9\library\src\main\java\com\bumptech\glide\Glide.java
getEngine().clearDiskCache()
=======
engine.clearDiskCache()
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\glide\revisions\rev_e161ca9_7e0f873\rev_right_7e0f873\library\src\main\java\com\bumptech\glide\Glide.java
;
  }
  public void setMemoryCategory(MemoryCategory memoryCategory) {
    memoryCache.setSizeMultiplier(memoryCategory.getMultiplier());
    bitmapPool.setSizeMultiplier(memoryCategory.getMultiplier());
  }
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\glide\revisions\rev_e161ca9_7e0f873\rev_left_e161ca9\library\src\main\java\com\bumptech\glide\Glide.java
public static void clear(Target<?> target) {
    Util.assertMainThread();
    Request request = target.getRequest();
    if (request != null) {
      request.clear();
      target.setRequest(null);
    }
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.

  public static RequestManager with(Context context) {
    RequestManagerRetriever retriever = RequestManagerRetriever.get();
    return retriever.get(context);
  }
  public static RequestManager with(Activity activity) {
    RequestManagerRetriever retriever = RequestManagerRetriever.get();
    return retriever.get(activity);
  }
  public static RequestManager with(FragmentActivity activity) {
    RequestManagerRetriever retriever = RequestManagerRetriever.get();
    return retriever.get(activity);
  }
  @TargetApi(value = Build.VERSION_CODES.HONEYCOMB) public static RequestManager with(android.app.Fragment fragment) {
    RequestManagerRetriever retriever = RequestManagerRetriever.get();
    return retriever.get(fragment);
  }
  public static RequestManager with(Fragment fragment) {
    RequestManagerRetriever retriever = RequestManagerRetriever.get();
    return retriever.get(fragment);
  }
  public Registry getRegistry() {
    return registry;
  }
  void removeFromManagers(Target<?> target, Request request) {
    for (RequestManager requestManager : managers) {
      if (requestManager.untrack(target, request)) {
        return ;
      }
    }
    if (request != null) {
      throw new IllegalStateException("Failed to remove request from managers");
    }
  }
  void registerRequestManager(RequestManager requestManager) {
    synchronized(managers) {
      if (managers.contains(requestManager)) {
        throw new IllegalStateException("Cannot register already registered manager");
      }
      managers.add(requestManager);
    }
  }
  void unregisterRequestManager(RequestManager requestManager) {
    synchronized(managers) {
      if (!managers.contains(requestManager)) {
        throw new IllegalStateException("Cannot register not yet registered manager");
      }
      managers.remove(requestManager);
    }
  }
  @Override public void onTrimMemory(int level) {
    trimMemory(level);
  }
  @Override public void onConfigurationChanged(Configuration newConfig) {
  }
  @Override public void onLowMemory() {
    clearMemory();
  }
  public void clearMemory() {
    bitmapPool.clearMemory();
    memoryCache.clearMemory();
    byteArrayPool.clearMemory();
  }
  public void trimMemory(int level) {
    bitmapPool.trimMemory(level);
    memoryCache.trimMemory(level);
    byteArrayPool.trimMemory(level);
  }
  public void clearDiskCache() {
    Util.assertBackgroundThread();
    
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\glide\revisions\rev_e161ca9_7e0f873\rev_left_e161ca9\library\src\main\java\com\bumptech\glide\Glide.java
getEngine().clearDiskCache()
=======
engine.clearDiskCache()
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\glide\revisions\rev_e161ca9_7e0f873\rev_right_7e0f873\library\src\main\java\com\bumptech\glide\Glide.java
;
  }
  public void setMemoryCategory(MemoryCategory memoryCategory) {
    memoryCache.setSizeMultiplier(memoryCategory.getMultiplier());
    bitmapPool.setSizeMultiplier(memoryCategory.getMultiplier());
  }
  public static void clear(Target<?> target) {
    Util.assertMainThread();
    Request request = target.getRequest();
    if (request != null) {
      request.clear();
      target.setRequest(null);
    }
  }
  public static void clear(FutureTarget<?> target) {
    target.clear();
  }
  public static void clear(View view) {
    Target<?> viewTarget = new ClearTarget(view);
    clear(viewTarget);
  }
  public  <T extends java.lang.Object, Y extends java.lang.Object> void register(Class<T> modelClass, Class<Y> resourceClass, ModelLoaderFactory<T, Y> factory) {
    ModelLoaderFactory<T, Y> removed = loaderFactory.register(modelClass, resourceClass, factory);
    if (removed != null) {
      removed.teardown();
    }
  }
  @Deprecated public  <T extends java.lang.Object, Y extends java.lang.Object> void unregister(Class<T> modelClass, Class<Y> resourceClass) {
    ModelLoaderFactory<T, Y> removed = loaderFactory.unregister(modelClass, resourceClass);
    if (removed != null) {
      removed.teardown();
    }
  }
  public static  <T extends java.lang.Object, Y extends java.lang.Object> ModelLoader<T, Y> buildModelLoader(Class<T> modelClass, Class<Y> resourceClass, Context context) {
    if (modelClass == null) {
      if (Log.isLoggable(TAG, Log.DEBUG)) {
        Log.d(TAG, "Unable to load null model, setting placeholder only");
      }
      return null;
    }
    return Glide.get(context).getLoaderFactory().buildModelLoader(modelClass, resourceClass);
  }
  @SuppressWarnings(value = {"unchecked", }) public static  <T extends java.lang.Object, Y extends java.lang.Object> ModelLoader<T, Y> buildModelLoader(T model, Class<Y> resourceClass, Context context) {
    return buildModelLoader(model != null ? (Class<T>)model.getClass() : null, resourceClass, context);
  }
  public static  <T extends java.lang.Object> ModelLoader<T, InputStream> buildStreamModelLoader(Class<T> modelClass, Context context) {
    return buildModelLoader(modelClass, InputStream.class, context);
  }
  public static  <T extends java.lang.Object> ModelLoader<T, InputStream> buildStreamModelLoader(T model, Context context) {
    return buildModelLoader(model, InputStream.class, context);
  }
  public static  <T extends java.lang.Object> ModelLoader<T, ParcelFileDescriptor> buildFileDescriptorModelLoader(Class<T> modelClass, Context context) {
    return buildModelLoader(modelClass, ParcelFileDescriptor.class, context);
  }
  public static  <T extends java.lang.Object> ModelLoader<T, ParcelFileDescriptor> buildFileDescriptorModelLoader(T model, Context context) {
    return buildModelLoader(model, ParcelFileDescriptor.class, context);
  }
  public static RequestManager with(Context context) {
    RequestManagerRetriever retriever = RequestManagerRetriever.get();
    return retriever.get(context);
  }
  public static RequestManager with(Activity activity) {
    RequestManagerRetriever retriever = RequestManagerRetriever.get();
    return retriever.get(activity);
  }
  public static RequestManager with(FragmentActivity activity) {
    RequestManagerRetriever retriever = RequestManagerRetriever.get();
    return retriever.get(activity);
  }
  @TargetApi(value = Build.VERSION_CODES.HONEYCOMB) public static RequestManager with(android.app.Fragment fragment) {
    RequestManagerRetriever retriever = RequestManagerRetriever.get();
    return retriever.get(fragment);
  }
  public static RequestManager with(Fragment fragment) {
    RequestManagerRetriever retriever = RequestManagerRetriever.get();
    return retriever.get(fragment);
  }
  
  private static class ClearTarget extends ViewTarget<View, Object> {
    public ClearTarget(View view) {
      super(view);
    }
    @Override public void onLoadStarted(Drawable placeholder) {
    }
    @Override public void onLoadFailed(Exception e, Drawable errorDrawable) {
    }
    @Override public void onResourceReady(Object resource, GlideAnimation<? super Object> glideAnimation) {
    }
    @Override public void onLoadCleared(Drawable placeholder) {
    }
  }
}

