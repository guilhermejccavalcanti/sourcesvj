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

import java.util.List;
import java.util.Optional;

import com.sun.jna.Structure;

/**
 * Options for merging a file
 *
 * @see <a href="https://libgit2.github.com/libgit2/#HEAD/type/git_merge_file_options">The libgit2 documentation.</a>
 */
public class GitMergeFileOptions extends Structure {

    private static final int GIT_MERGE_FILE_INPUT_VERSION = 1;

    public int version = GIT_MERGE_FILE_INPUT_VERSION;

    /**
     * Label for the ancestor file side of the conflict which will be prepended to labels in diff3-format merge files.
     */
    public String ancestor_label;

    /**
     * Label for our file side of the conflict which will be prepended to labels in merge files.
     */
    public String our_label;

    /**
     * Label for their file side of the conflict which will be prepended to labels in merge files.
     */
    public String their_label;

    /**
     * The file to favor in region conflicts. Use the {@link #setFavor(Favor)} method to set this field!
     */
    public int favor;

    /**
     * File merging flags. Use the {@link #setFlags(Flag)} method to set this field!
     */
    public int flags;

    /**
     * File merging flags
     */
    public enum Flag {

        /** Defaults */
        GIT_MERGE_FILE_DEFAULT(0),

        /** Create standard conflicted merge files */
        GIT_MERGE_FILE_STYLE_MERGE(1 << 0),

        /** Create diff3-style files */
        GIT_MERGE_FILE_STYLE_DIFF3(1 << 1),

        /** Condense non-alphanumeric regions for simplified diff file */
        GIT_MERGE_FILE_SIMPLIFY_ALNUM(1 << 2),

        /** Ignore all whitespace */
        GIT_MERGE_FILE_IGNORE_WHITESPACE(1 << 3),

        /** Ignore changes in amount of whitespace */
        GIT_MERGE_FILE_IGNORE_WHITESPACE_CHANGE(1 << 4),

        /** Ignore whitespace at end of line */
        GIT_MERGE_FILE_IGNORE_WHITESPACE_EOL(1 << 5),

        /** Use the "patience diff" algorithm */
        GIT_MERGE_FILE_DIFF_PATIENCE(1 << 6),

        /** Take extra time to find minimal diff */
        GIT_MERGE_FILE_DIFF_MINIMAL(1 << 7);

        private int id;

        Flag(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static Optional<Flag> forId(int id) {

            switch (id) {
                case (0):
                    return Optional.of(GIT_MERGE_FILE_DEFAULT);
                case (1 << 0):
                    return Optional.of(GIT_MERGE_FILE_STYLE_MERGE);
                case (1 << 1):
                    return Optional.of(GIT_MERGE_FILE_STYLE_DIFF3);
                case (1 << 2):
                    return Optional.of(GIT_MERGE_FILE_SIMPLIFY_ALNUM);
                case (1 << 3):
                    return Optional.of(GIT_MERGE_FILE_IGNORE_WHITESPACE);
                case (1 << 4):
                    return Optional.of(GIT_MERGE_FILE_IGNORE_WHITESPACE_CHANGE);
                case (1 << 5):
                    return Optional.of(GIT_MERGE_FILE_IGNORE_WHITESPACE_EOL);
                case (1 << 6):
                    return Optional.of(GIT_MERGE_FILE_DIFF_PATIENCE);
                case (1 << 7):
                    return Optional.of(GIT_MERGE_FILE_DIFF_MINIMAL);
            }

            return Optional.empty();
        }
    }

    /**
     * Merge file favor options for `git_merge_options` instruct the file-level
     * merging functionality how to deal with conflicting regions of the files.
     */
    public enum Favor {

        /**
         * When a region of a file is changed in both branches, a conflict
         * will be recorded in the index so that `git_checkout` can produce
         * a merge file with conflict markers in the working directory.
         * This is the default.
         */
        GIT_MERGE_FILE_FAVOR_NORMAL(0),

        /**
         * When a region of a file is changed in both branches, the file
         * created in the index will contain the "ours" side of any conflicting
         * region.  The index will not record a conflict.
         */
        GIT_MERGE_FILE_FAVOR_OURS(1),

        /**
         * When a region of a file is changed in both branches, the file
         * created in the index will contain the "theirs" side of any conflicting
         * region.  The index will not record a conflict.
         */
        GIT_MERGE_FILE_FAVOR_THEIRS(2),

        /**
         * When a region of a file is changed in both branches, the file
         * created in the index will contain each unique line from each side,
         * which has the result of combining both files.  The index will not
         * record a conflict.
         */
        GIT_MERGE_FILE_FAVOR_UNION(3);

        private int id;

        Favor(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static Optional<Favor> forId(int id) {

            switch (id) {
                case 0:
                    return Optional.of(GIT_MERGE_FILE_FAVOR_NORMAL);
                case 1:
                    return Optional.of(GIT_MERGE_FILE_FAVOR_OURS);
                case 2:
                    return Optional.of(GIT_MERGE_FILE_FAVOR_THEIRS);
                case 3:
                    return Optional.of(GIT_MERGE_FILE_FAVOR_UNION);
            }

            return Optional.empty();
        }
    }

    public Favor getFavor() {
        return Favor.forId(favor).get();
    }

    public void setFavor(Favor favor) {
        this.favor = favor.getId();
    }

    public Flag getFlags() {
        return Flag.forId(flags).get();
    }

    public void setFlags(Flag flags) {
        this.flags = flags.getId();
    }

    @Override
    protected List<String> getFieldOrder() {
        return Structure.createFieldsOrder("version", "ancestor_label", "our_label", "their_label", "favor", "flags");
    }
}
