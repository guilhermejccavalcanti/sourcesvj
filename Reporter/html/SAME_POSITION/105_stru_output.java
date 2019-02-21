package lombok.eclipse.handlers;
import static lombok.eclipse.Eclipse.fromQualifiedName;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import lombok.ConfigurationKeys;
import lombok.core.AnnotationValues;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.EclipseHandlerUtil.MemberExistsResult;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.StringLiteral;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.mangosdk.spi.ProviderFor;
import org.mangosdk.spi.ProviderFor;
import static lombok.core.handlers.HandlerUtil.*;
import static lombok.eclipse.handlers.EclipseHandlerUtil.*;

public class HandleLog {
  private HandleLog() {
    throw new UnsupportedOperationException();
  }
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
  public static ClassLiteralAccess selfType(EclipseNode type, Annotation source) {
    int pS = source.sourceStart, pE = source.sourceEnd;
    long p = (long)pS << 32 | pE;
    TypeDeclaration typeDeclaration = (TypeDeclaration)type.get();
    TypeReference typeReference = new SingleTypeReference(typeDeclaration.name, p);
    setGeneratedBy(typeReference, source);
    ClassLiteralAccess result = new ClassLiteralAccess(source.sourceEnd, typeReference);
    setGeneratedBy(result, source);
    return result;
  }
  public static FieldDeclaration createField(LoggingFramework framework, Annotation source, ClassLiteralAccess loggingType, 
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\lombok\revisions\rev_e557413_fbab1ca\rev_left_e557413\src\core\lombok\eclipse\handlers\HandleLog.java
String logFieldName
=======
String loggerCategory
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\lombok\revisions\rev_e557413_fbab1ca\rev_right_fbab1ca\src\core\lombok\eclipse\handlers\HandleLog.java
, boolean useStatic) {
    int pS = source.sourceStart, pE = source.sourceEnd;
    long p = (long)pS << 32 | pE;
    FieldDeclaration fieldDecl = new FieldDeclaration(logFieldName.toCharArray(), 0, -1);
    setGeneratedBy(fieldDecl, source);
    fieldDecl.declarationSourceEnd = -1;
    fieldDecl.modifiers = Modifier.PRIVATE | (useStatic ? Modifier.STATIC : 0) | Modifier.FINAL;
    fieldDecl.type = createTypeReference(framework.getLoggerTypeName(), source);
    MessageSend factoryMethodCall = new MessageSend();
    setGeneratedBy(factoryMethodCall, source);
    factoryMethodCall.receiver = createNameReference(framework.getLoggerFactoryTypeName(), source);
    factoryMethodCall.selector = framework.getLoggerFactoryMethodName().toCharArray();
    Expression parameter = framework.createFactoryParameter(loggingType, source);
    if (loggerCategory == null || loggerCategory.trim().length() == 0) {
      parameter = framework.createFactoryParameter(loggingType, source);
    }
    else {
      parameter = new StringLiteral(loggerCategory.toCharArray(), pS, pE, 0);
    }
    factoryMethodCall.arguments = new Expression[]{ parameter } ;
    factoryMethodCall.nameSourcePosition = p;
    factoryMethodCall.sourceStart = pS;
    factoryMethodCall.sourceEnd = factoryMethodCall.statementEnd = pE;
    fieldDecl.initialization = factoryMethodCall;
    return fieldDecl;
  }
  public static TypeReference createTypeReference(String typeName, Annotation source) {
    int pS = source.sourceStart, pE = source.sourceEnd;
    long p = (long)pS << 32 | pE;
    TypeReference typeReference;
    if (typeName.contains(".")) {
      char[][] typeNameTokens = fromQualifiedName(typeName);
      long[] pos = new long[typeNameTokens.length];
      Arrays.fill(pos, p);
      typeReference = new QualifiedTypeReference(typeNameTokens, pos);
    }
    else {
      typeReference = null;
    }
    setGeneratedBy(typeReference, source);
    return typeReference;
  }
  
  @ProviderFor(value = EclipseAnnotationHandler.class) public static class HandleCommonsLog extends EclipseAnnotationHandler<lombok.extern.apachecommons.CommonsLog> {
    @Override public void handle(AnnotationValues<lombok.extern.apachecommons.CommonsLog> annotation, Annotation source, EclipseNode annotationNode) {
      handleFlagUsage(annotationNode, ConfigurationKeys.LOG_COMMONS_FLAG_USAGE, "@apachecommons.CommonsLog", ConfigurationKeys.LOG_ANY_FLAG_USAGE, "any @Log");
      processAnnotation(LoggingFramework.COMMONS, annotation, source, annotationNode, annotation.getInstance().topic());
    }
  }
  
  @ProviderFor(value = EclipseAnnotationHandler.class) public static class HandleJulLog extends EclipseAnnotationHandler<lombok.extern.java.Log> {
    @Override public void handle(AnnotationValues<lombok.extern.java.Log> annotation, Annotation source, EclipseNode annotationNode) {
      handleFlagUsage(annotationNode, ConfigurationKeys.LOG_JUL_FLAG_USAGE, "@java.Log", ConfigurationKeys.LOG_ANY_FLAG_USAGE, "any @Log");
      processAnnotation(LoggingFramework.JUL, annotation, source, annotationNode, annotation.getInstance().topic());
    }
  }
  
  @ProviderFor(value = EclipseAnnotationHandler.class) public static class HandleLog4jLog extends EclipseAnnotationHandler<lombok.extern.log4j.Log4j> {
    @Override public void handle(AnnotationValues<lombok.extern.log4j.Log4j> annotation, Annotation source, EclipseNode annotationNode) {
      handleFlagUsage(annotationNode, ConfigurationKeys.LOG_LOG4J_FLAG_USAGE, "@Log4j", ConfigurationKeys.LOG_ANY_FLAG_USAGE, "any @Log");
      processAnnotation(LoggingFramework.LOG4J, annotation, source, annotationNode, annotation.getInstance().topic());
    }
  }
  
  @ProviderFor(value = EclipseAnnotationHandler.class) public static class HandleLog4j2Log extends EclipseAnnotationHandler<lombok.extern.log4j.Log4j2> {
    @Override public void handle(AnnotationValues<lombok.extern.log4j.Log4j2> annotation, Annotation source, EclipseNode annotationNode) {
      handleFlagUsage(annotationNode, ConfigurationKeys.LOG_LOG4J2_FLAG_USAGE, "@Log4j2", ConfigurationKeys.LOG_ANY_FLAG_USAGE, "any @Log");
      processAnnotation(LoggingFramework.LOG4J2, annotation, source, annotationNode, annotation.getInstance().topic());
    }
  }
  
  @ProviderFor(value = EclipseAnnotationHandler.class) public static class HandleSlf4jLog extends EclipseAnnotationHandler<lombok.extern.slf4j.Slf4j> {
    @Override public void handle(AnnotationValues<lombok.extern.slf4j.Slf4j> annotation, Annotation source, EclipseNode annotationNode) {
      handleFlagUsage(annotationNode, ConfigurationKeys.LOG_SLF4J_FLAG_USAGE, "@Slf4j", ConfigurationKeys.LOG_ANY_FLAG_USAGE, "any @Log");
      processAnnotation(LoggingFramework.SLF4J, annotation, source, annotationNode, annotation.getInstance().topic());
    }
  }
  
  @ProviderFor(value = EclipseAnnotationHandler.class) public static class HandleXSlf4jLog extends EclipseAnnotationHandler<lombok.extern.slf4j.XSlf4j> {
    @Override public void handle(AnnotationValues<lombok.extern.slf4j.XSlf4j> annotation, Annotation source, EclipseNode annotationNode) {
      handleFlagUsage(annotationNode, ConfigurationKeys.LOG_XSLF4J_FLAG_USAGE, "@XSlf4j", ConfigurationKeys.LOG_ANY_FLAG_USAGE, "any @Log");
      processAnnotation(LoggingFramework.XSLF4J, annotation, source, annotationNode, annotation.getInstance().topic());
    }
  }
  enum LoggingFramework {
    COMMONS("org.apache.commons.logging.Log", "org.apache.commons.logging.LogFactory", "getLog", "@CommonsLog"),

    JUL("java.util.logging.Logger", "java.util.logging.Logger", "getLogger", "@Log") {
        @Override public Expression createFactoryParameter(ClassLiteralAccess type, Annotation source) {
          int pS = source.sourceStart, pE = source.sourceEnd;
          long p = (long)pS << 32 | pE;
          MessageSend factoryParameterCall = new MessageSend();
          setGeneratedBy(factoryParameterCall, source);
          factoryParameterCall.receiver = super.createFactoryParameter(type, source);
          factoryParameterCall.selector = "getName".toCharArray();
          factoryParameterCall.nameSourcePosition = p;
          factoryParameterCall.sourceStart = pS;
          factoryParameterCall.sourceEnd = factoryParameterCall.statementEnd = pE;
          return factoryParameterCall;
        }
    },

    LOG4J("org.apache.log4j.Logger", "org.apache.log4j.Logger", "getLogger", "@Log4j"),

    LOG4J2("org.apache.logging.log4j.Logger", "org.apache.logging.log4j.LogManager", "getLogger", "@Log4j2"),

    SLF4J("org.slf4j.Logger", "org.slf4j.LoggerFactory", "getLogger", "@Slf4j"),

    XSLF4J("org.slf4j.ext.XLogger", "org.slf4j.ext.XLoggerFactory", "getXLogger", "@XSlf4j"),

  ;
    private final String loggerTypeName;
    private final String loggerFactoryTypeName;
    private final String loggerFactoryMethodName;
    private final String annotationAsString;
  LoggingFramework(String loggerTypeName, String loggerFactoryTypeName, String loggerFactoryMethodName, String annotationAsString) {
      this.loggerTypeName = loggerTypeName;
      this.loggerFactoryTypeName = loggerFactoryTypeName;
      this.loggerFactoryMethodName = loggerFactoryMethodName;
      this.annotationAsString = annotationAsString;
  }
    final String getAnnotationAsString() {
      return annotationAsString;
    }
    final String getLoggerTypeName() {
      return loggerTypeName;
    }
    final String getLoggerFactoryTypeName() {
      return loggerFactoryTypeName;
    }
    final String getLoggerFactoryMethodName() {
      return loggerFactoryMethodName;
    }
    Expression createFactoryParameter(ClassLiteralAccess loggingType, Annotation source) {
      TypeReference copy = copyType(loggingType.type, source);
      ClassLiteralAccess result = new ClassLiteralAccess(source.sourceEnd, copy);
      setGeneratedBy(result, source);
      return result;
    }
  }
}

