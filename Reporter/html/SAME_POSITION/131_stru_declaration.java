  private void processType(TypeElement type) {
    AutoValue autoValue = type.getAnnotation(AutoValue.class);
    if (autoValue == null) {
      errorReporter.abortWithError("annotation processor for @AutoValue was invoked with a type" + " that does not have that annotation; this is probably a compiler bug", type);
    }
    if (type.getKind() != ElementKind.CLASS) {
      errorReporter.abortWithError("@" + AutoValue.class.getName() + " only applies to classes", type);
    }
    if (ancestorIsAutoValue(type)) {
      errorReporter.abortWithError("One @AutoValue class may not extend another", type);
    }
    if (implementsAnnotation(type)) {
      errorReporter.abortWithError("@AutoValue may not be used to implement an annotation" + " interface; try using @AutoAnnotation instead", type);
    }
    checkTopLevelOrStatic(type);
    ImmutableSet<ExecutableElement> methods = getLocalAndInheritedMethods(type, processingEnv.getElementUtils());
    ImmutableSet<ExecutableElement> methodsToImplement = methodsToImplement(type, methods);
    ImmutableBiMap<String, ExecutableElement> properties = propertyNameToMethodMap(methodsToImplement);
    String fqExtClass = TypeSimplifier.classNameOf(type);
    List<AutoValueExtension> appliedExtensions = new ArrayList<AutoValueExtension>();
    ExtensionContext context = new ExtensionContext(processingEnv, type, properties);
    for (AutoValueExtension extension : extensions) {
      if (extension.applicable(context)) {
        if (extension.mustBeFinal(context)) {
          appliedExtensions.add(0, extension);
        }
        else {
          appliedExtensions.add(extension);
        }
      }
    }
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\auto\revisions\rev_658d11d_6520620\rev_left_658d11d\value\src\main\java\com\google\auto\value\processor\AutoValueProcessor.java
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
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\auto\revisions\rev_658d11d_6520620\rev_right_6520620\value\src\main\java\com\google\auto\value\processor\AutoValueProcessor.java

    String finalSubclass = generatedSubclassName(type, 0);
    String subclass = generatedSubclassName(type, appliedExtensions.size());
    AutoValueTemplateVars vars = new AutoValueTemplateVars();
    vars.pkg = TypeSimplifier.packageNameOf(type);
    vars.origClass = fqExtClass;
    vars.simpleClassName = TypeSimplifier.simpleNameOf(vars.origClass);
    vars.subclass = TypeSimplifier.simpleNameOf(subclass);
    vars.finalSubclass = TypeSimplifier.simpleNameOf(finalSubclass);
    vars.isFinal = appliedExtensions.isEmpty();
    vars.types = processingEnv.getTypeUtils();
    defineVarsForType(type, vars, methods);
    GwtCompatibility gwtCompatibility = new GwtCompatibility(type);
    vars.gwtCompatibleAnnotation = gwtCompatibility.gwtCompatibleAnnotationString();
    String text = vars.toText();
    text = Reformatter.fixup(text);
    writeSourceFile(subclass, text, type);
    GwtSerialization gwtSerialization = new GwtSerialization(gwtCompatibility, processingEnv, type);
    gwtSerialization.maybeWriteGwtSerializer(vars);
    String extClass = TypeSimplifier.simpleNameOf(subclass);
    for (int i = appliedExtensions.size() - 1; i >= 0; i--) {
      AutoValueExtension extension = appliedExtensions.remove(i);
      String fqClassName = generatedSubclassName(type, i);
      String className = TypeSimplifier.simpleNameOf(fqClassName);
      boolean isFinal = (i == 0);
      String source = extension.generateClass(context, className, extClass, isFinal);
      if (source == null || source.isEmpty()) {
        errorReporter.reportError("Extension returned no source code.", type);
        return ;
      }
      source = Reformatter.fixup(source);
      writeSourceFile(fqClassName, source, type);
      extClass = className;
    }
  }


