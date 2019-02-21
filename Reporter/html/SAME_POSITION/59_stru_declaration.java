  public void attachImageCrossFaded(ImageView view, String imgUrl, View placeholderView) {
    placeholderView.setVisibility(VISIBLE);
    view.setVisibility(INVISIBLE);
    map.put(view, new Pair<String, View>(imgUrl, placeholderView));
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\droidparts\revisions\rev_55ce489_25554aa\rev_left_55ce489\extra\src\org\droidparts\util\io\ImageAttacher.java
addAndExecute(view, new Pair<String, View>(imgUrl, placeholderView));
=======
exec.execute(fetchAndAttachRunnable);
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\droidparts\revisions\rev_55ce489_25554aa\rev_right_25554aa\extra\src\org\droidparts\util\io\ImageAttacher.java

  }


