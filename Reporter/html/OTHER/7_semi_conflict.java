<<<<<<< MINE
        this(connectionString, true);
||||||| BASE
        this.connectionString = Preconditions.checkNotNull(connectionString, "connectionString cannot be null");
=======
        Preconditions.checkArgument(!Strings.isNullOrEmpty(connectionString),
            "connectionString cannot be null or empty");
        this.connectionString = connectionString;
>>>>>>> YOURS

