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

import java.nio.charset.Charset;
import java.util.List;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * The file inputs to `git_merge_file`. Callers should populate the `git_merge_file_input` structure with descriptions
 * of the files in each side of the conflict for use in producing the merge file.
 *
 * @see <a href="https://libgit2.github.com/libgit2/#HEAD/type/git_merge_file_input">The libgit2 documentation.</a>
 */
public class GitMergeFileInput extends Structure {

    private static final int GIT_MERGE_FILE_INPUT_VERSION = 1;

    public int version = GIT_MERGE_FILE_INPUT_VERSION;

    /**
     * The file contents. The {@link #setContent(String)} method should be used to set this field!
     */
    public Pointer content;

    /**
     * The size in bytes of the file contents. Do not set this field manually!
     */
    public int size;

    /**
     * File name of the conflicted file. Set to {@code null} to not merge the path.
     */
    public String path;

    /**
     * File mode of the conflicted file, or '0' to not merge the mode.
     */
    public int mode;

    /**
     * Sets the file content to the given value. The associated {@link #size} will be calculated using the
     * {@link Charset} that would be used by the JNA library.
     *
     * @param content the file contents
     */
    public void setContent(String content) {
        setContent(content, LibGit2.getJNACharset());
    }

    /**
     * Sets the file content to the given value. The associated {@link #size} will be calculated using the given
     * {@link Charset}.
     *
     * @param content the file contents
     */
    public void setContent(String content, Charset charset) {

        if (content.isEmpty()) {
            this.content = Pointer.NULL;
            return;
        }

        byte[] bytes = content.getBytes(charset);

        if (this.content == null || this.size < bytes.length) {
            this.content = new Memory(bytes.length);
        }

        this.content.write(0, bytes, 0, bytes.length);
        this.size = bytes.length;
    }

    @Override
    protected List<String> getFieldOrder() {
        return Structure.createFieldsOrder("version", "content", "size", "path", "mode");
    }
}
