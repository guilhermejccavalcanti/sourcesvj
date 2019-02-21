@SuppressWarnings("unchecked")
    private Object injectObj(Object model, Mirror<?> mirror) {
        // zzh: ??? Object?????????
        if (mirror.getType() == Object.class)
            return model;
        Object obj = mirror.born();
        context.set(fetchPath(), obj);
        Map<String, ?> map = (Map<String, ?>) model;

        JsonEntity jen = Json.getEntity(mirror);
        for (Entry<String, ?> en : map.entrySet()) {
            Object val = en.getValue();
            if (val == null)
                continue;
            String key = en.getKey();
            JsonEntityField jef = jen.getField(key);
            if (jef == null) {
                continue;
            }
<<<<<<< MINE

            Object val = map.get(jef.getName());
            if (val == null) {
                continue;
            }

            Type jefType = ReflectTool.getInheritGenericType(obj.getClass(), jef.getGenericType());

=======
>>>>>>> YOURS
            if (isLeaf(val)) {
                if (val instanceof El) {
                    val = ((El) val).eval(context);
                }
<<<<<<< MINE
                // zzh@2012-09-14: ???? createBy ?
                // jef.setValue(obj, Castors.me().castTo(jef.createValue(obj,
                // val, null), Lang.getTypeClass(jef.getGenericType())));
                // jef.setValue(obj, jef.createValue(obj, val, null));
                jef.setValue(obj, Mapl.maplistToObj(val, jefType));
=======
                jef.setValue(obj, Mapl.maplistToObj(val, jef.getGenericType()));
>>>>>>> YOURS
                continue;
            } else {
                path.push(key);
<<<<<<< MINE
                // jef.setValue(obj, Mapl.maplistToObj(val,
                // me.getGenericsType(0)));
                jef.setValue(obj, Mapl.maplistToObj(val, jefType));
                // zzh@2012-09-14: ???? createBy ?
                // jef.setValue(obj, jef.createValue(obj, val,
                // me.getGenericsType(0)));
=======
                jef.setValue(obj, Mapl.maplistToObj(val, jef.getGenericType()));
>>>>>>> YOURS
            }
        }
        return obj;
    }

