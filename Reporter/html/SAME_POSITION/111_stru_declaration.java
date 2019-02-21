  public boolean exists(StoreKey key, FileSpan fileSpan) throws StoreException {
    final Timer.Context context = metrics.findTime.time();
    System.out.println("Searching for " + key + " in index with filespan ranging from " + fileSpan.getStartOffset() + " to " + fileSpan.getEndOffset());
    logger.trace("Searching for " + key + " in index with filespan ranging from " + fileSpan.getStartOffset() + " to " + fileSpan.getEndOffset());
    try {
      Map.Entry<Long, IndexSegment> startEntry = indexes.floorEntry(fileSpan.getStartOffset());
      Map.Entry<Long, IndexSegment> endEntry = indexes.floorEntry(fileSpan.getEndOffset());
      ConcurrentNavigableMap<Long, IndexSegment> interestedSegmentsMap = 
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\ambry\revisions\rev_8f7be2e_aae4793\rev_left_8f7be2e\ambry-store\src\main\java\com.github.ambry.store\PersistentIndex.java
indexes.subMap(startEntry.getKey(), true, endEntry.getKey(), true)
=======
new ConcurrentSkipListMap<Long, IndexSegment>()
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\ambry\revisions\rev_8f7be2e_aae4793\rev_right_aae4793\ambry-store\src\main\java\com.github.ambry.store\PersistentIndex.java
;
      boolean foundValue = false;
      for (Map.Entry<Long, IndexSegment> entry : interestedSegmentsMap.entrySet()) {
        logger.trace("Index : {} searching index with start offset {}", dataDir, entry.getKey());
        IndexValue value = entry.getValue().find(key);
        if (value != null) {
          logger.trace("Index : {} found value offset {} size {} ttl {}", dataDir, value.getOffset(), value.getSize(), value.getTimeToLiveInMs());
          foundValue = true;
          break ;
        }
      }
      return foundValue;
    }
  }


