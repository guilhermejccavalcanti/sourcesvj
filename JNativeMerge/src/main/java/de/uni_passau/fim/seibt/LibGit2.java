/**
 * Copyright (C) 2017 Georg Seibt
 *
 * This file is part of JNativeMerge.
 *
 * JNativeMerge is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JNativeMerge is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JNativeMerge. If not, see <http://www.gnu.org/licenses/>.
 */
package de.uni_passau.fim.seibt;

import java.nio.IntBuffer;
import java.nio.charset.Charset;

import com.sun.jna.Native;

public class LibGit2 {

    private static final String JNA_ENCODING_PROPERTY = "jna.encoding";

    /**
     * Initializes the native libgit2 library.
     * <p>
     * This function must the called before any other libgit2 function in order to set up global state and threading.
     * This function may be called multiple times.
     *
     * @return the number of times the method was called (including this invocation) that have not
     * subsequently been matched by a call to {@link #git_libgit2_shutdown()}
     * @see <a href="https://libgit2.github.com/libgit2/#HEAD/group/libgit2/git_libgit2_shutdown">The libgit2 documentation.</a>
     */
    static native int git_libgit2_init();

    /**
     * Shuts down the native libgit2 library.
     * <p>
     * Clean up the global state and threading context of the after calling it as many times as git_libgit2_init() was
     * called.
     *
     * @return the number of remainining initializations that have not been shutdown (after this one)
     * @see <a href="https://libgit2.github.com/libgit2/#HEAD/type/git_merge_file_options">The libgit2 documentation.</a>
     */
    static native int git_libgit2_shutdown();

    /**
     * Return the version of the libgit2 library.
     *
     * @param major
     *         an {@link IntBuffer} of size 1 to store the major version number in
     * @param minor
     *         an {@link IntBuffer} of size 1 to store the minor version number in
     * @param rev
     *         an {@link IntBuffer} of size 1 to store the revision (patch) version number in
     * @see <a href="https://libgit2.github.com/libgit2/#HEAD/group/libgit2/git_libgit2_version">The libgit2 documentation.</a>
     */
    static native void git_libgit2_version(IntBuffer major, IntBuffer minor, IntBuffer rev);

    /**
     * Returns the version of the libgit2 library.
     *
     * @return the version information
     */
    public static LibGit2Version git_libgit2_version() {
        IntBuffer major = IntBuffer.allocate(1), minor = IntBuffer.allocate(1), rev = IntBuffer.allocate(1);
        git_libgit2_version(major, minor, rev);

        return new LibGit2Version(major.get(0), minor.get(0), rev.get(0));
    }

    /**
     * Merge two files as they exist in the in-memory data structures, using the given common ancestor as the baseline,
     * producing a {@link GitMergeFileResult} that reflects the merge result. The {@link GitMergeFileResult} must be
     * freed with git_merge_file_result_free. TODO link correct free function or free automatically
     *
     * @param out
     *         the {@link GitMergeFileResult} to be filled in
     * @param ancestor
     *         the {@link GitMergeFileInput} for the ancestor file
     * @param ours
     *         the {@link GitMergeFileInput} for the file in "our" side
     * @param theirs
     *         the {@link GitMergeFileInput} for the file in "their" side
     * @param opts
     *         the {@link GitMergeFileOptions} or {@code null} for defaults
     * @return 0 on success or error code
     */
    public static native int git_merge_file(GitMergeFileResult out, GitMergeFileInput ancestor, GitMergeFileInput ours,
                                            GitMergeFileInput theirs, GitMergeFileOptions opts);

    static {
        Native.register("git2");

        git_libgit2_init();
        Runtime.getRuntime().addShutdownHook(new Thread(LibGit2::git_libgit2_shutdown));
    }

    /**
     * Returns the {@link Charset} that would be used by JNA to encode / decode {@link String Strings}.
     *
     * @return the {@link Charset} used by JNA
     */
    static Charset getJNACharset() {
        String csName = System.getProperty(JNA_ENCODING_PROPERTY);
        Charset cs;

        if (csName != null && Charset.isSupported(csName)) {
            cs = Charset.forName(csName);
        } else {
            cs = Charset.defaultCharset();
        }

        return cs;
    }
}
