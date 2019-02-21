class Test {
    private String getMethodCallback(JMethod method) {
        if (method.getReturnType().isPrimitive() != null) {
            JPrimitiveType primitiveType = method.getReturnType().isPrimitive();
            return "org.fusesource.restygwt.client.MethodCallback<" + primitiveType.getQualifiedBoxedSourceName() + "> callback";
        }
        return "org.fusesource.restygwt.client.MethodCallback<" + method.getReturnType().getParameterizedQualifiedSourceName() + "> callback";
    }
}