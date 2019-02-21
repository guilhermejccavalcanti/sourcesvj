<<<<<<< MINE
			JCVariableDecl param = maker.VarDef(maker.Modifiers(Flags.FINAL | Flags.PARAMETER, nonNulls.appendList(nullables)), field.name, field.vartype, null);
||||||| BASE
			JCVariableDecl param = maker.VarDef(maker.Modifiers(Flags.FINAL, nonNulls.appendList(nullables)), field.name, field.vartype, null);
=======
			JCVariableDecl param = maker.VarDef(maker.Modifiers(Flags.FINAL, nonNulls.appendList(nullables)), fieldName, field.vartype, null);
>>>>>>> YOURS

