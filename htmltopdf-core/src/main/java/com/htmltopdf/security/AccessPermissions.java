/*
 * This file is part of HTML To PDF Generator.
 *
 * Copyright (C) 2025 Gregory Echelberry
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.htmltopdf.security;

/**
 * Class that sets the access permissions for the generated PDF
 */
public class AccessPermissions {
    private static final int DEFAULT_PERMISSIONS = ~3;
    private static final int PRINT_BIT = 3;
    private static final int MODIFICATION_BIT = 4;
    private static final int EXTRACT_BIT = 5;
    private static final int MODIFY_ANNOTATIONS_BIT = 6;
    private static final int FILL_IN_FORM_BIT = 9;
    private static final int EXTRACT_FOR_ACCESSIBILITY_BIT = 10;
    private static final int ASSEMBLE_DOCUMENT_BIT = 11;
    private static final int FAITHFUL_PRINT_BIT = 12;

    private int bytes;

    /**
     * Constructor - Default permissions
     */
    public AccessPermissions() {
        bytes = DEFAULT_PERMISSIONS;
    }

    /**
     * Constructor - Sets permissions
     * @param permissions int of the permissions
     */
    public AccessPermissions(int permissions) {
        bytes = permissions;
    }

    /**
     * Get the permissions for use in the encryption algorithm
     * @return int of the permissions
     */
    public int getPermissionsBytesForPublicKey() {
        setPermissionBit(1, true);
        setPermissionBit(7, false);
        setPermissionBit(8, false);
        for (int i = 13; i <= 32; i++) {
            setPermissionBit(i, false);
        }

        return bytes;
    }

    /**
     * Sets if printing is allowed
     * @param allowPrinting boolean allow printing
     */
    public void setCanPrint(boolean allowPrinting) {
        setPermissionBit(PRINT_BIT, allowPrinting);
    }

    /**
     * Sets if modifications are allowed
     * @param allowModifications boolean allow modifications
     */
    public void setCanModify(boolean allowModifications) {
        setPermissionBit(MODIFICATION_BIT, allowModifications);
    }

    /**
     * Set a permission Bit
     * @param bit int the bit to be set
     * @param value boolean allow access
     */
    private void setPermissionBit(int bit, boolean value) {
        int permissions = bytes;
        if (value) {
            permissions |=  (1 << (bit - 1));
        } else {
            permissions &= (~(1 << (bit - 1)));
        }

        bytes = permissions;
    }
}
