  public static void processAnnotation(LoggingFramework framework, AnnotationValues<?> annotation, JavacNode annotationNode, String loggerCategory) {
    deleteAnnotationIfNeccessary(annotationNode, framework.getAnnotationClass());
    JavacNode typeNode = annotationNode.up();
    switch (typeNode.getKind()){
      case TYPE:
      String logFieldName = annotationNode.getAst().readConfiguration(ConfigurationKeys.LOG_ANY_FIELD_NAME);
      if (logFieldName == null) 
        logFieldName = "log";
      boolean useStatic = !Boolean.FALSE.equals(annotationNode.getAst().readConfiguration(ConfigurationKeys.LOG_ANY_FIELD_IS_STATIC));
      if ((((JCClassDecl)typeNode.get()).mods.flags & Flags.INTERFACE) != 0) {
        annotationNode.addError("@Log is legal only on classes and enums.");
        return ;
      }
      if (fieldExists(logFieldName, typeNode) != MemberExistsResult.NOT_EXISTS) {
        annotationNode.addWarning("Field \'" + logFieldName + "\' already exists.");
        return ;
      }
      JCFieldAccess loggingType = selfType(typeNode);
      createField(framework, typeNode, loggingType, annotationNode.get(), 
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\lombok\revisions\rev_e557413_fbab1ca\rev_left_e557413\src\core\lombok\javac\handlers\HandleLog.java
logFieldName
=======
loggerCategory
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\lombok\revisions\rev_e557413_fbab1ca\rev_right_fbab1ca\src\core\lombok\javac\handlers\HandleLog.java
, useStatic);
      break ;
      default:
      annotationNode.addError("@Log is legal only on types.");
      break ;
    }
  }


