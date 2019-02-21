package com.bumptech.glide.manager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v4.app.Fragment;
import com.bumptech.glide.RequestManager;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SupportRequestManagerFragment extends Fragment {
  private RequestManager requestManager;
  private final ActivityFragmentLifecycle lifecycle;
  private final RequestManagerTreeNode requestManagerTreeNode = new SupportFragmentRequestManagerTreeNode();
  private final HashSet<SupportRequestManagerFragment> childRequestManagerFragments = 
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\glide\revisions\rev_e161ca9_7e0f873\rev_left_e161ca9\library\src\main\java\com\bumptech\glide\manager\SupportRequestManagerFragment.java
new HashSet<SupportRequestManagerFragment>()
=======
new HashSet<>()
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\glide\revisions\rev_e161ca9_7e0f873\rev_right_7e0f873\library\src\main\java\com\bumptech\glide\manager\SupportRequestManagerFragment.java
;
  private SupportRequestManagerFragment rootRequestManagerFragment;
  public SupportRequestManagerFragment() {
    this(new ActivityFragmentLifecycle());
  }
  @SuppressLint(value = "ValidFragment") public SupportRequestManagerFragment(ActivityFragmentLifecycle lifecycle) {
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
  private void addChildRequestManagerFragment(SupportRequestManagerFragment child) {
    childRequestManagerFragments.add(child);
  }
  private void removeChildRequestManagerFragment(SupportRequestManagerFragment child) {
    childRequestManagerFragments.remove(child);
  }
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
  private boolean isDescendant(Fragment fragment) {
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
    rootRequestManagerFragment = RequestManagerRetriever.get().getSupportRequestManagerFragment(getActivity().getSupportFragmentManager());
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
  @Override public void onLowMemory() {
    super.onLowMemory();
    if (requestManager != null) {
      requestManager.onLowMemory();
    }
  }
  
  private class SupportFragmentRequestManagerTreeNode implements RequestManagerTreeNode {
    @Override public Set<RequestManager> getDescendants() {
      Set<SupportRequestManagerFragment> descendantFragments = getDescendantRequestManagerFragments();
      HashSet<RequestManager> descendants = 
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\glide\revisions\rev_e161ca9_7e0f873\rev_left_e161ca9\library\src\main\java\com\bumptech\glide\manager\SupportRequestManagerFragment.java
new HashSet<RequestManager>(descendantFragments.size())
=======
new HashSet<>(descendantFragments.size())
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\glide\revisions\rev_e161ca9_7e0f873\rev_right_7e0f873\library\src\main\java\com\bumptech\glide\manager\SupportRequestManagerFragment.java
;
      for (SupportRequestManagerFragment fragment : descendantFragments) {
        if (fragment.getRequestManager() != null) {
          descendants.add(fragment.getRequestManager());
        }
      }
      return descendants;
    }
  }
}

