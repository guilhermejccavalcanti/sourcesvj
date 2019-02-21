  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    Bundle args = getArguments();
    photoSize = args.getInt(IMAGE_SIZE_KEY);
    thumbnail = args.getBoolean(THUMBNAIL_KEY);
    fullRequest = Glide.with(this).asDrawable().transition(withCrossFade(R.anim.fade_in, 150)).apply(centerCropTransform(getActivity()));
    thumbnailRequest = Glide.with(this).asDrawable().
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\glide\revisions\rev_e161ca9_7e0f873\rev_left_e161ca9\samples\flickr\src\main\java\com\bumptech\glide\samples\flickr\FlickrPhotoGrid.java
diskCacheStrategy(DiskCacheStrategy.SOURCE).crossFade(R.anim.fade_in, 150).override(Api.SQUARE_THUMB_SIZE, Api.SQUARE_THUMB_SIZE)
=======
transition(withCrossFade(R.anim.fade_in, 150)).apply(diskCacheStrategyOf(DiskCacheStrategy.DATA).centerCrop(getActivity()).override(Api.SQUARE_THUMB_SIZE, Api.SQUARE_THUMB_SIZE))
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\glide\revisions\rev_e161ca9_7e0f873\rev_right_7e0f873\samples\flickr\src\main\java\com\bumptech\glide\samples\flickr\FlickrPhotoGrid.java
;
    preloadRequest = thumbnail ? thumbnailRequest.clone().priority(Priority.HIGH) : fullRequest;
    final View result = inflater.inflate(R.layout.flickr_photo_grid, container, false);
    grid = (GridView)result.findViewById(R.id.images);
    grid.setColumnWidth(photoSize);
    adapter = new PhotoAdapter();
    grid.setAdapter(adapter);
    final FixedPreloadSizeProvider<Photo> preloadSizeProvider = new FixedPreloadSizeProvider<Photo>(photoSize, photoSize);
    final ListPreloader<Photo> preloader = new ListPreloader<Photo>(adapter, preloadSizeProvider, args.getInt(PRELOAD_KEY));
    grid.setOnScrollListener(preloader);
    if (currentPhotos != null) {
      adapter.setPhotos(currentPhotos);
    }
    if (savedInstanceState != null) {
      int index = savedInstanceState.getInt(STATE_POSITION_INDEX);
      grid.setSelection(index);
    }
    return result;
  }


