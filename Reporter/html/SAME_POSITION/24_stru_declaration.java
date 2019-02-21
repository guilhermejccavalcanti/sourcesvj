    @Override public Set<RequestManager> getDescendants() {
      Set<RequestManagerFragment> descendantFragments = getDescendantRequestManagerFragments();
      HashSet<RequestManager> descendants = 
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\glide\revisions\rev_e161ca9_7e0f873\rev_left_e161ca9\library\src\main\java\com\bumptech\glide\manager\RequestManagerFragment.java
new HashSet<RequestManager>(descendantFragments.size())
=======
new HashSet<>(descendantFragments.size())
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\glide\revisions\rev_e161ca9_7e0f873\rev_right_7e0f873\library\src\main\java\com\bumptech\glide\manager\RequestManagerFragment.java
;
      for (RequestManagerFragment fragment : descendantFragments) {
        if (fragment.getRequestManager() != null) {
          descendants.add(fragment.getRequestManager());
        }
      }
      return descendants;
    }


