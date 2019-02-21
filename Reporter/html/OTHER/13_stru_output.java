package org.nutz.mapl.impl.convert;
import org.nutz.castor.Castors;
import org.nutz.el.El;
import org.nutz.json.Json;
import org.nutz.json.entity.JsonEntity;
import org.nutz.json.entity.JsonEntityField;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.reflect.ReflectTool;
import org.nutz.lang.util.Context;
import java.util.Map.Entry;
import org.nutz.mapl.Mapl;
import java.util.Set;
import org.nutz.mapl.MaplConvert;
import java.util.Stack;
import java.lang.reflect.Array;
import org.nutz.castor.Castors;
import java.lang.reflect.Type;
import org.nutz.el.El;
import java.util.ArrayList;
import org.nutz.json.Json;
import java.util.Collection;
import org.nutz.json.entity.JsonEntity;
import java.util.HashSet;
import org.nutz.json.entity.JsonEntityField;
import java.util.LinkedHashMap;
import org.nutz.lang.Lang;
import java.util.List;
import org.nutz.lang.Mirror;
import java.util.Map;
import org.nutz.lang.util.Context;
import java.util.Set;
import org.nutz.mapl.Mapl;
import java.util.Stack;
import org.nutz.mapl.MaplConvert;

public class ObjConvertImpl implements MaplConvert {
  Stack<String> path = new Stack<String>();
  Context context = Lang.context();
  private Type type;
  public ObjConvertImpl(Type type) {
    this.type = type;
  }
  public Object convert(Object model) {
    if (model == null) 
      return null;
    if (type == null) 
      return model;
    if (!(model instanceof Map) && !(model instanceof List)) {
      return Castors.me().castTo(model, Lang.getTypeClass(type));
    }
    return inject(model, type);
  }
  Object inject(Object model, Type type) {
    if (model == null) {
      return null;
    }
    Mirror<?> me = Mirror.me(type);
    Object obj = null;
    if (Collection.class.isAssignableFrom(me.getType())) {
      obj = injectCollection(model, me);
    }
    else 
      if (Map.class.isAssignableFrom(me.getType())) {
        obj = injectMap(model, me);
      }
      else 
        if (me.getType().isArray()) {
          obj = injectArray(model, me);
        }
        else {
          obj = injectObj(model, me);
        }
    if (path.size() > 0) 
      path.pop();
    return obj;
  }
  @SuppressWarnings(value = {"unchecked", "rawtypes", }) private Object injectArray(Object model, Mirror<?> me) {
    Class<?> clazz = me.getType().getComponentType();
    List list = (List)model;
    List vals = new ArrayList();
    int j = 0;
    for (Object obj : list) {
      if (isLeaf(obj)) {
        vals.add(Castors.me().castTo(obj, clazz));
        continue ;
      }
      path.push("a" + (j++));
      vals.add(inject(obj, clazz));
    }
    Object obj = Array.newInstance(clazz, vals.size());
    for (int i = 0; i < vals.size(); i++) {
      Array.set(obj, i, vals.get(i));
    }
    return obj;
  }
  @SuppressWarnings(value = {"unchecked", "rawtypes", }) private Object injectMap(Object model, Mirror<?> me) {
    Map re = null;
    if (me.isInterface()) {
      re = new LinkedHashMap();
    }
    else {
      re = (Map)me.born();
    }
    Map map = (Map)model;
    if (me.getGenericsTypes() == null) {
      re.putAll(map);
      return re;
    }
    Type type = me.getGenericsType(1);
    for (Object key : map.keySet()) {
      Object val = map.get(key);
      if (!isLeaf(key)) {
        key = inject(key, me.getGenericsType(0));
      }
      if (isLeaf(val)) {
        re.put(key, Castors.me().castTo(val, Lang.getTypeClass(type)));
        continue ;
      }
      path.push(key.toString());
      re.put(key, inject(val, type));
    }
    return re;
  }
  @SuppressWarnings(value = {"rawtypes", "unchecked", }) private Object injectCollection(Object model, Mirror<?> me) {
    if (!(model instanceof Collection)) {
      if (model instanceof Map) {
        model = ((Map)model).values();
      }
      else {
        throw Lang.makeThrow("Not a Collection --> " + model.getClass());
      }
    }
    Collection re = null;
    if (!me.isInterface()) {
      re = (Collection)me.born();
    }
    else {
      re = makeCollection(me);
    }
    if (me.getGenericsTypes() == null) {
      re.addAll((Collection)model);
      return re;
    }
    Type type = me.getGenericsType(0);
    int j = 0;
    for (Object obj : (Collection)model) {
      if (isLeaf(obj)) {
        re.add(Castors.me().castTo(obj, Lang.getTypeClass(type)));
        continue ;
      }
      path.push("a" + (j++));
      re.add(inject(obj, type));
    }
    return re;
  }
  @SuppressWarnings(value = {"rawtypes", }) private Collection makeCollection(Mirror<?> me) {
    if (List.class.isAssignableFrom(me.getType())) {
      return new ArrayList();
    }
    if (Set.class.isAssignableFrom(me.getType())) {
      return new HashSet();
    }
    throw new RuntimeException("\u00e4\u00b8\ufffd\u00e6\u201d\u00af\u00e6\u0152\ufffd\u00e7\u0161\u201e\u00e7\u00b1\u00bb\u00e5\u017e\u2039!");
  }
  @SuppressWarnings(value = {"unchecked", }) private Object injectObj(Object model, Mirror<?> mirror) {
    if (mirror.getType() == Object.class) 
      return model;
    Object obj = mirror.born();
    context.set(fetchPath(), obj);
    Map<String, ?> map = (Map<String, ?>)model;
    JsonEntity jen = Json.getEntity(mirror);
    for (Entry<String, ?> en : map.entrySet()) {
      Object val = en.getValue();
      if (val == null) 
        continue ;
      String key = en.getKey();
      JsonEntityField jef = jen.getField(key);
      if (jef == null) {
        continue ;
      }
      Type jefType = ReflectTool.getInheritGenericType(obj.getClass(), jef.getGenericType());
      if (isLeaf(val)) {
        if (val instanceof El) {
          val = ((El)val).eval(context);
        }
        jef.setValue(obj, Mapl.maplistToObj(val, jefType));
        continue ;
      }
      else {
        path.push(key);
        jef.setValue(obj, Mapl.maplistToObj(val, jefType));
      }
    }
    return obj;
  }
  private static boolean isLeaf(Object obj) {
    if (obj instanceof Map) {
      return false;
    }
    if (obj instanceof List) {
      return false;
    }
    return true;
  }
  private String fetchPath() {
    StringBuffer sb = new StringBuffer();
    sb.append("root");
    for (String item : path) {
      if (item.charAt(0) != 'a') {
        sb.append("m");
      }
      sb.append(item);
    }
    return sb.toString();
  }
}

