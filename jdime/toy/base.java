class Test {
    private String getMethodCallback(JMethod method) {
        if (isVoidMethod(method)) {
            return "org.fusesource.restygwt.client.MethodCallback<java.lang.Void> callback";
        }
        return "org.fusesource.restygwt.client.MethodCallback<" + method.getReturnType().getParameterizedQualifiedSourceName() + "> callback";
    }
}