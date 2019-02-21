<<<<<<< MINE
if (stringKey == null) {
      stringKey = new StringBuilder().append("EngineKey{").append(id).append('+').append(signature).append("+[").append(width).append('x').append(height).append("]+").append('\'').append(cacheDecoder != null ? cacheDecoder.getId() : EMPTY_LOG_STRING).append('\'').append('+').append('\'').append(decoder != null ? decoder.getId() : EMPTY_LOG_STRING).append('\'').append('+').append('\'').append(transformation != null ? transformation.getId() : EMPTY_LOG_STRING).append('\'').append('+').append('\'').append(encoder != null ? encoder.getId() : EMPTY_LOG_STRING).append('\'').append('+').append('\'').append(transcoder != null ? transcoder.getId() : EMPTY_LOG_STRING).append('\'').append('+').append('\'').append(sourceEncoder != null ? sourceEncoder.getId() : EMPTY_LOG_STRING).append('\'').append('}').toString();
    }
=======
return "EngineKey{" + "model=" + model + ", width=" + width + ", height=" + height + ", resourceClass=" + resourceClass + ", transcodeClass=" + transcodeClass + ", signature=" + signature + ", hashCode=" + hashCode + ", transformations=" + transformations + ", options=" + options + '}';
>>>>>>> YOURS

