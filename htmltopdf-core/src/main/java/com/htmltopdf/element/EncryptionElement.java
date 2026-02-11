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

package com.htmltopdf.element;

import java.nio.charset.StandardCharsets;
import java.util.List;

import com.htmltopdf.renderer.Util;
import com.htmltopdf.security.PdfR4V4Security;

/**
 * Class to hold all the encryption data
 */
public class EncryptionElement implements Element {
    private int objectId;
    private byte[] encryptionKey;
    private byte[] owner;
    private byte[] user;
    private int permissions;
    private boolean encryptMetaData;

    /**
     * Constructor - Initializes encryption objects
     * @param ownerPassword String of the owner password
     * @param userPassword String of the user password
     * @param documentId String of the document Id
     * @param permissions int of the permissions
     * @param encryptMetaData boolean encrypt meta data
     */
    public EncryptionElement(String ownerPassword, String userPassword, String documentId, int permissions, boolean encryptMetaData) {
        byte[] fileId = Util.hexStringToBytes(documentId);
        owner = PdfR4V4Security.computeOwnerValue(ownerPassword, userPassword);
        encryptionKey = PdfR4V4Security.computeEncryptionKey(userPassword, owner, permissions, fileId, encryptMetaData);
        user = PdfR4V4Security.computeUserValue(encryptionKey, fileId);
        this.permissions = permissions;
        this.encryptMetaData = encryptMetaData;
    }

    /**
     * Gets the encryption key
     * @return byte array of the encryption key
     */
    public byte[] getEncryptionKey() {
        return encryptionKey;
    }

    @Override
    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }

    @Override
    public int getObjectId() {
        return objectId;
    }

    @Override
    public byte[] toByte(byte[] encryptionKey) {
        StringBuilder sb = new StringBuilder();
        sb.append(objectId + " 0 obj\n");
        sb.append("<< /Filter /Standard\n");
        sb.append("/V 4\n");
        sb.append("/R 4\n");
        sb.append("/Length 128\n");
        sb.append("/CF << /StdCF << /CFM /AESV2 /Length 16 >> >>\n");
        sb.append("/StmF /StdCF\n");
        sb.append("/StrF /StdCF\n");
        sb.append("/O <" + Util.byteToHex(owner) + ">\n");
        sb.append("/U <" + Util.byteToHex(user) + ">\n");
        sb.append("/P " + permissions + "\n");
        sb.append("/EncryptMetadata " + (encryptMetaData ? "true" : "false") + "\n");
        sb.append(">>\n");
        sb.append("endobj\n");

        return sb.toString().getBytes(StandardCharsets.ISO_8859_1);
    }

    @Override
    public List<Element> buildElementList() {
        // Not needed for this Element type
        throw new UnsupportedOperationException("EncryptionElement has an Unimplemented method 'buildElementList'");
    }
    
}
