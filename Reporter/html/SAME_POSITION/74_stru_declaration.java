  private void writeSupertypeInjectorField(JavaWriter writer, 
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\dagger\revisions\rev_1bc7c83_8f2e49e\rev_left_1bc7c83\compiler\src\main\java\dagger\internal\codegen\InjectAdapterProcessor.java
TypeElement type
=======
TypeMirror supertype
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\dagger\revisions\rev_1bc7c83_8f2e49e\rev_right_8f2e49e\compiler\src\main\java\dagger\internal\codegen\InjectAdapterProcessor.java
, TypeMirror nextAncestor) throws IOException {
    TypeElement supertypeElement = ((TypeElement)processingEnv.getTypeUtils().asElement(nextAncestor));
    String adapterName = parentAdapterName(type, supertypeElement);
    writer.emitField(
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\dagger\revisions\rev_1bc7c83_8f2e49e\rev_left_1bc7c83\compiler\src\main\java\dagger\internal\codegen\InjectAdapterProcessor.java
adapterName
=======
JavaWriter.type(Binding.class, rawTypeToString(supertype, '.'))
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\dagger\revisions\rev_1bc7c83_8f2e49e\rev_right_8f2e49e\compiler\src\main\java\dagger\internal\codegen\InjectAdapterProcessor.java
, 
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\dagger\revisions\rev_1bc7c83_8f2e49e\rev_left_1bc7c83\compiler\src\main\java\dagger\internal\codegen\InjectAdapterProcessor.java
"nextInjectableAncestor"
=======
"supertype"
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\dagger\revisions\rev_1bc7c83_8f2e49e\rev_right_8f2e49e\compiler\src\main\java\dagger\internal\codegen\InjectAdapterProcessor.java
, EnumSet.of(PRIVATE), "new " + adapterName + "()");
  }


