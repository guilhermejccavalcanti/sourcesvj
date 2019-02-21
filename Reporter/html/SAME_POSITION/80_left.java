package org.nutz.ioc.impl;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Disks;
import org.nutz.lang.util.FileVisitor;
import org.nutz.lang.util.MultiLineProperties;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.resource.NutResource;
import org.nutz.resource.Scans;

/**
 * ??Properties??,?????Ioc???????
 * 
 * @author wendal(wendal1985@gmail.com)
 * @author zozoh(zozohtnt@gmail.com)
 * 
 * @since 1.b.37
 */
public class PropertiesProxy {

    private static final Log log = Logs.get();

    // ???UTF8???Properties??
    private final boolean utf8;
    // ???????????
    private boolean ignoreResourceNotFound = false;

    private MultiLineProperties mp = new MultiLineProperties();

    public PropertiesProxy() {
        this(true);
    }

    public PropertiesProxy(boolean utf8) {
        this.utf8 = utf8;
    }

    public PropertiesProxy(String... paths) {
        this(true);
        this.setPaths(paths);
    }

    public PropertiesProxy(InputStream in) {
        this(true);
        try {
            mp = new MultiLineProperties();
            mp.load(new InputStreamReader(in));
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e);
        }
    }

    /**
     * @param r
     *            ?????
     * @since 1.b.50
     */
    public PropertiesProxy(Reader r) {
        this(true);
        try {
            mp = new MultiLineProperties();
            mp.load(r);
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e);
        }
    }

    /**
     * ??????/????Properties??,?????Properties??
     * <p>
     * <b style=color:red>??????key,??????????!!<b/>
     * 
     * @param paths
     *            ?????Properties????
     */
    public void setPaths(String... paths) {
        mp = new MultiLineProperties();

        try {
            List<NutResource> list = getResources(paths);
            if (utf8)
                for (NutResource nr : list)
                    mp.load(nr.getReader(), false);
            else {
                Properties p = new Properties();
                for (NutResource nr : list) {
                    p.load(nr.getInputStream());
                }
                mp.putAll(p);
            }
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e);
        }
    }

    /**
     * ??????/????Properties??
     * 
     * @param paths
     *            ?????Properties????
     * @return ????Properties??Resource??
     */
    private List<NutResource> getResources(String... paths) {
        List<NutResource> list = new ArrayList<NutResource>();
        for (String path : paths) {
            try {
                List<NutResource> resources = Scans.me().loadResource("^.+[.]properties$", path);
                list.addAll(resources);
            }
            catch (Exception e) {
                if (ignoreResourceNotFound) {
                    if (log.isWarnEnabled()) {
                        log.warn("Could not load resource from " + path + ": " + e.getMessage());
                    }
                } else {
                    throw Lang.wrapThrow(e);
                }
            }
        }
        return list;
    }

    public void setIgnoreResourceNotFound(boolean ignoreResourceNotFound) {
        this.ignoreResourceNotFound = ignoreResourceNotFound;
    }

    /**
     * @param key
     *            ?
     * @return ???????
     * @since 1.b.50
     */
    public boolean has(String key) {
        return mp.containsKey(key);
    }

    public void put(String key, String value) {
        mp.put(key, value);
    }

    public String get(String key) {
        return mp.get(key);
    }

    public String get(String key, String defaultValue) {
        return Strings.sNull(mp.get(key), defaultValue);
    }

    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        try {
            return Boolean.parseBoolean(get(key));
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    public String trim(String key) {
        return Strings.trim(get(key));
    }

    public String trim(String key, String defaultValue) {
        return Strings.trim(get(key, defaultValue));
    }

    public int getInt(String key) {
        return getInt(key, -1);
    }

    public int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(get(key));
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public long getLong(String key) {
        return getLong(key, -1);
    }

    public long getLong(String key, long dfval) {
        try {
            return Long.parseLong(get(key));
        }
        catch (NumberFormatException e) {
            return dfval;
        }
    }

    public String getTrim(String key) {
        return Strings.trim(get(key));
    }

    public String getTrim(String key, String defaultValue) {
        return Strings.trim(get(key, defaultValue));
    }

    public List<String> getKeys() {
        return mp.keys();
    }

    public Collection<String> getValues() {
        return mp.values();
    }

    public Properties toProperties() {
        Properties p = new Properties();
        for (String key : mp.keySet()) {
            p.put(key, mp.get(key));
        }
        return p;
    }

    /**
     * ????????????????????
     * <p>
     * ??????????????:
     * 
     * <pre>
     * ...
     * files:
     * path/to_a.properties
     * path/to_b.properties
     * #End files
     * </pre>
     * 
     * ??????? <code>joinByKey("files");</code> <br>
     * ?????????????????????
     * <p>
     * ??????????????????? CLASSPATH ????
     * 
     * @param key
     *            ?
     * @return ??
     */
    public PropertiesProxy joinByKey(String key) {
        String str = get(key);
        final PropertiesProxy me = this;
        if (!Strings.isBlank(str)) {
            String[] ss = Strings.splitIgnoreBlank(str, "\n");
            for (String s : ss) {
                File f = Files.findFile(s);
                if (null == f) {
                    throw Lang.makeThrow("Fail to found path '%s' in CLASSPATH or File System!", s);
                }
                // ??????????? Files
                if (f.isDirectory()) {
                    Disks.visitFile(f, new FileVisitor() {
                        public void visit(File f) {
                            me.joinAndClose(Streams.fileInr(f));
                        }
                    }, new FileFilter() {
                        public boolean accept(File f) {
                            if (f.isDirectory())
                                return !f.isHidden() && !f.getName().startsWith(".");
                            return f.getName().endsWith(".properties");
                        }
                    });
                }
                // ????????
                else if (f.isFile()) {
                    this.joinAndClose(Streams.fileInr(f));
                }
            }
        }
        return this;
    }

    /**
     * ????? Properties ????????
     * 
     * @param r
     *            ?????
     * @return ??
     */
    public PropertiesProxy joinAndClose(Reader r) {
        MultiLineProperties mp = new MultiLineProperties();
        try {
            mp.load(r);
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e);
        }
        finally {
            Streams.safeClose(r);
        }
        this.mp.putAll(mp);
        return this;
    }

    public Map<String, String> toMap() {
        return new HashMap<String, String>(mp);
    }
}
