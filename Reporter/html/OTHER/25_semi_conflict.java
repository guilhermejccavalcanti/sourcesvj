<<<<<<< MINE
			JCVariableDecl param = maker.VarDef(maker.Modifiers(Flags.FINAL | Flags.PARAMETER, nonNulls.appendList(nullables)), field.name, pType, null);
||||||| BASE
			JCVariableDecl param = maker.VarDef(maker.Modifiers(Flags.FINAL, nonNulls.appendList(nullables)), field.name, pType, null);
=======
			JCVariableDecl param = maker.VarDef(maker.Modifiers(Flags.FINAL, nonNulls.appendList(nullables)), fieldName, pType, null);
>>>>>>> YOURS

