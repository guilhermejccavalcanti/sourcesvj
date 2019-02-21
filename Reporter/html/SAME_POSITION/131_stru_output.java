package com.google.auto.value.processor;
import static com.google.auto.common.MoreElements.getLocalAndInheritedMethods;
import com.google.auto.common.MoreElements;
import com.google.auto.service.AutoService;
import com.google.auto.value.AutoValue;
import com.google.auto.value.extension.AutoValueExtension;
import com.google.common.base.Functions;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableSet;
import java.beans.Introspector;
import com.google.common.collect.Maps;
import java.io.IOException;
import com.google.common.collect.Sets;
import java.io.Serializable;
import java.beans.Introspector;
import java.io.Writer;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.io.Serializable;
import java.util.ArrayList;
import java.io.Writer;
import java.util.Arrays;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import javax.annotation.Generated;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@AutoService(value = Processor.class) public class AutoValueProcessor extends AbstractProcessor {
  public AutoValueProcessor() {
    this(ServiceLoader.load(AutoValueExtension.class, AutoValueProcessor.class.getClassLoader()));
  }
  AutoValueProcessor(Iterable<? extends AutoValueExtension> extensions) {
    this.extensions = extensions;
  }
  @Override public Set<String> getSupportedAnnotationTypes() {
    return ImmutableSet.of(AutoValue.class.getName());
  }
  @Override public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }
  private ErrorReporter errorReporter;
  private final List<String> deferredTypeNames = new ArrayList<String>();
  private Iterable<? extends AutoValueExtension> extensions;
  @Override public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    errorReporter = new ErrorReporter(processingEnv);
  }
  @Override public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    List<TypeElement> deferredTypes = new ArrayList<TypeElement>();
    for (String deferred : deferredTypeNames) {
      deferredTypes.add(processingEnv.getElementUtils().getTypeElement(deferred));
    }
    if (roundEnv.processingOver()) {
      for (TypeElement type : deferredTypes) {
        errorReporter.reportError("Did not generate @AutoValue class for " + type.getQualifiedName() + " because it references undefined types", type);
      }
      return false;
    }
    Collection<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(AutoValue.class);
    List<TypeElement> types = new ImmutableList.Builder<TypeElement>().addAll(deferredTypes).addAll(ElementFilter.typesIn(annotatedElements)).build();
    deferredTypeNames.clear();
    for (TypeElement type : types) {
      try {
        processType(type);
      }
      catch (AbortProcessingException e) {
      }
      catch (MissingTypeException e) {
        deferredTypeNames.add(type.getQualifiedName().toString());
      }
      catch (RuntimeException e) {
        String trace = Throwables.getStackTraceAsString(e);
        errorReporter.reportError("@AutoValue processor threw an exception: " + trace, type);
      }
    }
    return false;
  }
  private String generatedClassName(TypeElement type, String prefix) {
    String name = type.getSimpleName().toString();
    while (type.getEnclosingElement() instanceof TypeElement){
      type = (TypeElement)type.getEnclosingElement();
      name = type.getSimpleName() + "_" + name;
    }
    String pkg = TypeSimplifier.packageNameOf(type);
    String dot = pkg.isEmpty() ? "" : ".";
    return pkg + dot + prefix + name;
  }
  private String generatedSubclassName(TypeElement type, int depth) {
    return generatedClassName(type, Strings.repeat("$", depth) + "AutoValue_");
  }
  
  public static class Property {
    private final String name;
    private final String identifier;
    private final ExecutableElement method;
    private final String type;
    private final ImmutableList<String> annotations;
    Property(String name, String identifier, ExecutableElement method, String type, TypeSimplifier typeSimplifier) {
      this.name = name;
      this.identifier = identifier;
      this.method = method;
      this.type = type;
      this.annotations = buildAnnotations(typeSimplifier);
    }
    private ImmutableList<String> buildAnnotations(TypeSimplifier typeSimplifier) {
      ImmutableList.Builder<String> builder = ImmutableList.builder();
      for (AnnotationMirror annotationMirror : method.getAnnotationMirrors()) {
        TypeElement annotationElement = (TypeElement)annotationMirror.getAnnotationType().asElement();
        if (annotationElement.getQualifiedName().toString().equals(Override.class.getName())) {
          continue ;
        }
        AnnotationOutput annotationOutput = new AnnotationOutput(typeSimplifier);
        builder.add(annotationOutput.sourceFormForAnnotation(annotationMirror));
      }
      return builder.build();
    }
    @Override public String toString() {
      return identifier;
    }
    public String getName() {
      return name;
    }
    public String getGetter() {
      return method.getSimpleName().toString();
    }
    TypeElement getOwner() {
      return (TypeElement)method.getEnclosingElement();
    }
    public TypeMirror getTypeMirror() {
      return method.getReturnType();
    }
    public String getType() {
      return type;
    }
    public TypeKind getKind() {
      return method.getReturnType().getKind();
    }
    public List<String> getAnnotations() {
      return annotations;
    }
    public String getNullableAnnotation() {
      for (String annotationString : annotations) {
        if (annotationString.equals("@Nullable") || annotationString.endsWith(".Nullable")) {
          return annotationString + " ";
        }
      }
      return "";
    }
    public boolean isNullable() {
      return !getNullableAnnotation().isEmpty();
    }
    public String getAccess() {
      Set<Modifier> mods = method.getModifiers();
      if (mods.contains(Modifier.PUBLIC)) {
        return "public ";
      }
      else 
        if (mods.contains(Modifier.PROTECTED)) {
          return "protected ";
        }
        else {
          return "";
        }
    }
    @Override public boolean equals(Object obj) {
      return obj instanceof Property && ((Property)obj).method.equals(method);
    }
    @Override public int hashCode() {
      return method.hashCode();
    }
  }
  private static boolean isJavaLangObject(TypeElement type) {
    return type.getSuperclass().getKind() == TypeKind.NONE && type.getKind() == ElementKind.CLASS;
  }
  private enum ObjectMethodToOverride {
    NONE(),

    TO_STRING(),

    EQUALS(),

    HASH_CODE(),

  ;
  }
  private static ObjectMethodToOverride objectMethodToOverride(ExecutableElement method) {
    String name = method.getSimpleName().toString();
    switch (method.getParameters().size()){
      case 0:
      if (name.equals("toString")) {
        return ObjectMethodToOverride.TO_STRING;
      }
      else 
        if (name.equals("hashCode")) {
          return ObjectMethodToOverride.HASH_CODE;
        }
      break ;
      case 1:
      if (name.equals("equals") && method.getParameters().get(0).asType().toString().equals("java.lang.Object")) {
        return ObjectMethodToOverride.EQUALS;
      }
      break ;
    }
    return ObjectMethodToOverride.NONE;
  }
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
  private static  <T extends java.lang.Object> ImmutableSet<T> newFilteredImmutableSet(ImmutableSet<T> original, Predicate<T> predicate) {
    ImmutableSet.Builder<T> builder = ImmutableSet.builder();
    for (T item : original) {
      if (predicate.apply(item)) 
        builder.add(item);
    }
    return builder.build();
  }
  private static  <K extends java.lang.Object, V extends java.lang.Object> ImmutableBiMap<K, V> newImmutableBiMapRemovingKeys(ImmutableBiMap<K, V> original, Set<K> keysToRemove) {
    ImmutableBiMap.Builder<K, V> builder = ImmutableBiMap.builder();
    for (Map.Entry<K, V> property : original.entrySet()) {
      if (!keysToRemove.contains(property.getKey())) {
        builder.put(property);
      }
    }
    return builder.build();
  }
  private void defineVarsForType(TypeElement type, AutoValueTemplateVars vars, Set<ExecutableElement> methods) {
    Types typeUtils = processingEnv.getTypeUtils();
    determineObjectMethodsToGenerate(methods, vars);
    ImmutableSet<ExecutableElement> methodsToImplement = methodsToImplement(type, methods);
    Set<TypeMirror> types = new TypeMirrorSet();
    types.addAll(returnTypesOf(methodsToImplement));
    TypeElement generatedTypeElement = processingEnv.getElementUtils().getTypeElement(Generated.class.getName());
    if (generatedTypeElement != null) {
      types.add(generatedTypeElement.asType());
    }
    TypeMirror javaUtilArrays = getTypeMirror(Arrays.class);
    if (containsArrayType(types)) {
      types.add(javaUtilArrays);
    }
    BuilderSpec builderSpec = new BuilderSpec(type, processingEnv, errorReporter);
    Optional<BuilderSpec.Builder> builder = builderSpec.getBuilder();
    ImmutableSet<ExecutableElement> toBuilderMethods;
    if (builder.isPresent()) {
      toBuilderMethods = builder.get().toBuilderMethods(typeUtils, methodsToImplement);
      types.addAll(builder.get().referencedTypes());
    }
    else {
      toBuilderMethods = ImmutableSet.of();
    }
    vars.toBuilderMethods = FluentIterable.from(toBuilderMethods).transform(SimpleNameFunction.INSTANCE).toList();
    Set<ExecutableElement> propertyMethods = Sets.difference(methodsToImplement, toBuilderMethods);
    types.addAll(allMethodAnnotationTypes(propertyMethods));
    String pkg = TypeSimplifier.packageNameOf(type);
    TypeSimplifier typeSimplifier = new TypeSimplifier(typeUtils, pkg, types, type.asType());
    vars.imports = typeSimplifier.typesToImport();
    vars.generated = generatedTypeElement == null ? "" : typeSimplifier.simplify(generatedTypeElement.asType());
    vars.arrays = typeSimplifier.simplify(javaUtilArrays);
    ImmutableBiMap<ExecutableElement, String> methodToPropertyName = propertyNameToMethodMap(propertyMethods).inverse();
    Map<ExecutableElement, String> methodToIdentifier = Maps.newLinkedHashMap(methodToPropertyName);
    fixReservedIdentifiers(methodToIdentifier);
    List<Property> props = new ArrayList<Property>();
    for (ExecutableElement method : propertyMethods) {
      String propertyType = typeSimplifier.simplify(method.getReturnType());
      String propertyName = methodToPropertyName.get(method);
      String identifier = methodToIdentifier.get(method);
      props.add(new Property(propertyName, identifier, method, propertyType, typeSimplifier));
    }
    eclipseHack().reorderProperties(props);
    vars.props = ImmutableSet.copyOf(props);
    vars.serialVersionUID = getSerialVersionUID(type);
    vars.formalTypes = typeSimplifier.formalTypeParametersString(type);
    vars.actualTypes = TypeSimplifier.actualTypeParametersString(type);
    vars.wildcardTypes = wildcardTypeParametersString(type);
    if (builder.isPresent()) {
      builder.get().defineVars(vars, typeSimplifier, methodToPropertyName);
    }
  }
  private ImmutableBiMap<String, ExecutableElement> propertyNameToMethodMap(Iterable<ExecutableElement> propertyMethods) {
    Map<String, ExecutableElement> map = Maps.newLinkedHashMap();
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\auto\revisions\rev_658d11d_6520620\rev_left_658d11d\value\src\main\java\com\google\auto\value\processor\AutoValueProcessor.java
for (ExecutableElement method : propertyMethods) {
      String methodName = method.getSimpleName().toString();
      String name = allGetters ? nameWithoutPrefix(methodName) : methodName;
      Object old = map.put(name, method);
      if (old != null) {
        errorReporter.reportError("More than one @AutoValue property called " + name, method);
      }
    }
=======
return methodToPropertyNameMap(propertyMethods).inverse();
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\auto\revisions\rev_658d11d_6520620\rev_right_6520620\value\src\main\java\com\google\auto\value\processor\AutoValueProcessor.java

    return methodToPropertyNameMap(propertyMethods).inverse();
    return methodToPropertyNameMap(propertyMethods).inverse();
  }
  private static boolean allGetters(Iterable<ExecutableElement> methods) {
    for (ExecutableElement method : methods) {
      String name = method.getSimpleName().toString();
      boolean get = name.startsWith("get") && !name.equals("get");
      boolean is = name.startsWith("is") && !name.equals("is") && method.getReturnType().getKind() == TypeKind.BOOLEAN;
      if (!get && !is) {
        return false;
      }
    }
    return true;
  }
  private Set<TypeMirror> allMethodAnnotationTypes(Iterable<ExecutableElement> methods) {
    Set<TypeMirror> annotationTypes = new TypeMirrorSet();
    for (ExecutableElement method : methods) {
      for (AnnotationMirror annotationMirror : method.getAnnotationMirrors()) {
        annotationTypes.add(annotationMirror.getAnnotationType());
      }
    }
    return annotationTypes;
  }
  private String nameWithoutPrefix(String name) {
    if (name.startsWith("get")) {
      name = name.substring(3);
    }
    else {
      assert name.startsWith("is");
      name = name.substring(2);
    }
    return Introspector.decapitalize(name);
  }
  private void checkTopLevelOrStatic(TypeElement type) {
    ElementKind enclosingKind = type.getEnclosingElement().getKind();
    if ((enclosingKind.isClass() || enclosingKind.isInterface()) && !type.getModifiers().contains(Modifier.STATIC)) {
      errorReporter.abortWithError("Nested @AutoValue class must be static", type);
    }
  }
  private void fixReservedIdentifiers(Map<ExecutableElement, String> methodToIdentifier) {
    for (Map.Entry<ExecutableElement, String> entry : methodToIdentifier.entrySet()) {
      if (SourceVersion.isKeyword(entry.getValue())) {
        entry.setValue(disambiguate(entry.getValue(), methodToIdentifier.values()));
      }
    }
  }
  private String disambiguate(String name, Collection<String> existingNames) {
    for (int i = 0; true; i++) {
      String candidate = name + i;
      if (!existingNames.contains(candidate)) {
        return candidate;
      }
    }
  }
  private Set<TypeMirror> returnTypesOf(Iterable<ExecutableElement> methods) {
    Set<TypeMirror> returnTypes = new TypeMirrorSet();
    for (ExecutableElement method : methods) {
      returnTypes.add(method.getReturnType());
    }
    return returnTypes;
  }
  private static boolean containsArrayType(Set<TypeMirror> types) {
    for (TypeMirror type : types) {
      if (type.getKind() == TypeKind.ARRAY) {
        return true;
      }
    }
    return false;
  }
  private static void determineObjectMethodsToGenerate(Set<ExecutableElement> methods, AutoValueTemplateVars vars) {
    vars.equals = false;
    vars.hashCode = false;
    vars.toString = false;
    for (ExecutableElement method : methods) {
      ObjectMethodToOverride override = objectMethodToOverride(method);
      boolean canGenerate = method.getModifiers().contains(Modifier.ABSTRACT) || isJavaLangObject((TypeElement)method.getEnclosingElement());
      switch (override){
        case EQUALS:
        vars.equals = canGenerate;
        break ;
        case HASH_CODE:
        vars.hashCode = canGenerate;
        break ;
        case TO_STRING:
        vars.toString = canGenerate;
        break ;
      }
    }
  }
  private ImmutableSet<ExecutableElement> methodsToImplement(TypeElement autoValueClass, Set<ExecutableElement> methods) {
    ImmutableSet.Builder<ExecutableElement> toImplement = ImmutableSet.builder();
    Set<Name> toImplementNames = Sets.newHashSet();
    boolean ok = true;
    for (ExecutableElement method : methods) {
      if (method.getModifiers().contains(Modifier.ABSTRACT) && objectMethodToOverride(method) == ObjectMethodToOverride.NONE) {
        if (method.getParameters().isEmpty() && method.getReturnType().getKind() != TypeKind.VOID) {
          ok &= checkReturnType(autoValueClass, method);
          if (toImplementNames.add(method.getSimpleName())) {
            toImplement.add(method);
          }
        }
        else {
          errorReporter.reportWarning("@AutoValue classes cannot have abstract methods other than" + " property getters and Builder converters", method);
        }
      }
    }
    if (!ok) {
      throw new AbortProcessingException();
    }
    return toImplement.build();
  }
  private boolean checkReturnType(TypeElement autoValueClass, ExecutableElement getter) {
    TypeMirror type = getter.getReturnType();
    if (type.getKind() == TypeKind.ARRAY) {
      TypeMirror componentType = ((ArrayType)type).getComponentType();
      if (componentType.getKind().isPrimitive()) {
        warnAboutPrimitiveArrays(autoValueClass, getter);
        return true;
      }
      else {
        errorReporter.reportError("An @AutoValue class cannot define an array-valued property" + " unless it is a primitive array", getter);
        return false;
      }
    }
    else {
      return true;
    }
  }
  private void warnAboutPrimitiveArrays(TypeElement autoValueClass, ExecutableElement getter) {
    SuppressWarnings suppressWarnings = getter.getAnnotation(SuppressWarnings.class);
    if (suppressWarnings == null || !Arrays.asList(suppressWarnings.value()).contains("mutable")) {
      String warning = "An @AutoValue property that is a primitive array returns the original array, " + "which can therefore be modified by the caller. If this OK, you can " + "suppress this warning with @SuppressWarnings(\"mutable\"). Otherwise, you " + "should replace the property with an immutable type, perhaps a simple wrapper " + "around the original array.";
      boolean sameClass = getter.getEnclosingElement().equals(autoValueClass);
      if (sameClass) {
        errorReporter.reportWarning(warning, getter);
      }
      else {
        errorReporter.reportWarning(warning + " Method: " + getter.getEnclosingElement() + "." + getter, autoValueClass);
      }
    }
  }
  private void writeSourceFile(String className, String text, TypeElement originatingType) {
    try {
      JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(className, originatingType);
      Writer writer = sourceFile.openWriter();
      try {
        writer.write(text);
      }
      finally {
        writer.close();
      }
    }
    catch (IOException e) {
      processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "Could not write generated class " + className + ": " + e);
    }
  }
  private boolean ancestorIsAutoValue(TypeElement type) {
    while (true){
      TypeMirror parentMirror = type.getSuperclass();
      if (parentMirror.getKind() == TypeKind.NONE) {
        return false;
      }
      Types typeUtils = processingEnv.getTypeUtils();
      TypeElement parentElement = (TypeElement)typeUtils.asElement(parentMirror);
      if (MoreElements.isAnnotationPresent(parentElement, AutoValue.class)) {
        return true;
      }
      type = parentElement;
    }
  }
  private boolean implementsAnnotation(TypeElement type) {
    Types typeUtils = processingEnv.getTypeUtils();
    return typeUtils.isAssignable(type.asType(), getTypeMirror(Annotation.class));
  }
  private String getSerialVersionUID(TypeElement type) {
    Types typeUtils = processingEnv.getTypeUtils();
    TypeMirror serializable = getTypeMirror(Serializable.class);
    if (typeUtils.isAssignable(type.asType(), serializable)) {
      List<VariableElement> fields = ElementFilter.fieldsIn(type.getEnclosedElements());
      for (VariableElement field : fields) {
        if (field.getSimpleName().toString().equals("serialVersionUID")) {
          Object value = field.getConstantValue();
          if (field.getModifiers().containsAll(Arrays.asList(Modifier.STATIC, Modifier.FINAL)) && field.asType().getKind() == TypeKind.LONG && value != null) {
            return value + "L";
          }
          else {
            errorReporter.reportError("serialVersionUID must be a static final long compile-time constant", field);
            break ;
          }
        }
      }
    }
    return "";
  }
  private TypeMirror getTypeMirror(Class<?> c) {
    return processingEnv.getElementUtils().getTypeElement(c.getName()).asType();
  }
  private static String wildcardTypeParametersString(TypeElement type) {
    List<? extends TypeParameterElement> typeParameters = type.getTypeParameters();
    if (typeParameters.isEmpty()) {
      return "";
    }
    else {
      return "<" + Joiner.on(", ").join(FluentIterable.from(typeParameters).transform(Functions.constant("?"))) + ">";
    }
  }
  private EclipseHack eclipseHack() {
    return new EclipseHack(processingEnv);
  }
}

