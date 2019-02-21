  public static org.eclipse.jdt.core.dom.MethodDeclaration getRealMethodDeclarationNode(org.eclipse.jdt.core.IMethod sourceMethod, org.eclipse.jdt.core.dom.CompilationUnit cuUnit) throws JavaModelException {
    MethodDeclaration methodDeclarationNode = ASTNodeSearchUtil.getMethodDeclarationNode(sourceMethod, cuUnit);
    if (isGenerated(methodDeclarationNode)) {
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\lombok\revisions\rev_3027618_bf354e3\rev_left_3027618\src\eclipseAgent\lombok\eclipse\agent\PatchFixes.java
String typeName = sourceMethod.getTypeRoot().getElementName();
=======
IType declaringType = sourceMethod.getDeclaringType();
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\lombok\revisions\rev_3027618_bf354e3\rev_right_bf354e3\src\eclipseAgent\lombok\eclipse\agent\PatchFixes.java

<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\lombok\revisions\rev_3027618_bf354e3\rev_left_3027618\src\eclipseAgent\lombok\eclipse\agent\PatchFixes.java
String methodName = sourceMethod.getElementName();
=======
Stack<IType> typeStack = new Stack<IType>();
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\lombok\revisions\rev_3027618_bf354e3\rev_right_bf354e3\src\eclipseAgent\lombok\eclipse\agent\PatchFixes.java

<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\lombok\revisions\rev_3027618_bf354e3\rev_left_3027618\src\eclipseAgent\lombok\eclipse\agent\PatchFixes.java
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
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\lombok\revisions\rev_3027618_bf354e3\rev_right_bf354e3\src\eclipseAgent\lombok\eclipse\agent\PatchFixes.java

    }
    return methodDeclarationNode;
  }


