package com.bumptech.glide.manager;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.os.Build;
import com.bumptech.glide.RequestManager;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@TargetApi(value = Build.VERSION_CODES.HONEYCOMB) public class RequestManagerFragment extends Fragment {
  private final ActivityFragmentLifecycle lifecycle;
  private final RequestManagerTreeNode requestManagerTreeNode = new FragmentRequestManagerTreeNode();
  private RequestManager requestManager;
  private final HashSet<RequestManagerFragment> childRequestManagerFragments = 
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\glide\revisions\rev_e161ca9_7e0f873\rev_left_e161ca9\library\src\main\java\com\bumptech\glide\manager\RequestManagerFragment.java
new HashSet<RequestManagerFragment>()
=======
new HashSet<>()
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\glide\revisions\rev_e161ca9_7e0f873\rev_right_7e0f873\library\src\main\java\com\bumptech\glide\manager\RequestManagerFragment.java
;
  private RequestManagerFragment rootRequestManagerFragment;
  public RequestManagerFragment() {
    this(new ActivityFragmentLifecycle());
  }
  @SuppressLint(value = "ValidFragment") RequestManagerFragment(ActivityFragmentLifecycle lifecycle) {
    this.lifecycle = lifecycle;
  }
  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }
  ActivityFragmentLifecycle getLifecycle() {
    return lifecycle;
  }
  public RequestManager getRequestManager() {
    return requestManager;
  }
  public RequestManagerTreeNode getRequestManagerTreeNode() {
    return requestManagerTreeNode;
  }
  private void addChildRequestManagerFragment(RequestManagerFragment child) {
    childRequestManagerFragments.add(child);
  }
  private void removeChildRequestManagerFragment(RequestManagerFragment child) {
    childRequestManagerFragments.remove(child);
  }
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
  @TargetApi(value = Build.VERSION_CODES.JELLY_BEAN_MR1) private boolean isDescendant(Fragment fragment) {
    Fragment root = this.getParentFragment();
    while (fragment.getParentFragment() != null){
      if (fragment.getParentFragment() == root) {
        return true;
      }
      fragment = fragment.getParentFragment();
    }
    return false;
  }
  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);
    rootRequestManagerFragment = RequestManagerRetriever.get().getRequestManagerFragment(getActivity().getFragmentManager());
    if (rootRequestManagerFragment != this) {
      rootRequestManagerFragment.addChildRequestManagerFragment(this);
    }
  }
  @Override public void onDetach() {
    super.onDetach();
    if (rootRequestManagerFragment != null) {
      rootRequestManagerFragment.removeChildRequestManagerFragment(this);
      rootRequestManagerFragment = null;
    }
  }
  @Override public void onStart() {
    super.onStart();
    lifecycle.onStart();
  }
  @Override public void onStop() {
    super.onStop();
    lifecycle.onStop();
  }
  @Override public void onDestroy() {
    super.onDestroy();
    lifecycle.onDestroy();
  }
  @Override public void onTrimMemory(int level) {
    if (requestManager != null) {
      requestManager.onTrimMemory(level);
    }
  }
  @Override public void onLowMemory() {
    if (requestManager != null) {
      requestManager.onLowMemory();
    }
  }
  
  private class FragmentRequestManagerTreeNode implements RequestManagerTreeNode {
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
  }
}

