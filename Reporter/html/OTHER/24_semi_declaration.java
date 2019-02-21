static JCMethodDecl createConstructor(AccessLevel level, List<JCAnnotation> onConstructor, JavacNode typeNode, List<JavacNode> fields, boolean suppressConstructorProperties, JCTree source) {
		JavacTreeMaker maker = typeNode.getTreeMaker();
		
		boolean isEnum = (((JCClassDecl) typeNode.get()).mods.flags & Flags.ENUM) != 0;
		if (isEnum) level = AccessLevel.PRIVATE;
		
		ListBuffer<JCStatement> nullChecks = ListBuffer.lb();
		ListBuffer<JCStatement> assigns = ListBuffer.lb();
		ListBuffer<JCVariableDecl> params = ListBuffer.lb();
		
		for (JavacNode fieldNode : fields) {
			JCVariableDecl field = (JCVariableDecl) fieldNode.get();
			Name fieldName = removePrefixFromField(fieldNode);
			Name rawName = field.name;
			List<JCAnnotation> nonNulls = findAnnotations(fieldNode, TransformationsUtil.NON_NULL_PATTERN);
			List<JCAnnotation> nullables = findAnnotations(fieldNode, TransformationsUtil.NULLABLE_PATTERN);
<<<<<<< MINE
			JCVariableDecl param = maker.VarDef(maker.Modifiers(Flags.FINAL | Flags.PARAMETER, nonNulls.appendList(nullables)), field.name, field.vartype, null);
=======
			JCVariableDecl param = maker.VarDef(maker.Modifiers(Flags.FINAL, nonNulls.appendList(nullables)), fieldName, field.vartype, null);
>>>>>>> YOURS
			params.append(param);
			JCFieldAccess thisX = maker.Select(maker.Ident(fieldNode.toName("this")), rawName);
			JCAssign assign = maker.Assign(thisX, maker.Ident(fieldName));
			assigns.append(maker.Exec(assign));
			
			if (!nonNulls.isEmpty()) {
				JCStatement nullCheck = generateNullCheck(maker, fieldNode);
				if (nullCheck != null) nullChecks.append(nullCheck);
			}
		}
		
		JCModifiers mods = maker.Modifiers(toJavacModifier(level), List.<JCAnnotation>nil());
		if (!suppressConstructorProperties && level != AccessLevel.PRIVATE && !isLocalType(typeNode)) {
			addConstructorProperties(mods, typeNode, fields);
		}
		if (onConstructor != null) mods.annotations = mods.annotations.appendList(copyAnnotations(onConstructor));
		
		return recursiveSetGeneratedBy(maker.MethodDef(mods, typeNode.toName("<init>"),
				null, List.<JCTypeParameter>nil(), params.toList(), List.<JCExpression>nil(), maker.Block(0L, nullChecks.appendList(assigns).toList()), null), source);
	}

