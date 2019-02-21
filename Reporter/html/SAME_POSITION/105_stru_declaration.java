  public static void processAnnotation(LoggingFramework framework, AnnotationValues<? extends java.lang.annotation.Annotation> annotation, Annotation source, EclipseNode annotationNode, String loggerCategory) {
    EclipseNode owner = annotationNode.up();
    switch (owner.getKind()){
      case TYPE:
      String logFieldName = annotationNode.getAst().readConfiguration(ConfigurationKeys.LOG_ANY_FIELD_NAME);
      if (logFieldName == null) 
        logFieldName = "log";
      boolean useStatic = !Boolean.FALSE.equals(annotationNode.getAst().readConfiguration(ConfigurationKeys.LOG_ANY_FIELD_IS_STATIC));
      TypeDeclaration typeDecl = null;
      if (owner.get() instanceof TypeDeclaration) 
        typeDecl = (TypeDeclaration)owner.get();
      int modifiers = typeDecl == null ? 0 : typeDecl.modifiers;
      boolean notAClass = (modifiers & (ClassFileConstants.AccInterface | ClassFileConstants.AccAnnotation)) != 0;
      if (typeDecl == null || notAClass) {
        annotationNode.addError(framework.getAnnotationAsString() + " is legal only on classes and enums.");
        return ;
      }
      if (fieldExists(logFieldName, owner) != MemberExistsResult.NOT_EXISTS) {
        annotationNode.addWarning("Field \'" + logFieldName + "\' already exists.");
        return ;
      }
      ClassLiteralAccess loggingType = selfType(owner, source);
      FieldDeclaration fieldDeclaration = createField(framework, source, loggingType, 
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\lombok\revisions\rev_e557413_fbab1ca\rev_left_e557413\src\core\lombok\eclipse\handlers\HandleLog.java
logFieldName
=======
loggerCategory
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\lombok\revisions\rev_e557413_fbab1ca\rev_right_fbab1ca\src\core\lombok\eclipse\handlers\HandleLog.java
, useStatic);
      fieldDeclaration.traverse(new SetGeneratedByVisitor(source), typeDecl.staticInitializerScope);
      injectField(owner, fieldDeclaration);
      owner.rebuild();
      break ;
      default:
      break ;
    }
  }


