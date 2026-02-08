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
import java.util.ArrayList;
import java.util.List;

import com.htmltopdf.security.PdfR4V4Security;

/**
 * Class that holds the Catalog structure data
 */
public class Catalog implements Element {
    private int objectId;
    private float version = 1.7f;
    private String language = "en-US";
    private XmpMetaData metaData;

    /**
     * Sets the version of the PDF default 1.7
     * @param version float of the version
     */
    public void setVersion(float version) {
        this.version = version;
    }

    /**
     * Seta the PDF language default en-US
     * @param language String of the language
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Sets the meta data object
     * @param metaData XmpMetaData object for the PDF
     */
    public void setMetaData(XmpMetaData metaData) {
        this.metaData = metaData;
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
        sb.append("<< /Type /Catalog\n");
        sb.append("/Version /" + version + "\n");
        if (encryptionKey != null) {
            sb.append("/Lang <" + PdfR4V4Security.encryptString(encryptionKey, objectId, 0, language.getBytes(StandardCharsets.UTF_8)) + ">\n");
        } else {
            sb.append("/Lang (" + language + ")\n");
        }
        sb.append("/MarkInfo << /Marked true >>\n");
        sb.append("/Metadata " + metaData.getObjectId() + " 0 R\n");
        sb.append("/ViewerPrefernces << /DisplayDocTitle true >>\n");
        sb.append(">>\n");
        sb.append("endobj");

        return sb.toString().getBytes(StandardCharsets.ISO_8859_1);
    }

    @Override
    public List<Element> buildElementList() {
        List<Element> elements = new ArrayList<>();
        elements.add(this);

        return elements;
    }
    
}
