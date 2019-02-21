  private void generateInjectAdapter(TypeElement type, ExecutableElement constructor, List<Element> fields) throws IOException {
    String packageName = getPackage(type).getQualifiedName().toString();
    String strippedTypeName = strippedTypeName(type.getQualifiedName().toString(), packageName);
    TypeMirror supertype = 
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\dagger\revisions\rev_1bc7c83_8f2e49e\rev_left_1bc7c83\compiler\src\main\java\dagger\internal\codegen\InjectAdapterProcessor.java
getNextMemberInjectedAncestor(type)
=======
getApplicationSupertype(type)
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\dagger\revisions\rev_1bc7c83_8f2e49e\rev_right_8f2e49e\compiler\src\main\java\dagger\internal\codegen\InjectAdapterProcessor.java
;
    String adapterName = adapterName(type, INJECT_ADAPTER_SUFFIX);
    JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(adapterName, type);
    JavaWriter writer = new JavaWriter(sourceFile.openWriter());
    boolean isAbstract = type.getModifiers().contains(ABSTRACT);
    boolean injectMembers = !fields.isEmpty() || supertype != null;
    boolean disambiguateFields = !fields.isEmpty() && (constructor != null) && !constructor.getParameters().isEmpty();
    boolean dependent = injectMembers || ((constructor != null) && !constructor.getParameters().isEmpty());
    writer.emitSingleLineComment(AdapterJavadocs.GENERATED_BY_DAGGER);
    writer.emitPackage(packageName);
    writer.emitImports(findImports(dependent, injectMembers, constructor != null));
    writer.emitEmptyLine();
    writer.emitJavadoc(bindingTypeDocs(strippedTypeName, isAbstract, injectMembers, dependent));
    writer.beginType(adapterName, "class", EnumSet.of(PUBLIC, FINAL), JavaWriter.type(Binding.class, strippedTypeName), implementedInterfaces(strippedTypeName, injectMembers, constructor != null));
    writeMemberBindingsFields(writer, fields, disambiguateFields);
    if (constructor != null) {
      writeParameterBindingsFields(writer, constructor, disambiguateFields);
    }
    if (supertype != null) {
      writeSupertypeInjectorField(writer, type, supertype);
    }
    writer.emitEmptyLine();
    writeInjectAdapterConstructor(writer, constructor, type, strippedTypeName, adapterName);
    if (dependent) {
      writeAttachMethod(writer, constructor, fields, disambiguateFields, strippedTypeName, supertype, true);
      writeGetDependenciesMethod(writer, constructor, fields, disambiguateFields, supertype, true);
    }
    if (constructor != null) {
      writeGetMethod(writer, constructor, disambiguateFields, injectMembers, strippedTypeName);
    }
    if (injectMembers) {
      writeMembersInjectMethod(writer, fields, disambiguateFields, strippedTypeName, supertype);
    }
    writer.endType();
    writer.close();
    if (supertype != null) {
      generateParentBindings(type, ((TypeElement)processingEnv.getTypeUtils().asElement(supertype)));
    }
  }


