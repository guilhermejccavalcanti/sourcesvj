  @Override public Position destination(EditorAdaptor editorAdaptor, int count) throws CommandExecutionException {
    int originalOffset = editorAdaptor.getPosition().getModelOffset();
    Position parentPosition = super.destination(editorAdaptor, count);
    int newOffset = 
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\vrapper\revisions\rev_d392d75_6ed1662\rev_left_d392d75\net.sourceforge.vrapper.core\src\net\sourceforge\vrapper\vim\commands\motions\MoveWordRightForUpdate.java
offsetWithoutLastNewline(originalOffset, delegatePosition.getModelOffset(), editorAdaptor.getModelContent())
=======
MoveWordRightUtils.offsetWithoutLastNewline(originalOffset, parentPosition.getModelOffset(), editorAdaptor.getModelContent())
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\vrapper\revisions\rev_d392d75_6ed1662\rev_right_6ed1662\net.sourceforge.vrapper.core\src\net\sourceforge\vrapper\vim\commands\motions\MoveWordRightForUpdate.java
;
    return editorAdaptor.getCursorService().newPositionForModelOffset(newOffset);
  }


