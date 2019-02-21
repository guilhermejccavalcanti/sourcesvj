package org.apache.hadoop.hive.ql.plan;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.ql.ErrorMsg;
import org.apache.hadoop.hive.ql.exec.FunctionRegistry;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.exec.Utilities;
import org.apache.hadoop.hive.ql.session.SessionState;
import org.apache.hadoop.hive.ql.session.SessionState.LogHelper;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDFBaseCompare;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDFBridge;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoUtils;

public class ExprNodeGenericFuncDesc extends ExprNodeDesc implements Serializable {
  private static final long serialVersionUID = 1L;
  private static final Log LOG = LogFactory.getLog(ExprNodeGenericFuncDesc.class.getName());
  private GenericUDF genericUDF;
  private List<ExprNodeDesc> chidren;
  private transient String funcText;
  private transient ObjectInspector writableObjectInspector;
  private boolean isSortedExpr;
  public ExprNodeGenericFuncDesc() {
    ;
  }
  public ExprNodeGenericFuncDesc(TypeInfo typeInfo, GenericUDF genericUDF, String funcText, List<ExprNodeDesc> children) {
    this(TypeInfoUtils.getStandardWritableObjectInspectorFromTypeInfo(typeInfo), genericUDF, funcText, children);
  }
  public ExprNodeGenericFuncDesc(ObjectInspector oi, GenericUDF genericUDF, String funcText, List<ExprNodeDesc> children) {
    super(TypeInfoUtils.getTypeInfoFromObjectInspector(oi));
    this.writableObjectInspector = ObjectInspectorUtils.getWritableObjectInspector(oi);
    assert (genericUDF != null);
    this.genericUDF = genericUDF;
    this.chidren = children;
    this.funcText = funcText;
  }
  public ExprNodeGenericFuncDesc(TypeInfo typeInfo, GenericUDF genericUDF, List<ExprNodeDesc> children) {
    this(typeInfo, genericUDF, null, children);
  }
  public ExprNodeGenericFuncDesc(ObjectInspector oi, GenericUDF genericUDF, List<ExprNodeDesc> children) {
    this(oi, genericUDF, null, children);
  }
  @Override public ObjectInspector getWritableObjectInspector() {
    return writableObjectInspector;
  }
  public GenericUDF getGenericUDF() {
    return genericUDF;
  }
  public void setGenericUDF(GenericUDF genericUDF) {
    this.genericUDF = genericUDF;
  }
  public void setChildren(List<ExprNodeDesc> children) {
    chidren = children;
  }
  @Override public List<ExprNodeDesc> getChildren() {
    return chidren;
  }
  @Override public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(genericUDF.getClass().getSimpleName());
    sb.append("(");
    if (chidren != null) {
      for (int i = 0; i < chidren.size(); i++) {
        if (i > 0) {
          sb.append(", ");
        }
        sb.append(
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\hive\revisions\rev_95bfd92_6c2d71c\rev_left_95bfd92\ql\src\java\org\apache\hadoop\hive\ql\plan\ExprNodeGenericFuncDesc.java
chidren.get(i).toString()
=======
chidren.get(i)
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\hive\revisions\rev_95bfd92_6c2d71c\rev_right_6c2d71c\ql\src\java\org\apache\hadoop\hive\ql\plan\ExprNodeGenericFuncDesc.java
);
      }
    }
    sb.append(")");
    return sb.toString();
  }
  @Override public String getExprString() {
    String[] childrenExprStrings = new String[chidren.size()];
    for (int i = 0; i < childrenExprStrings.length; i++) {
      childrenExprStrings[i] = chidren.get(i).getExprString();
    }
    return genericUDF.getDisplayString(childrenExprStrings);
  }
  @Override public List<String> getCols() {
    List<String> colList = new ArrayList<String>();
    if (chidren != null) {
      int pos = 0;
      while (pos < chidren.size()){
        List<String> colCh = chidren.get(pos).getCols();
        colList = Utilities.mergeUniqElems(colList, colCh);
        pos++;
      }
    }
    return colList;
  }
  @Override public ExprNodeDesc clone() {
    List<ExprNodeDesc> cloneCh = new ArrayList<ExprNodeDesc>(chidren.size());
    for (ExprNodeDesc ch : chidren) {
      cloneCh.add(ch.clone());
    }
    ExprNodeGenericFuncDesc clone = new ExprNodeGenericFuncDesc(typeInfo, FunctionRegistry.cloneGenericUDF(genericUDF), funcText, cloneCh);
    return clone;
  }
  public static ExprNodeGenericFuncDesc newInstance(GenericUDF genericUDF, String funcText, List<ExprNodeDesc> children) throws UDFArgumentException {
    ObjectInspector[] childrenOIs = new ObjectInspector[children.size()];
    for (int i = 0; i < childrenOIs.length; i++) {
      childrenOIs[i] = children.get(i).getWritableObjectInspector();
    }
    if (genericUDF instanceof GenericUDFBaseCompare && children.size() == 2) {
      TypeInfo oiTypeInfo0 = children.get(0).getTypeInfo();
      TypeInfo oiTypeInfo1 = children.get(1).getTypeInfo();
      SessionState ss = SessionState.get();
      Configuration conf = (ss != null) ? ss.getConf() : new Configuration();
      LogHelper console = new LogHelper(LOG);
      if ((oiTypeInfo0.equals(TypeInfoFactory.stringTypeInfo) && oiTypeInfo1.equals(TypeInfoFactory.longTypeInfo)) || (oiTypeInfo0.equals(TypeInfoFactory.longTypeInfo) && oiTypeInfo1.equals(TypeInfoFactory.stringTypeInfo))) {
        if (HiveConf.getVar(conf, HiveConf.ConfVars.HIVEMAPREDMODE).equalsIgnoreCase("strict")) {
          throw new UDFArgumentException(ErrorMsg.NO_COMPARE_BIGINT_STRING.getMsg());
        }
        else {
          console.printError("WARNING: Comparing a bigint and a string may result in a loss of precision.");
        }
      }
      else 
        if ((oiTypeInfo0.equals(TypeInfoFactory.doubleTypeInfo) && oiTypeInfo1.equals(TypeInfoFactory.longTypeInfo)) || (oiTypeInfo0.equals(TypeInfoFactory.longTypeInfo) && oiTypeInfo1.equals(TypeInfoFactory.doubleTypeInfo))) {
          if (HiveConf.getVar(conf, HiveConf.ConfVars.HIVEMAPREDMODE).equalsIgnoreCase("strict")) {
            throw new UDFArgumentException(ErrorMsg.NO_COMPARE_BIGINT_DOUBLE.getMsg());
          }
          else {
            console.printError("WARNING: Comparing a bigint and a double may result in a loss of precision.");
          }
        }
    }
    ObjectInspector oi = genericUDF.initializeAndFoldConstants(childrenOIs);
    String[] requiredJars = genericUDF.getRequiredJars();
    String[] requiredFiles = genericUDF.getRequiredFiles();
    SessionState ss = SessionState.get();
    if (requiredJars != null) {
      SessionState.ResourceType t = SessionState.find_resource_type("JAR");
      try {
        ss.add_resources(t, Arrays.asList(requiredJars));
      }
      catch (Exception e) {
        throw new UDFArgumentException(e);
      }
    }
    if (requiredFiles != null) {
      SessionState.ResourceType t = SessionState.find_resource_type("FILE");
      try {
        ss.add_resources(t, Arrays.asList(requiredFiles));
      }
      catch (Exception e) {
        throw new UDFArgumentException(e);
      }
    }
    return new ExprNodeGenericFuncDesc(oi, genericUDF, funcText, children);
  }
  public static ExprNodeGenericFuncDesc newInstance(GenericUDF genericUDF, List<ExprNodeDesc> children) throws UDFArgumentException {
    return newInstance(genericUDF, null, children);
  }
  @Override public boolean isSame(Object o) {
    if (!(o instanceof ExprNodeGenericFuncDesc)) {
      return false;
    }
    ExprNodeGenericFuncDesc dest = (ExprNodeGenericFuncDesc)o;
    if (!typeInfo.equals(dest.getTypeInfo()) || !genericUDF.getClass().equals(dest.getGenericUDF().getClass())) {
      return false;
    }
    if (genericUDF instanceof GenericUDFBridge) {
      GenericUDFBridge bridge = (GenericUDFBridge)genericUDF;
      GenericUDFBridge bridge2 = (GenericUDFBridge)dest.getGenericUDF();
      if (!bridge.getUdfClassName().equals(bridge2.getUdfClassName()) || !bridge.getUdfName().equals(bridge2.getUdfName()) || bridge.isOperator() != bridge2.isOperator()) {
        return false;
      }
    }
    if (chidren.size() != dest.getChildren().size()) {
      return false;
    }
    for (int pos = 0; pos < chidren.size(); pos++) {
      if (!chidren.get(pos).isSame(dest.getChildren().get(pos))) {
        return false;
      }
    }
    return true;
  }
  @Override public int hashCode() {
    int superHashCode = super.hashCode();
    HashCodeBuilder builder = new HashCodeBuilder();
    builder.appendSuper(superHashCode);
    builder.append(chidren);
    return builder.toHashCode();
  }
  public boolean isSortedExpr() {
    return isSortedExpr;
  }
  public void setSortedExpr(boolean isSortedExpr) {
    this.isSortedExpr = isSortedExpr;
  }
  public String getFuncText() {
    return this.funcText;
  }
}

