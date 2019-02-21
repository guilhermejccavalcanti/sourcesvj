  public MessageFormatSend(MessageReadSet readSet, MessageFormatFlags flag, MessageFormatMetrics metrics) throws IOException, MessageFormatException {
    this.readSet = readSet;
    this.flag = flag;
    totalSizeToWrite = 0;
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\ambry\revisions\rev_2cb49af_4a6a7fc\rev_left_2cb49af\ambry-messageformat\src\main\java\com.github.ambry.messageformat\MessageFormatSend.java
long startTime = SystemTime.getInstance().milliseconds();
=======
this.metrics = metrics;
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\ambry\revisions\rev_2cb49af_4a6a7fc\rev_right_4a6a7fc\ambry-messageformat\src\main\java\com.github.ambry.messageformat\MessageFormatSend.java

    calculateOffsets();
    metrics.calculateOffsetMessageSendTime.update(SystemTime.getInstance().milliseconds() - startTime);
    sizeWritten = 0;
    currentWriteIndex = 0;
    sizeWrittenFromCurrentIndex = 0;
  }


