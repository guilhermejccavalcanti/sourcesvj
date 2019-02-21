<<<<<<< MINE
v = vertexCache.get(vertexid, externalVertexRetriever);
=======
try {
      v = vertexCache.get(vertexid, externalVertexRetriever);
    }
    catch (InvalidIDException e) {
      log.debug("Illegal vertex ID", e);
    }
>>>>>>> YOURS

