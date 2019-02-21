<<<<<<< MINE
return getStreamForSuccessfulRequest(urlConnection);
=======
stream = ContentLengthInputStream.obtain(urlConnection.getInputStream(), contentLength);
>>>>>>> YOURS

