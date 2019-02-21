<<<<<<< MINE
this.start = this.start + this.graph.getBufferSize();
=======
if (this.queue.size() == bufferSize) {
      this.start = this.start + bufferSize;
      this.end = this.end + bufferSize;
    }
    else {
      this.start = this.end;
    }
>>>>>>> YOURS

