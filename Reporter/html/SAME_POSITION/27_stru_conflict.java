<<<<<<< MINE
diskCacheStrategy(DiskCacheStrategy.SOURCE).crossFade(R.anim.fade_in, 150).override(Api.SQUARE_THUMB_SIZE, Api.SQUARE_THUMB_SIZE)
=======
transition(withCrossFade(R.anim.fade_in, 150)).apply(diskCacheStrategyOf(DiskCacheStrategy.DATA).centerCrop(getActivity()).override(Api.SQUARE_THUMB_SIZE, Api.SQUARE_THUMB_SIZE))
>>>>>>> YOURS

