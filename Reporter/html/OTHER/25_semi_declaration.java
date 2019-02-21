private JCMethodDecl createStaticConstructor(String name, AccessLevel level, JavacNode typeNode, List<JavacNode> fields, JCTree source) {
		JavacTreeMaker maker = typeNode.getTreeMaker();
		JCClassDecl type = (JCClassDecl) typeNode.get();
		
		JCModifiers mods = maker.Modifiers(Flags.STATIC | toJavacModifier(level));
		
		JCExpression returnType, constructorType;
		
		ListBuffer<JCTypeParameter> typeParams = ListBuffer.lb();
		ListBuffer<JCVariableDecl> params = ListBuffer.lb();
		ListBuffer<JCExpression> typeArgs1 = ListBuffer.lb();
		ListBuffer<JCExpression> typeArgs2 = ListBuffer.lb();
		ListBuffer<JCExpression> args = ListBuffer.lb();
		
		if (!type.typarams.isEmpty()) {
			for (JCTypeParameter param : type.typarams) {
				typeArgs1.append(maker.Ident(param.name));
				typeArgs2.append(maker.Ident(param.name));
				typeParams.append(maker.TypeParameter(param.name, param.bounds));
			}
			returnType = maker.TypeApply(maker.Ident(type.name), typeArgs1.toList());
			constructorType = maker.TypeApply(maker.Ident(type.name), typeArgs2.toList());
		} else {
			returnType = maker.Ident(type.name);
			constructorType = maker.Ident(type.name);
		}
		
		for (JavacNode fieldNode : fields) {
			JCVariableDecl field = (JCVariableDecl) fieldNode.get();
			Name fieldName = removePrefixFromField(fieldNode);
			JCExpression pType = cloneType(maker, field.vartype, source);
			List<JCAnnotation> nonNulls = findAnnotations(fieldNode, TransformationsUtil.NON_NULL_PATTERN);
			List<JCAnnotation> nullables = findAnnotations(fieldNode, TransformationsUtil.NULLABLE_PATTERN);
<<<<<<< MINE
			JCVariableDecl param = maker.VarDef(maker.Modifiers(Flags.FINAL | Flags.PARAMETER, nonNulls.appendList(nullables)), field.name, pType, null);
=======
			JCVariableDecl param = maker.VarDef(maker.Modifiers(Flags.FINAL, nonNulls.appendList(nullables)), fieldName, pType, null);
>>>>>>> YOURS
			params.append(param);
			args.append(maker.Ident(fieldName));
		}
		JCReturn returnStatement = maker.Return(maker.NewClass(null, List.<JCExpression>nil(), constructorType, args.toList(), null));
		JCBlock body = maker.Block(0, List.<JCStatement>of(returnStatement));
		
		return recursiveSetGeneratedBy(maker.MethodDef(mods, typeNode.toName(name), returnType, typeParams.toList(), params.toList(), List.<JCExpression>nil(), body, null), source);
	}

