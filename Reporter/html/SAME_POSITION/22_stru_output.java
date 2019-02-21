package com.bumptech.glide.load.engine.bitmap_recycle;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class LruBitmapPool implements BitmapPool {
  private static final String TAG = "LruBitmapPool";
  private static final Bitmap.Config DEFAULT_CONFIG = Bitmap.Config.ARGB_8888;
  private final LruPoolStrategy strategy;
  private final Set<Bitmap.Config> allowedConfigs;
  private final int initialMaxSize;
  private final BitmapTracker tracker;
  private int maxSize;
  private int currentSize;
  private int hits;
  private int misses;
  private int puts;
  private int evictions;
  LruBitmapPool(int maxSize, LruPoolStrategy strategy, Set<Bitmap.Config> allowedConfigs) {
    this.initialMaxSize = maxSize;
    this.maxSize = maxSize;
    this.strategy = strategy;
    this.allowedConfigs = allowedConfigs;
    this.tracker = new NullBitmapTracker();
  }
  public LruBitmapPool(int maxSize) {
    this(maxSize, getDefaultStrategy(), getDefaultAllowedConfigs());
  }
  public LruBitmapPool(int maxSize, Set<Bitmap.Config> allowedConfigs) {
    this(maxSize, getDefaultStrategy(), allowedConfigs);
  }
  @Override public int getMaxSize() {
    return maxSize;
  }
  @Override public synchronized void setSizeMultiplier(float sizeMultiplier) {
    maxSize = Math.round(initialMaxSize * sizeMultiplier);
    evict();
  }
  @Override public synchronized boolean put(Bitmap bitmap) {
    if (bitmap == null) {
      throw new NullPointerException("Bitmap must not be null");
    }
    if (bitmap.isRecycled()) {
      throw new IllegalStateException("Cannot pool recycled bitmap");
    }
    if (!bitmap.isMutable() || strategy.getSize(bitmap) > maxSize || !allowedConfigs.contains(bitmap.getConfig())) {
      if (Log.isLoggable(TAG, Log.VERBOSE)) {
        Log.v(TAG, "Reject bitmap from pool" + ", bitmap: " + strategy.logBitmap(bitmap) + ", is mutable: " + bitmap.isMutable() + ", is allowed config: " + allowedConfigs.contains(bitmap.getConfig()));
      }
      return false;
    }
    final int size = strategy.getSize(bitmap);
    strategy.put(bitmap);
    tracker.add(bitmap);
    puts++;
    currentSize += size;
    if (Log.isLoggable(TAG, Log.VERBOSE)) {
      Log.v(TAG, "Put bitmap in pool=" + strategy.logBitmap(bitmap));
    }
    dump();
    evict();
    return true;
  }
  private void evict() {
    trimToSize(maxSize);
  }
  @Override public synchronized Bitmap get(int width, int height, Bitmap.Config config) {
    Bitmap result = getDirty(width, height, config);
    if (result != null) {
      result.eraseColor(Color.TRANSPARENT);
    }
    return result;
  }
  @TargetApi(value = Build.VERSION_CODES.HONEYCOMB_MR1) @Override public synchronized Bitmap getDirty(int width, int height, Bitmap.Config config) {
    final Bitmap result = strategy.get(width, height, config != null ? config : DEFAULT_CONFIG);
    if (result == null) {
      if (Log.isLoggable(TAG, Log.DEBUG)) {
        Log.d(TAG, "Missing bitmap=" + strategy.logBitmap(width, height, config));
      }
      misses++;
    }
    else {
      hits++;
      currentSize -= strategy.getSize(result);
      tracker.remove(result);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
        result.setHasAlpha(true);
      }
    }
    if (Log.isLoggable(TAG, Log.VERBOSE)) {
      Log.v(TAG, "Get bitmap=" + strategy.logBitmap(width, height, config));
    }
    dump();
    return result;
  }
  @Override public void clearMemory() {
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\glide\revisions\rev_e161ca9_7e0f873\rev_left_e161ca9\library\src\main\java\com\bumptech\glide\load\engine\bitmap_recycle\LruBitmapPool.java
if (Log.isLoggable(TAG, Log.DEBUG)) {
      Log.d(TAG, "clearMemory");
    }
=======
trimToSize(0);
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\glide\revisions\rev_e161ca9_7e0f873\rev_right_7e0f873\library\src\main\java\com\bumptech\glide\load\engine\bitmap_recycle\LruBitmapPool.java

    trimToSize(0);
  }
  @SuppressLint(value = "InlinedApi") @Override public void trimMemory(int level) {
    if (Log.isLoggable(TAG, Log.DEBUG)) {
      Log.v(TAG, "trimMemory, level=" + level);
    }
    if (level >= android.content.ComponentCallbacks2.TRIM_MEMORY_MODERATE) {
      clearMemory();
    }
    else 
      if (level >= android.content.ComponentCallbacks2.TRIM_MEMORY_BACKGROUND) {
        trimToSize(maxSize / 2);
      }
  }
  private synchronized void trimToSize(int size) {
    while (currentSize > size){
      final Bitmap removed = strategy.removeLast();
      if (removed == null) {
        if (Log.isLoggable(TAG, Log.WARN)) {
          Log.w(TAG, "Size mismatch, resetting");
          dumpUnchecked();
        }
        currentSize = 0;
        return ;
      }
      tracker.remove(removed);
      currentSize -= strategy.getSize(removed);
      evictions++;
      if (Log.isLoggable(TAG, Log.DEBUG)) {
        Log.d(TAG, "Evicting bitmap=" + strategy.logBitmap(removed));
      }
      dump();
      removed.recycle();
    }
  }
  private void dump() {
    if (Log.isLoggable(TAG, Log.VERBOSE)) {
      dumpUnchecked();
    }
  }
  private void dumpUnchecked() {
    Log.v(TAG, "Hits=" + hits + ", misses=" + misses + ", puts=" + puts + ", evictions=" + evictions + ", currentSize=" + currentSize + ", maxSize=" + maxSize + "\nStrategy=" + strategy);
  }
  private static LruPoolStrategy getDefaultStrategy() {
    final LruPoolStrategy strategy;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      strategy = new SizeConfigStrategy();
    }
    else {
      strategy = new AttributeStrategy();
    }
    return strategy;
  }
  private static Set<Bitmap.Config> getDefaultAllowedConfigs() {
    Set<Bitmap.Config> configs = new HashSet<>();
    configs.addAll(Arrays.asList(Bitmap.Config.values()));
    if (Build.VERSION.SDK_INT >= 19) {
      configs.add(null);
    }
    return Collections.unmodifiableSet(configs);
  }
  
  private interface BitmapTracker {
    void add(Bitmap bitmap);
    void remove(Bitmap bitmap);
  }
  
  @SuppressWarnings(value = {"unused", }) private static class ThrowingBitmapTracker implements BitmapTracker {
    private final Set<Bitmap> bitmaps = Collections.synchronizedSet(new HashSet<Bitmap>());
    @Override public void add(Bitmap bitmap) {
      if (bitmaps.contains(bitmap)) {
        throw new IllegalStateException("Can\'t add already added bitmap: " + bitmap + " [" + bitmap.getWidth() + "x" + bitmap.getHeight() + "]");
      }
      bitmaps.add(bitmap);
    }
    @Override public void remove(Bitmap bitmap) {
      if (!bitmaps.contains(bitmap)) {
        throw new IllegalStateException("Cannot remove bitmap not in tracker");
      }
      bitmaps.remove(bitmap);
    }
  }
  
  private static class NullBitmapTracker implements BitmapTracker {
    @Override public void add(Bitmap bitmap) {
    }
    @Override public void remove(Bitmap bitmap) {
    }
  }
}

