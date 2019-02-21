<<<<<<< MINE
if (!appliedExtensions.isEmpty()) {
      final Set<String> methodsToRemove = Sets.newHashSet();
      for (int i = appliedExtensions.size() - 1; i >= 0; i--) {
        AutoValueExtension extension = appliedExtensions.get(i);
        methodsToRemove.addAll(extension.consumeProperties(context));
      }
      if (!methodsToRemove.isEmpty()) {
        context.setProperties(newImmutableBiMapRemovingKeys(properties, methodsToRemove));
        Set<ExecutableElement> newMethods = Sets.newLinkedHashSet(methods);
        for (java.util.Iterator<javax.lang.model.element.ExecutableElement> it = newMethods.iterator(); it.hasNext(); ) {
          if (methodsToRemove.contains(it.next().getSimpleName().toString())) {
            it.remove();
          }
        }
        methods = ImmutableSet.copyOf(newMethods);
      }
    }
=======
if (appliedExtensions.size() > 0) {
      final Set<String> methodsToRemove = Sets.newHashSet();
      for (int i = appliedExtensions.size() - 1; i >= 0; i--) {
        AutoValueExtension extension = appliedExtensions.get(i);
        methodsToRemove.addAll(extension.consumeProperties(context));
      }
      if (methodsToRemove.size() > 0) {
        context.setProperties(newImmutableBiMapRemovingKeys(properties, methodsToRemove));
        methods = newFilteredImmutableSet(methods, new Predicate<ExecutableElement>() {
            @Override public boolean apply(ExecutableElement executableElement) {
              return !methodsToRemove.contains(executableElement.getSimpleName().toString());
            }
        });
      }
    }
>>>>>>> YOURS

