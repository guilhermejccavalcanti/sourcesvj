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

import java.util.Comparator;

/**
 * The version information of the libgit2 native library.
 */
public final class LibGit2Version implements Comparable<LibGit2Version> {

    private static final Comparator<LibGit2Version> cmp = Comparator.comparingInt(LibGit2Version::getMajor)
                                                                    .thenComparingInt(LibGit2Version::getMinor)
                                                                    .thenComparingInt(LibGit2Version::getPatch);

    public final int major;
    public final int minor;
    public final int patch;

    LibGit2Version(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    /**
     * Returns the major version number.
     *
     * @return the major version number
     */
    public int getMajor() {
        return major;
    }

    /**
     * Returns the minor version number.
     *
     * @return the minor version number
     */
    public int getMinor() {
        return minor;
    }

    /**
     * Returns the patch version number (aka revision).
     *
     * @return the patch version number
     */
    public int getPatch() {
        return patch;
    }

    @Override
    public int compareTo(LibGit2Version o) {
        return cmp.compare(this, o);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LibGit2Version that = (LibGit2Version) o;

        if (major != that.major) {
            return false;
        }

        if (minor != that.minor) {
            return false;
        }

        return patch == that.patch;
    }

    @Override
    public int hashCode() {
        int result = major;
        result = 31 * result + minor;
        result = 31 * result + patch;
        return result;
    }

    @Override
    public String toString() {
        return String.format("LibGit2Version{major=%d, minor=%d, patch=%d}", major, minor, patch);
    }
}
