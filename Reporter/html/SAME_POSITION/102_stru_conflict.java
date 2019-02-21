<<<<<<< MINE
for (Object type : cuUnit.types()) {
        org.eclipse.jdt.core.dom.AbstractTypeDeclaration typeDeclaration = (AbstractTypeDeclaration)type;
        if ((typeDeclaration.getName() + ".java").equals(typeName)) {
          for (Object declaration : typeDeclaration.bodyDeclarations()) {
            if (declaration instanceof org.eclipse.jdt.core.dom.MethodDeclaration) {
              org.eclipse.jdt.core.dom.MethodDeclaration methodDeclaration = (org.eclipse.jdt.core.dom.MethodDeclaration)declaration;
              if (methodDeclaration.getName().toString().equals(methodName)) {
                return methodDeclaration;
              }
            }
          }
        }
      }
=======
while (declaringType != null){
        typeStack.push(declaringType);
        declaringType = declaringType.getDeclaringType();
      }
>>>>>>> YOURS

