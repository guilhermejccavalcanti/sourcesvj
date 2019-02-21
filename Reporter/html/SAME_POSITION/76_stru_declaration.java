  private void writeMembersInjectMethod(JavaWriter writer, List<Element> fields, boolean disambiguateFields, String strippedTypeName, TypeMirror supertype) throws IOException {
    writer.emitJavadoc(AdapterJavadocs.MEMBERS_INJECT_METHOD, strippedTypeName);
    writer.emitAnnotation(Override.class);
    writer.beginMethod("void", "injectMembers", EnumSet.of(PUBLIC), strippedTypeName, "object");
    for (Element field : fields) {
      writer.emitStatement("object.%s = %s.get()", field.getSimpleName(), fieldName(disambiguateFields, field));
    }
    if (supertype != null) {
      writer.emitStatement(
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\dagger\revisions\rev_1bc7c83_8f2e49e\rev_left_1bc7c83\compiler\src\main\java\dagger\internal\codegen\InjectAdapterProcessor.java
"nextInjectableAncestor.injectMembers(object)"
=======
"supertype.injectMembers(object)"
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\dagger\revisions\rev_1bc7c83_8f2e49e\rev_right_8f2e49e\compiler\src\main\java\dagger\internal\codegen\InjectAdapterProcessor.java
);
    }
    writer.endMethod();
    writer.emitEmptyLine();
  }


