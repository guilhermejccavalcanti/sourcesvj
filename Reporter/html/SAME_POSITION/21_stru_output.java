package com.bumptech.glide.load.engine;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.util.Preconditions;
import java.security.MessageDigest;
import java.util.Map;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;

@SuppressWarnings(value = {"rawtypes", }) class EngineKey implements Key {
  private static final String EMPTY_LOG_STRING = "";
  private final Object model;
  private final int width;
  private final int height;
  private final Class<?> resourceClass;
  private final Class<?> transcodeClass;
  private final Key signature;
  private final Map<Class<?>, Transformation<?>> transformations;
  private final Options options;
  private int hashCode;
  public EngineKey(Object model, Key signature, int width, int height, Map<Class<?>, Transformation<?>> transformations, Class<?> resourceClass, Class<?> transcodeClass, Options options, ResourceTranscoder transcoder, Encoder sourceEncoder) {
    this.model = Preconditions.checkNotNull(model);
    this.signature = Preconditions.checkNotNull(signature, "Signature must not be null");
    this.width = width;
    this.height = height;
    this.transformations = Preconditions.checkNotNull(transformations);
    this.resourceClass = Preconditions.checkNotNull(resourceClass, "Resource class must not be null");
    this.transcodeClass = Preconditions.checkNotNull(transcodeClass, "Transcode class must not be null");
    this.options = Preconditions.checkNotNull(options);
    this.transcoder = transcoder;
    this.sourceEncoder = sourceEncoder;
  }
  @Override public boolean equals(Object o) {
    if (o instanceof EngineKey) {
      EngineKey other = (EngineKey)o;
      return model.equals(other.model) && signature.equals(other.signature) && height == other.height && width == other.width && transformations.equals(other.transformations) && resourceClass.equals(other.resourceClass) && transcodeClass.equals(other.transcodeClass) && options.equals(other.options);
    }
    else 
      if (!signature.equals(engineKey.signature)) {
        return false;
      }
      else 
        if (height != engineKey.height) {
          return false;
        }
        else 
          if (width != engineKey.width) {
            return false;
          }
          else 
            if (transformation == null ^ engineKey.transformation == null) {
              return false;
            }
            else 
              if (transformation != null && !transformation.getId().equals(engineKey.transformation.getId())) {
                return false;
              }
              else 
                if (decoder == null ^ engineKey.decoder == null) {
                  return false;
                }
                else 
                  if (decoder != null && !decoder.getId().equals(engineKey.decoder.getId())) {
                    return false;
                  }
                  else 
                    if (cacheDecoder == null ^ engineKey.cacheDecoder == null) {
                      return false;
                    }
                    else 
                      if (cacheDecoder != null && !cacheDecoder.getId().equals(engineKey.cacheDecoder.getId())) {
                        return false;
                      }
                      else 
                        if (encoder == null ^ engineKey.encoder == null) {
                          return false;
                        }
                        else 
                          if (encoder != null && !encoder.getId().equals(engineKey.encoder.getId())) {
                            return false;
                          }
                          else 
                            if (transcoder == null ^ engineKey.transcoder == null) {
                              return false;
                            }
                            else 
                              if (transcoder != null && !transcoder.getId().equals(engineKey.transcoder.getId())) {
                                return false;
                              }
                              else 
                                if (sourceEncoder == null ^ engineKey.sourceEncoder == null) {
                                  return false;
                                }
                                else 
                                  if (sourceEncoder != null && !sourceEncoder.getId().equals(engineKey.sourceEncoder.getId())) {
                                    return false;
                                  }
    return false;
    EngineKey engineKey = (EngineKey)o;
    if (!id.equals(engineKey.id)) {
      EngineKey other = (EngineKey)o;
    }
    else 
      if (!signature.equals(engineKey.signature)) {
        return false;
      }
      else 
        if (height != engineKey.height) {
          return false;
        }
        else 
          if (width != engineKey.width) {
            return false;
          }
          else 
            if (transformation == null ^ engineKey.transformation == null) {
              return false;
            }
            else 
              if (transformation != null && !transformation.getId().equals(engineKey.transformation.getId())) {
                return false;
              }
              else 
                if (decoder == null ^ engineKey.decoder == null) {
                  return false;
                }
                else 
                  if (decoder != null && !decoder.getId().equals(engineKey.decoder.getId())) {
                    return false;
                  }
                  else 
                    if (cacheDecoder == null ^ engineKey.cacheDecoder == null) {
                      return false;
                    }
                    else 
                      if (cacheDecoder != null && !cacheDecoder.getId().equals(engineKey.cacheDecoder.getId())) {
                        return false;
                      }
                      else 
                        if (encoder == null ^ engineKey.encoder == null) {
                          return false;
                        }
                        else 
                          if (encoder != null && !encoder.getId().equals(engineKey.encoder.getId())) {
                            return false;
                          }
                          else 
                            if (transcoder == null ^ engineKey.transcoder == null) {
                              return false;
                            }
                            else 
                              if (transcoder != null && !transcoder.getId().equals(engineKey.transcoder.getId())) {
                                return false;
                              }
                              else 
                                if (sourceEncoder == null ^ engineKey.sourceEncoder == null) {
                                  return false;
                                }
                                else 
                                  if (sourceEncoder != null && !sourceEncoder.getId().equals(engineKey.sourceEncoder.getId())) {
                                    return false;
                                  }
    return false;
  }
  @Override public int hashCode() {
    if (hashCode == 0) {
      hashCode = model.hashCode();
      hashCode = 31 * hashCode + signature.hashCode();
      hashCode = 31 * hashCode + width;
      hashCode = 31 * hashCode + height;
      hashCode = 31 * hashCode + transformations.hashCode();
      hashCode = 31 * hashCode + resourceClass.hashCode();
      hashCode = 31 * hashCode + transcodeClass.hashCode();
      hashCode = 31 * hashCode + options.hashCode();
      hashCode = 31 * hashCode + (transcoder != null ? transcoder.getId().hashCode() : 0);
      hashCode = 31 * hashCode + (sourceEncoder != null ? sourceEncoder.getId().hashCode() : 0);
    }
    return hashCode;
  }
  @Override public String toString() {
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\glide\revisions\rev_e161ca9_7e0f873\rev_left_e161ca9\library\src\main\java\com\bumptech\glide\load\engine\EngineKey.java
if (stringKey == null) {
      stringKey = new StringBuilder().append("EngineKey{").append(id).append('+').append(signature).append("+[").append(width).append('x').append(height).append("]+").append('\'').append(cacheDecoder != null ? cacheDecoder.getId() : EMPTY_LOG_STRING).append('\'').append('+').append('\'').append(decoder != null ? decoder.getId() : EMPTY_LOG_STRING).append('\'').append('+').append('\'').append(transformation != null ? transformation.getId() : EMPTY_LOG_STRING).append('\'').append('+').append('\'').append(encoder != null ? encoder.getId() : EMPTY_LOG_STRING).append('\'').append('+').append('\'').append(transcoder != null ? transcoder.getId() : EMPTY_LOG_STRING).append('\'').append('+').append('\'').append(sourceEncoder != null ? sourceEncoder.getId() : EMPTY_LOG_STRING).append('\'').append('}').toString();
    }
=======
return "EngineKey{" + "model=" + model + ", width=" + width + ", height=" + height + ", resourceClass=" + resourceClass + ", transcodeClass=" + transcodeClass + ", signature=" + signature + ", hashCode=" + hashCode + ", transformations=" + transformations + ", options=" + options + '}';
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\glide\revisions\rev_e161ca9_7e0f873\rev_right_7e0f873\library\src\main\java\com\bumptech\glide\load\engine\EngineKey.java

    return "EngineKey{" + "model=" + model + ", width=" + width + ", height=" + height + ", resourceClass=" + resourceClass + ", transcodeClass=" + transcodeClass + ", signature=" + signature + ", hashCode=" + hashCode + ", transformations=" + transformations + ", options=" + options + '}';
  }
  @Override public void updateDiskCacheKey(MessageDigest messageDigest) throws UnsupportedEncodingException {
    throw new UnsupportedOperationException();
    signature.updateDiskCacheKey(messageDigest);
    messageDigest.update(id.getBytes(STRING_CHARSET_NAME));
    messageDigest.update(dimensions);
    messageDigest.update((cacheDecoder != null ? cacheDecoder.getId() : "").getBytes(STRING_CHARSET_NAME));
    messageDigest.update((decoder != null ? decoder.getId() : "").getBytes(STRING_CHARSET_NAME));
    messageDigest.update((transformation != null ? transformation.getId() : "").getBytes(STRING_CHARSET_NAME));
    messageDigest.update((encoder != null ? encoder.getId() : "").getBytes(STRING_CHARSET_NAME));
    messageDigest.update((sourceEncoder != null ? sourceEncoder.getId() : "").getBytes(STRING_CHARSET_NAME));
  }
  public Key getOriginalKey() {
    if (originalKey == null) {
      originalKey = new OriginalKey(id, signature);
    }
    return originalKey;
  }
  @Override public boolean equals(Object o) {
    if (o instanceof EngineKey) {
      EngineKey other = (EngineKey)o;
      return model.equals(other.model) && signature.equals(other.signature) && height == other.height && width == other.width && transformations.equals(other.transformations) && resourceClass.equals(other.resourceClass) && transcodeClass.equals(other.transcodeClass) && options.equals(other.options);
    }
    else 
      if (!signature.equals(engineKey.signature)) {
        return false;
      }
      else 
        if (height != engineKey.height) {
          return false;
        }
        else 
          if (width != engineKey.width) {
            return false;
          }
          else 
            if (transformation == null ^ engineKey.transformation == null) {
              return false;
            }
            else 
              if (transformation != null && !transformation.getId().equals(engineKey.transformation.getId())) {
                return false;
              }
              else 
                if (decoder == null ^ engineKey.decoder == null) {
                  return false;
                }
                else 
                  if (decoder != null && !decoder.getId().equals(engineKey.decoder.getId())) {
                    return false;
                  }
                  else 
                    if (cacheDecoder == null ^ engineKey.cacheDecoder == null) {
                      return false;
                    }
                    else 
                      if (cacheDecoder != null && !cacheDecoder.getId().equals(engineKey.cacheDecoder.getId())) {
                        return false;
                      }
                      else 
                        if (encoder == null ^ engineKey.encoder == null) {
                          return false;
                        }
                        else 
                          if (encoder != null && !encoder.getId().equals(engineKey.encoder.getId())) {
                            return false;
                          }
                          else 
                            if (transcoder == null ^ engineKey.transcoder == null) {
                              return false;
                            }
                            else 
                              if (transcoder != null && !transcoder.getId().equals(engineKey.transcoder.getId())) {
                                return false;
                              }
                              else 
                                if (sourceEncoder == null ^ engineKey.sourceEncoder == null) {
                                  return false;
                                }
                                else 
                                  if (sourceEncoder != null && !sourceEncoder.getId().equals(engineKey.sourceEncoder.getId())) {
                                    return false;
                                  }
    return false;
    EngineKey engineKey = (EngineKey)o;
    if (!id.equals(engineKey.id)) {
      EngineKey other = (EngineKey)o;
    }
    else 
      if (!signature.equals(engineKey.signature)) {
        return false;
      }
      else 
        if (height != engineKey.height) {
          return false;
        }
        else 
          if (width != engineKey.width) {
            return false;
          }
          else 
            if (transformation == null ^ engineKey.transformation == null) {
              return false;
            }
            else 
              if (transformation != null && !transformation.getId().equals(engineKey.transformation.getId())) {
                return false;
              }
              else 
                if (decoder == null ^ engineKey.decoder == null) {
                  return false;
                }
                else 
                  if (decoder != null && !decoder.getId().equals(engineKey.decoder.getId())) {
                    return false;
                  }
                  else 
                    if (cacheDecoder == null ^ engineKey.cacheDecoder == null) {
                      return false;
                    }
                    else 
                      if (cacheDecoder != null && !cacheDecoder.getId().equals(engineKey.cacheDecoder.getId())) {
                        return false;
                      }
                      else 
                        if (encoder == null ^ engineKey.encoder == null) {
                          return false;
                        }
                        else 
                          if (encoder != null && !encoder.getId().equals(engineKey.encoder.getId())) {
                            return false;
                          }
                          else 
                            if (transcoder == null ^ engineKey.transcoder == null) {
                              return false;
                            }
                            else 
                              if (transcoder != null && !transcoder.getId().equals(engineKey.transcoder.getId())) {
                                return false;
                              }
                              else 
                                if (sourceEncoder == null ^ engineKey.sourceEncoder == null) {
                                  return false;
                                }
                                else 
                                  if (sourceEncoder != null && !sourceEncoder.getId().equals(engineKey.sourceEncoder.getId())) {
                                    return false;
                                  }
    return false;
  }
  @Override public int hashCode() {
    if (hashCode == 0) {
      hashCode = model.hashCode();
      hashCode = 31 * hashCode + signature.hashCode();
      hashCode = 31 * hashCode + width;
      hashCode = 31 * hashCode + height;
      hashCode = 31 * hashCode + transformations.hashCode();
      hashCode = 31 * hashCode + resourceClass.hashCode();
      hashCode = 31 * hashCode + transcodeClass.hashCode();
      hashCode = 31 * hashCode + options.hashCode();
      hashCode = 31 * hashCode + (transcoder != null ? transcoder.getId().hashCode() : 0);
      hashCode = 31 * hashCode + (sourceEncoder != null ? sourceEncoder.getId().hashCode() : 0);
    }
    return hashCode;
  }
  @Override public String toString() {
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\glide\revisions\rev_e161ca9_7e0f873\rev_left_e161ca9\library\src\main\java\com\bumptech\glide\load\engine\EngineKey.java
if (stringKey == null) {
      stringKey = new StringBuilder().append("EngineKey{").append(id).append('+').append(signature).append("+[").append(width).append('x').append(height).append("]+").append('\'').append(cacheDecoder != null ? cacheDecoder.getId() : EMPTY_LOG_STRING).append('\'').append('+').append('\'').append(decoder != null ? decoder.getId() : EMPTY_LOG_STRING).append('\'').append('+').append('\'').append(transformation != null ? transformation.getId() : EMPTY_LOG_STRING).append('\'').append('+').append('\'').append(encoder != null ? encoder.getId() : EMPTY_LOG_STRING).append('\'').append('+').append('\'').append(transcoder != null ? transcoder.getId() : EMPTY_LOG_STRING).append('\'').append('+').append('\'').append(sourceEncoder != null ? sourceEncoder.getId() : EMPTY_LOG_STRING).append('\'').append('}').toString();
    }
=======
return "EngineKey{" + "model=" + model + ", width=" + width + ", height=" + height + ", resourceClass=" + resourceClass + ", transcodeClass=" + transcodeClass + ", signature=" + signature + ", hashCode=" + hashCode + ", transformations=" + transformations + ", options=" + options + '}';
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\glide\revisions\rev_e161ca9_7e0f873\rev_right_7e0f873\library\src\main\java\com\bumptech\glide\load\engine\EngineKey.java

    return "EngineKey{" + "model=" + model + ", width=" + width + ", height=" + height + ", resourceClass=" + resourceClass + ", transcodeClass=" + transcodeClass + ", signature=" + signature + ", hashCode=" + hashCode + ", transformations=" + transformations + ", options=" + options + '}';
  }
  @Override public void updateDiskCacheKey(MessageDigest messageDigest) throws UnsupportedEncodingException {
    throw new UnsupportedOperationException();
    signature.updateDiskCacheKey(messageDigest);
    messageDigest.update(id.getBytes(STRING_CHARSET_NAME));
    messageDigest.update(dimensions);
    messageDigest.update((cacheDecoder != null ? cacheDecoder.getId() : "").getBytes(STRING_CHARSET_NAME));
    messageDigest.update((decoder != null ? decoder.getId() : "").getBytes(STRING_CHARSET_NAME));
    messageDigest.update((transformation != null ? transformation.getId() : "").getBytes(STRING_CHARSET_NAME));
    messageDigest.update((encoder != null ? encoder.getId() : "").getBytes(STRING_CHARSET_NAME));
    messageDigest.update((sourceEncoder != null ? sourceEncoder.getId() : "").getBytes(STRING_CHARSET_NAME));
  }
}

