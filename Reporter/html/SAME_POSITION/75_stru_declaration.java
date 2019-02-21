  private void writeGetDependenciesMethod(JavaWriter writer, ExecutableElement constructor, List<Element> fields, boolean disambiguateFields, TypeMirror supertype, boolean extendsBinding) throws IOException {
    writer.emitJavadoc(AdapterJavadocs.GET_DEPENDENCIES_METHOD);
    if (extendsBinding) {
      writer.emitAnnotation(Override.class);
    }
    String setOfBindings = JavaWriter.type(Set.class, "Binding<?>");
    writer.beginMethod("void", "getDependencies", EnumSet.of(PUBLIC), setOfBindings, "getBindings", setOfBindings, "injectMembersBindings");
    if (constructor != null) {
      for (Element parameter : constructor.getParameters()) {
        writer.emitStatement("getBindings.add(%s)", parameterName(disambiguateFields, parameter));
      }
    }
    for (Element field : fields) {
      writer.emitStatement("injectMembersBindings.add(%s)", fieldName(disambiguateFields, field));
    }
    if (supertype != null) {
      writer.emitStatement(
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\dagger\revisions\rev_1bc7c83_8f2e49e\rev_left_1bc7c83\compiler\src\main\java\dagger\internal\codegen\InjectAdapterProcessor.java
"nextInjectableAncestor.getDependencies(null, injectMembersBindings)"
=======
"injectMembersBindings.add(%s)"
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\dagger\revisions\rev_1bc7c83_8f2e49e\rev_right_8f2e49e\compiler\src\main\java\dagger\internal\codegen\InjectAdapterProcessor.java
);
    }
    writer.endMethod();
    writer.emitEmptyLine();
  }


