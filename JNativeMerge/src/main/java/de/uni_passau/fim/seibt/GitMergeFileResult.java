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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * Information about file-level merging
 *
 * @see <a href="https://libgit2.github.com/libgit2/#HEAD/type/git_merge_file_result">The libgit2 documentation.</a>
 */
public class GitMergeFileResult extends Structure {

    public int automergeable;

    public String path;
    public int mode;

    /**
     * The results of the merge. Use the {@link #getResult()} method to retrieve it as a {@link String}.
     */
    public Pointer ptr;
    public int len;

    /**
     * Whether the output was automerged.
     *
     * @return {@code true} if the output was automerged, {@code false} if the output contains conflict markers
     */
    public boolean automerged() {
        return automergeable != 0;
    }

    /**
     * The path that the resultant merge file should use, or an empty {@link Optional} if a filename conflict would occur.
     *
     * @return the path
     */
    public Optional<Path> getPath() {
        return Optional.ofNullable(path).map(Paths::get);
    }

    /**
     * The mode that the resultant merge file should use.
     *
     * @return the file mode
     */
    public int getMode() {
        return mode;
    }

    /**
     * Returns the results of the merge as a {@link String}. The encoding that JNA would choose will be used to
     * turn bytes into characters.
     *
     * @return the result of the merge
     */
    public String getResult() {
        return getResult(LibGit2.getJNACharset());
    }

    /**
     * Returns the resutls of the merge as a {@link String}. The given {@link Charset} will be used to turn bytes into
     * characters.
     *
     * @param cs
     *         the {@link Charset} to be used
     * @return the results of the merge
     */
    public String getResult(Charset cs) {
        return new String(ptr.getByteArray(0, len), cs).intern();
    }

    @Override
    protected List<String> getFieldOrder() {
        return Structure.createFieldsOrder("automergeable", "path", "mode", "ptr", "len");
    }
}
