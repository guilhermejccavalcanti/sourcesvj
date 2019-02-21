package org.nutz.lang;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.Charset;

public final class Encoding {
  public static final String UTF8 = "UTF-8";
  public static final String GBK = "GBK";
  public static final String GB2312 = "GB2312";
  public static final String ASCII = "US-ASCII";
  public static final String ISO_8859_1 = "ISO-8859-1";
  public static final String UTF16BE = "UTF-16BE";
  public static final String UTF16LE = "UTF-16LE";
  public static final String UTF16 = "UTF-16";
  public static final Charset CHARSET_UTF8 = Charset.forName(UTF8);
  public static final Charset CHARSET_GBK = Charset.forName(GBK);
  public static final Charset CHARSET_GB2312 = Charset.forName(GB2312);
  public static final Charset CHARSET_ASCII = Charset.forName(ASCII);
  public static final Charset CHARSET_ISO_8859_1 = Charset.forName(ISO_8859_1);
  public static final Charset CHARSET_UTF16 = Charset.forName(UTF16);
  public static final Charset CHARSET_UTF16BE = Charset.forName(UTF16BE);
  public static final Charset CHARSET_UTF16LE = Charset.forName(UTF16LE);
  public static String defaultEncoding() {
    return Charset.defaultCharset().name();
  }
  public static String encodeURIComponent(
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\nutz\revisions\rev_6b80c32_b447a8a\rev_left_6b80c32\src\org\nutz\lang\Encoding.java
String str
=======
String postStr
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\nutz\revisions\rev_6b80c32_b447a8a\rev_right_b447a8a\src\org\nutz\lang\Encoding.java
) {
    try {
      return URLEncoder.encode(
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\nutz\revisions\rev_6b80c32_b447a8a\rev_left_6b80c32\src\org\nutz\lang\Encoding.java
str
=======
postStr
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\nutz\revisions\rev_6b80c32_b447a8a\rev_right_b447a8a\src\org\nutz\lang\Encoding.java
, 
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\nutz\revisions\rev_6b80c32_b447a8a\rev_left_6b80c32\src\org\nutz\lang\Encoding.java
"UTF-8"
=======
defaultEncoding()
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\nutz\revisions\rev_6b80c32_b447a8a\rev_right_b447a8a\src\org\nutz\lang\Encoding.java
);
    }
    catch (UnsupportedEncodingException e) {
      throw Lang.wrapThrow(e);
    }
  }
  public static String decodeURIComponent(String str) {
    try {
      return URLDecoder.decode(str, "UTF-8");
    }
    catch (UnsupportedEncodingException e) {
      throw Lang.wrapThrow(e);
    }
  }
}

