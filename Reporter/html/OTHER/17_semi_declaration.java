public void removeVertex(final Vertex vertex) {
        Vertex vertexToRemove = vertex;
        if (vertex instanceof EventVertex) {
            vertexToRemove = ((EventVertex) vertex).getBaseVertex();
        }

<<<<<<< MINE
        final Map<String, Object> props = ElementHelper.getProperties(vertex);
=======
        Map<String, Object> props = ElementHelper.getProperties(vertex);
>>>>>>> YOURS
        this.baseGraph.removeVertex(vertexToRemove);
        this.onVertexRemoved(vertex, props);
    }

