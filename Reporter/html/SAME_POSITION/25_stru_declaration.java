  public Set<SupportRequestManagerFragment> getDescendantRequestManagerFragments() {
    if (rootRequestManagerFragment == null) {
      return Collections.emptySet();
    }
    else 
      if (rootRequestManagerFragment == this) {
        return Collections.unmodifiableSet(childRequestManagerFragments);
      }
      else {
        HashSet<SupportRequestManagerFragment> descendants = 
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\glide\revisions\rev_e161ca9_7e0f873\rev_left_e161ca9\library\src\main\java\com\bumptech\glide\manager\SupportRequestManagerFragment.java
new HashSet<SupportRequestManagerFragment>()
=======
new HashSet<>()
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\glide\revisions\rev_e161ca9_7e0f873\rev_right_7e0f873\library\src\main\java\com\bumptech\glide\manager\SupportRequestManagerFragment.java
;
        for (SupportRequestManagerFragment fragment : rootRequestManagerFragment.getDescendantRequestManagerFragments()) {
          if (isDescendant(fragment.getParentFragment())) {
            descendants.add(fragment);
          }
        }
        return Collections.unmodifiableSet(descendants);
      }
  }


