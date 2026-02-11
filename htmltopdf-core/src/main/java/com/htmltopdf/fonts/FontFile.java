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

package com.htmltopdf.fonts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.htmltopdf.element.Element;
import com.htmltopdf.renderer.Util;
import com.htmltopdf.security.PdfR4V4Security;

/**
 * Class to hold the embedded font file
 */
public class FontFile implements Element {
    private static final Logger logger = LoggerFactory.getLogger(FontFile.class);
    protected int objectId;
    private String path;

    /**
     * Constructor - Initialize element with the file path
     * @param path Stirng path for the font file
     */
    public FontFile(String path) {
        this.path = path;
    }

    /**
     * Default Constructor -- For use only when copying a PDF
     */
    public FontFile() {

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
        byte[] output = new byte[0];
        try (FileInputStream inputStream = new FileInputStream(new File(path))) {
            byte[] bytes = null;
            byte[] uncompressed = inputStream.readAllBytes();
            if (encryptionKey != null) {
                bytes = PdfR4V4Security.encryptObject(encryptionKey, objectId, 0, Util.compressStream(uncompressed));
            } else {
                bytes = Util.compressStream(uncompressed);
            }

            StringBuilder sb = new StringBuilder();
            sb.append(objectId + " 0 obj\n");
            sb.append("<< /Length " + bytes.length + " /Length1 " + uncompressed.length + " /Filter /FlateDecode >>\n");
            sb.append("stream\n");

            byte[] end = "\nendstream\nendobj\n".getBytes(StandardCharsets.ISO_8859_1);
            int len = sb.toString().getBytes(StandardCharsets.ISO_8859_1).length;
            output = new byte[len + bytes.length + end.length];
            System.arraycopy(sb.toString().getBytes(StandardCharsets.ISO_8859_1), 0, output, 0, len);
            System.arraycopy(bytes, 0, output, len, bytes.length);
            System.arraycopy(end, 0, output, len + bytes.length, end.length);
        } catch (FileNotFoundException notFound) {
            logger.error("File not found at path " + path, notFound);
            throw new RuntimeException("File not found at specified path");
        } catch (IOException ioEx) {
            logger.error("IO Exception was encountered reading file at " + path, ioEx);
            throw new RuntimeException("IO Exception was encountered while reading " + path);
        }

        return output;
    }

    @Override
    public List<Element> buildElementList() {
        // Not needed for this Element type
        throw new UnsupportedOperationException("Unimplemented method 'buildElementList'");
    }
    
}
