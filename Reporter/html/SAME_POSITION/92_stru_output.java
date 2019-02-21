package cucumber.runtime;
import static gherkin.util.FixJava.map;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import cucumber.runtime.converters.LocalizedXStreams;
import cucumber.runtime.converters.TimeConverter;
import cucumber.table.DataTable;
import cucumber.table.TableConverter;
import gherkin.I18n;
import gherkin.formatter.Argument;
import gherkin.formatter.model.DataTableRow;
import gherkin.formatter.model.Match;
import gherkin.formatter.model.Step;
import gherkin.util.Mapper;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StepDefinitionMatch extends Match {
  private final StepDefinition stepDefinition;
  private final transient String uri;
  private final transient Step step;
  private final LocalizedXStreams localizedXStreams;
  public StepDefinitionMatch(List<Argument> arguments, StepDefinition stepDefinition, String uri, Step step, LocalizedXStreams localizedXStreams) {
    super(arguments, stepDefinition.getLocation());
    this.stepDefinition = stepDefinition;
    this.uri = uri;
    this.step = step;
    this.localizedXStreams = localizedXStreams;
  }
  public void runStep(I18n i18n) throws Throwable {
    try {
      Object[] args = transformedArgs(stepDefinition.getParameterTypes(), step, localizedXStreams.get(i18n), i18n.getLocale());
      stepDefinition.execute(i18n, args);
    }
    catch (CucumberException e) {
      throw e;
    }
    catch (Throwable t) {
      throw removeFrameworkFramesAndAppendStepLocation(t, getStepLocation());
    }
  }
  private Object[] transformedArgs(List<ParameterType> parameterTypes, Step step, LocalizedXStreams.LocalizedXStream xStream, Locale locale) {
    if (xStream == null) {
      throw new NullPointerException("xStream");
    }
    int argumentCount = getArguments().size();
    if (step.getDocString() != null) 
      argumentCount++;
    if (step.getRows() != null) 
      argumentCount++;
    if (parameterTypes != null) {
      if (parameterTypes.size() != argumentCount) {
        List<Argument> arguments = createArgumentsForErrorMessage(step);
        throw new CucumberException("Arity mismatch. Declared parameters: " + parameterTypes + ". Matched arguments: " + arguments);
      }
    }
    else {
      parameterTypes = Utils.listOf(argumentCount, new ParameterType(String.class, null));
    }
    Object[] result = new Object[argumentCount];
    ConverterLookup converterLookup = xStream.getConverterLookup();
    int n = 0;
    for (Argument a : getArguments()) {
      TimeConverter timeConverter = null;
      if (parameterTypes != null) {
        SingleValueConverter converter;
        ParameterType parameterType = parameterTypes.get(n);
        if (parameterType.getDateFormat() != null) {
          converter = new DateConverter(parameterType.getDateFormat(), locale);
        }
        else {
          converter = (SingleValueConverter)converterLookup.lookupConverterForType(parameterType.getParameterClass());
        }
        
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\cucumber-jvm\revisions\rev_393837b_d4ffedd\rev_left_393837b\core\src\main\java\cucumber\runtime\StepDefinitionMatch.java
timeConverter = TimeConverter.getInstance(parameterType, locale)
=======
result[n] = converter.fromString(a.getVal())
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\cucumber-jvm\revisions\rev_393837b_d4ffedd\rev_right_d4ffedd\core\src\main\java\cucumber\runtime\StepDefinitionMatch.java
;
        timeConverter.setOnlyFormat(parameterType.getDateFormat(), locale);
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\cucumber-jvm\revisions\rev_393837b_d4ffedd\rev_left_393837b\core\src\main\java\cucumber\runtime\StepDefinitionMatch.java
converter = timeConverter;
=======
result[n] = converter.fromString(a.getVal());
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\cucumber-jvm\revisions\rev_393837b_d4ffedd\rev_right_d4ffedd\core\src\main\java\cucumber\runtime\StepDefinitionMatch.java

      }
      else {
        result[n] = a.getVal();
      }
      try {
        result[n] = converter.fromString(a.getVal());
      }
      finally {
        if (timeConverter != null) {
          timeConverter.removeOnlyFormat();
        }
      }
      n++;
      try {
        result[n] = converter.fromString(a.getVal());
      }
      finally {
        if (timeConverter != null) {
          timeConverter.removeOnlyFormat();
        }
      }
      n++;
    }
    if (step.getRows() != null) {
      ParameterType parameterType = parameterTypes.get(n);
      xStream.setDateFormat(parameterType.getDateFormat());
      try {
        result[n] = tableArgument(step, n, xStream, parameterType.getDateFormat());
      }
      finally {
        xStream.unsetDateFormat();
      }
    }
    else 
      if (step.getDocString() != null) {
        result[n] = step.getDocString().getValue();
      }
    return result;
  }
  private List<Argument> createArgumentsForErrorMessage(Step step) {
    List<Argument> arguments = new ArrayList<Argument>(getArguments());
    if (step.getDocString() != null) {
      arguments.add(new Argument(-1, "DocString:" + step.getDocString().getValue()));
    }
    if (step.getRows() != null) {
      List<List<String>> rows = map(step.getRows(), new Mapper<DataTableRow, List<String>>() {
          @Override public List<String> map(DataTableRow row) {
            return row.getCells();
          }
      });
      arguments.add(new Argument(-1, "Table:" + rows.toString()));
    }
    return arguments;
  }
  private Object tableArgument(Step step, int argIndex, XStream xStream, String dateFormat) {
    DataTable table = new DataTable(step.getRows(), new TableConverter(xStream, dateFormat));
    Type listType = getGenericListType(argIndex);
    if (listType != null) {
      return table.asList(listType);
    }
    else {
      return table;
    }
  }
  private Type getGenericListType(int argIndex) {
    Type result = null;
    List<ParameterType> parameterTypes = stepDefinition.getParameterTypes();
    if (parameterTypes != null) {
      ParameterType parameterType = parameterTypes.get(argIndex);
      Type[] actualTypeArguments = parameterType.getActualTypeArguments();
      if (actualTypeArguments != null && actualTypeArguments.length > 0) {
        result = actualTypeArguments[0];
      }
    }
    return result;
  }
  public Throwable removeFrameworkFramesAndAppendStepLocation(Throwable error, StackTraceElement stepLocation) {
    StackTraceElement[] stackTraceElements = error.getStackTrace();
    if (stackTraceElements.length == 0 || stepLocation == null) {
      return error;
    }
    int newStackTraceLength;
    for (newStackTraceLength = 1; newStackTraceLength < stackTraceElements.length; ++newStackTraceLength) {
      if (stepDefinition.isDefinedAt(stackTraceElements[newStackTraceLength - 1])) {
        break ;
      }
    }
    StackTraceElement[] newStackTrace = new StackTraceElement[newStackTraceLength + 1];
    System.arraycopy(stackTraceElements, 0, newStackTrace, 0, newStackTraceLength);
    newStackTrace[newStackTraceLength] = stepLocation;
    error.setStackTrace(newStackTrace);
    return error;
  }
  public String getPattern() {
    return stepDefinition.getPattern();
  }
  public StackTraceElement getStepLocation() {
    return step.getStackTraceElement(uri);
  }
}

