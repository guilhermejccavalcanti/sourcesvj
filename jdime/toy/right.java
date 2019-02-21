class Test {
    private String getMethodCallback(JMethod method) {
        if (isVoidMethod(method)) {
            return "org.fusesource.restygwt.client.MethodCallback<java.lang.Void> callback";
        }
        final String returnType = method.getReturnType().getParameterizedQualifiedSourceName();
        if (isOverlayMethod(method)) {
            return "org.fusesource.restygwt.client.OverlayCallback<" + returnType + "> callback";
        } else {
            return "org.fusesource.restygwt.client.MethodCallback<" + returnType + "> callback";
        }
    }
}