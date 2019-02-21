  @TargetApi(value = Build.VERSION_CODES.JELLY_BEAN_MR1) public Set<RequestManagerFragment> getDescendantRequestManagerFragments() {
    if (rootRequestManagerFragment == this) {
      return Collections.unmodifiableSet(childRequestManagerFragments);
    }
    else 
      if (rootRequestManagerFragment == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
        return Collections.emptySet();
      }
      else {
        HashSet<RequestManagerFragment> descendants = 
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\glide\revisions\rev_e161ca9_7e0f873\rev_left_e161ca9\library\src\main\java\com\bumptech\glide\manager\RequestManagerFragment.java
new HashSet<RequestManagerFragment>()
=======
new HashSet<>()
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\glide\revisions\rev_e161ca9_7e0f873\rev_right_7e0f873\library\src\main\java\com\bumptech\glide\manager\RequestManagerFragment.java
;
        for (RequestManagerFragment fragment : rootRequestManagerFragment.getDescendantRequestManagerFragments()) {
          if (isDescendant(fragment.getParentFragment())) {
            descendants.add(fragment);
          }
        }
        return Collections.unmodifiableSet(descendants);
      }
  }


