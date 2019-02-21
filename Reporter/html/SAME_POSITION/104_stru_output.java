package net.sourceforge.vrapper.vim.commands.motions;
import static java.lang.Math.min;
import static net.sourceforge.vrapper.vim.commands.Utils.isNewLineCharacter;
import net.sourceforge.vrapper.platform.TextContent;
import net.sourceforge.vrapper.utils.Position;
import net.sourceforge.vrapper.vim.EditorAdaptor;
import net.sourceforge.vrapper.vim.commands.BorderPolicy;
import net.sourceforge.vrapper.vim.commands.CommandExecutionException;

public class MoveWordRightForUpdate extends MoveWordRight {
  public static final Motion MOVE_WORD_RIGHT_INSTANCE = new MoveWordRightForUpdate(new MoveWordRight(false));
  public static final Motion MOVE_WORD_RIGHT_INSTANCE_BAILS_OFF = new MoveWordRightForUpdate(new MoveWordRight(true));
  public static final Motion MOVE_BIG_WORD_RIGHT_INSTANCE = new MoveWordRightForUpdate(new MoveBigWORDRight(false));
  public static final Motion MOVE_BIG_WORD_RIGHT_INSTANCE_BAIL_OFF = new MoveWordRightForUpdate(new MoveBigWORDRight(true));
  protected MoveWordRightForUpdate(boolean bailOff) {
    super(bailOff);
  }
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\vrapper\revisions\rev_d392d75_6ed1662\rev_left_d392d75\net.sourceforge.vrapper.core\src\net\sourceforge\vrapper\vim\commands\motions\MoveWordRightForUpdate.java
private MoveWordRightForUpdate(CountAwareMotion delegate) {
    this.delegate = delegate;
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.

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
  public int offsetWithoutLastNewline(int startingIndex, int endingIndex, TextContent content) {
    int bufferLength = min(MoveWithBounds.BUFFER_LEN, endingIndex);
    if (bufferLength == 0) 
      return endingIndex;
    String buffer = content.getText(endingIndex - bufferLength, bufferLength);
    int lastBufferIndex = buffer.length() - 1;
    int trailingWS = numTrailingWhitespaceChars(buffer, lastBufferIndex);
    int trailingNL = numTrailingNewLines(buffer, lastBufferIndex - trailingWS);
    if (trailingNL > 0) {
      int newOffset = endingIndex - (trailingWS + 1);
      if (newOffset > startingIndex) 
        endingIndex = newOffset;
    }
    return endingIndex;
  }
  private int numTrailingWhitespaceChars(String buffer, int endingIndex) {
    int numWS = 0;
    while (endingIndex >= 0 && Character.isWhitespace(buffer.charAt(endingIndex)) && !isNewLineCharacter(buffer.charAt(endingIndex))){
      numWS++;
      endingIndex--;
    }
    return numWS;
  }
  private int numTrailingNewLines(String buffer, int endingIndex) {
    int numWS = 0;
    while (endingIndex >= 0 && isNewLineCharacter(buffer.charAt(endingIndex))){
      numWS++;
      endingIndex--;
    }
    return numWS;
  }
  public boolean isJump() {
    return delegate.isJump();
  }
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
  public int offsetWithoutLastNewline(int startingIndex, int endingIndex, TextContent content) {
    int bufferLength = min(MoveWithBounds.BUFFER_LEN, endingIndex);
    if (bufferLength == 0) 
      return endingIndex;
    String buffer = content.getText(endingIndex - bufferLength, bufferLength);
    int lastBufferIndex = buffer.length() - 1;
    int trailingWS = numTrailingWhitespaceChars(buffer, lastBufferIndex);
    int trailingNL = numTrailingNewLines(buffer, lastBufferIndex - trailingWS);
    if (trailingNL > 0) {
      int newOffset = endingIndex - (trailingWS + 1);
      if (newOffset > startingIndex) 
        endingIndex = newOffset;
    }
    return endingIndex;
  }
  private int numTrailingWhitespaceChars(String buffer, int endingIndex) {
    int numWS = 0;
    while (endingIndex >= 0 && Character.isWhitespace(buffer.charAt(endingIndex)) && !isNewLineCharacter(buffer.charAt(endingIndex))){
      numWS++;
      endingIndex--;
    }
    return numWS;
  }
  private int numTrailingNewLines(String buffer, int endingIndex) {
    int numWS = 0;
    while (endingIndex >= 0 && isNewLineCharacter(buffer.charAt(endingIndex))){
      numWS++;
      endingIndex--;
    }
    return numWS;
  }
}

