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
      ParameterType parameterType = parameterTypes.get(n);
      if (parameterType.getDateFormat() != null) {
        SingleValueConverter converter;
        ParameterType parameterType = parameterTypes.get(n);
        if (parameterType.getDateFormat() != null) {
          converter = new DateConverter(parameterType.getDateFormat(), locale);
        }
        else {
          converter = (SingleValueConverter)converterLookup.lookupConverterForType(parameterType.getParameterClass());
        }
      }
      else {
        converter = (SingleValueConverter)converterLookup.lookupConverterForType(parameterType.getParameterClass());
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


