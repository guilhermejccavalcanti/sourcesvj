package lombok.javac.handlers;
import java.lang.annotation.Annotation;
import lombok.ConfigurationKeys;
import lombok.core.AnnotationValues;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.JavacTreeMaker;
import org.mangosdk.spi.ProviderFor;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCAnnotation;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCMethodInvocation;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import static lombok.core.handlers.HandlerUtil.*;
import static lombok.javac.handlers.JavacHandlerUtil.*;

public class HandleLog {
  private HandleLog() {
    throw new UnsupportedOperationException();
  }
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
  public static JCFieldAccess selfType(JavacNode typeNode) {
    JavacTreeMaker maker = typeNode.getTreeMaker();
    Name name = ((JCClassDecl)typeNode.get()).name;
    return maker.Select(maker.Ident(name), typeNode.toName("class"));
  }
  public static boolean createField(LoggingFramework framework, JavacNode typeNode, JCFieldAccess loggingType, JCTree source, 
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\lombok\revisions\rev_e557413_fbab1ca\rev_left_e557413\src\core\lombok\javac\handlers\HandleLog.java
String logFieldName
=======
String loggerCategory
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\lombok\revisions\rev_e557413_fbab1ca\rev_right_fbab1ca\src\core\lombok\javac\handlers\HandleLog.java
, boolean useStatic) {
    JavacTreeMaker maker = typeNode.getTreeMaker();
    JCExpression loggerType = chainDotsString(typeNode, framework.getLoggerTypeName());
    JCExpression factoryMethod = chainDotsString(typeNode, framework.getLoggerFactoryMethodName());
    JCExpression loggerName = framework.createFactoryParameter(typeNode, loggingType);
    if (loggerCategory == null || loggerCategory.trim().length() == 0) {
      loggerName = framework.createFactoryParameter(typeNode, loggingType);
    }
    else {
      loggerName = maker.Literal(loggerCategory);
    }
    JCMethodInvocation factoryMethodCall = maker.Apply(List.<JCExpression>nil(), factoryMethod, List.<JCExpression>of(loggerName));
    JCVariableDecl fieldDecl = recursiveSetGeneratedBy(maker.VarDef(maker.Modifiers(Flags.PRIVATE | Flags.FINAL | (useStatic ? Flags.STATIC : 0)), typeNode.toName(logFieldName), loggerType, factoryMethodCall), source, typeNode.getContext());
    injectFieldSuppressWarnings(typeNode, fieldDecl);
    return true;
  }
  
  @ProviderFor(value = JavacAnnotationHandler.class) public static class HandleCommonsLog extends JavacAnnotationHandler<lombok.extern.apachecommons.CommonsLog> {
    @Override public void handle(AnnotationValues<lombok.extern.apachecommons.CommonsLog> annotation, JCAnnotation ast, JavacNode annotationNode) {
      handleFlagUsage(annotationNode, ConfigurationKeys.LOG_COMMONS_FLAG_USAGE, "@apachecommons.CommonsLog", ConfigurationKeys.LOG_ANY_FLAG_USAGE, "any @Log");
      processAnnotation(LoggingFramework.COMMONS, annotation, annotationNode, annotation.getInstance().topic());
    }
  }
  
  @ProviderFor(value = JavacAnnotationHandler.class) public static class HandleJulLog extends JavacAnnotationHandler<lombok.extern.java.Log> {
    @Override public void handle(AnnotationValues<lombok.extern.java.Log> annotation, JCAnnotation ast, JavacNode annotationNode) {
      handleFlagUsage(annotationNode, ConfigurationKeys.LOG_JUL_FLAG_USAGE, "@java.Log", ConfigurationKeys.LOG_ANY_FLAG_USAGE, "any @Log");
      processAnnotation(LoggingFramework.JUL, annotation, annotationNode, annotation.getInstance().topic());
    }
  }
  
  @ProviderFor(value = JavacAnnotationHandler.class) public static class HandleLog4jLog extends JavacAnnotationHandler<lombok.extern.log4j.Log4j> {
    @Override public void handle(AnnotationValues<lombok.extern.log4j.Log4j> annotation, JCAnnotation ast, JavacNode annotationNode) {
      handleFlagUsage(annotationNode, ConfigurationKeys.LOG_LOG4J_FLAG_USAGE, "@Log4j", ConfigurationKeys.LOG_ANY_FLAG_USAGE, "any @Log");
      processAnnotation(LoggingFramework.LOG4J, annotation, annotationNode, annotation.getInstance().topic());
    }
  }
  
  @ProviderFor(value = JavacAnnotationHandler.class) public static class HandleLog4j2Log extends JavacAnnotationHandler<lombok.extern.log4j.Log4j2> {
    @Override public void handle(AnnotationValues<lombok.extern.log4j.Log4j2> annotation, JCAnnotation ast, JavacNode annotationNode) {
      handleFlagUsage(annotationNode, ConfigurationKeys.LOG_LOG4J2_FLAG_USAGE, "@Log4j2", ConfigurationKeys.LOG_ANY_FLAG_USAGE, "any @Log");
      processAnnotation(LoggingFramework.LOG4J2, annotation, annotationNode, annotation.getInstance().topic());
    }
  }
  
  @ProviderFor(value = JavacAnnotationHandler.class) public static class HandleSlf4jLog extends JavacAnnotationHandler<lombok.extern.slf4j.Slf4j> {
    @Override public void handle(AnnotationValues<lombok.extern.slf4j.Slf4j> annotation, JCAnnotation ast, JavacNode annotationNode) {
      handleFlagUsage(annotationNode, ConfigurationKeys.LOG_SLF4J_FLAG_USAGE, "@Slf4j", ConfigurationKeys.LOG_ANY_FLAG_USAGE, "any @Log");
      processAnnotation(LoggingFramework.SLF4J, annotation, annotationNode, annotation.getInstance().topic());
    }
  }
  
  @ProviderFor(value = JavacAnnotationHandler.class) public static class HandleXSlf4jLog extends JavacAnnotationHandler<lombok.extern.slf4j.XSlf4j> {
    @Override public void handle(AnnotationValues<lombok.extern.slf4j.XSlf4j> annotation, JCAnnotation ast, JavacNode annotationNode) {
      handleFlagUsage(annotationNode, ConfigurationKeys.LOG_XSLF4J_FLAG_USAGE, "@XSlf4j", ConfigurationKeys.LOG_ANY_FLAG_USAGE, "any @Log");
      processAnnotation(LoggingFramework.XSLF4J, annotation, annotationNode, annotation.getInstance().topic());
    }
  }
  enum LoggingFramework {
    COMMONS(lombok.extern.apachecommons.CommonsLog.class, "org.apache.commons.logging.Log", "org.apache.commons.logging.LogFactory.getLog"),

    JUL(lombok.extern.java.Log.class, "java.util.logging.Logger", "java.util.logging.Logger.getLogger") {
        @Override public JCExpression createFactoryParameter(JavacNode typeNode, JCFieldAccess loggingType) {
          JavacTreeMaker maker = typeNode.getTreeMaker();
          JCExpression method = maker.Select(loggingType, typeNode.toName("getName"));
          return maker.Apply(List.<JCExpression>nil(), method, List.<JCExpression>nil());
        }
    },

    LOG4J(lombok.extern.log4j.Log4j.class, "org.apache.log4j.Logger", "org.apache.log4j.Logger.getLogger"),

    LOG4J2(lombok.extern.log4j.Log4j2.class, "org.apache.logging.log4j.Logger", "org.apache.logging.log4j.LogManager.getLogger"),

    SLF4J(lombok.extern.slf4j.Slf4j.class, "org.slf4j.Logger", "org.slf4j.LoggerFactory.getLogger"),

    XSLF4J(lombok.extern.slf4j.XSlf4j.class, "org.slf4j.ext.XLogger", "org.slf4j.ext.XLoggerFactory.getXLogger"),

  ;
    private final Class<? extends Annotation> annotationClass;
    private final String loggerTypeName;
    private final String loggerFactoryName;
  LoggingFramework(Class<? extends Annotation> annotationClass, String loggerTypeName, String loggerFactoryName) {
      this.annotationClass = annotationClass;
      this.loggerTypeName = loggerTypeName;
      this.loggerFactoryName = loggerFactoryName;
  }
    final Class<? extends Annotation> getAnnotationClass() {
      return annotationClass;
    }
    final String getLoggerTypeName() {
      return loggerTypeName;
    }
    final String getLoggerFactoryMethodName() {
      return loggerFactoryName;
    }
    JCExpression createFactoryParameter(JavacNode typeNode, JCFieldAccess loggingType) {
      return loggingType;
    }
  }
}

