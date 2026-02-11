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

package com.htmltopdf.renderer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.htmltopdf.element.Element;
import com.htmltopdf.element.Info;

/**
 * Class to write the PDF structure elements to a PDF file
 */
public class PDFWriter {
    private static final String HEADER = "%PDF-1.7\n";
    private static final byte[] MARKER = {'%', (byte) 0xE2, (byte) 0xE3, (byte) 0xCF, (byte) 0xD3, '\n'};
    private static final String EOF = "%EOF\n";
    private List<Long> offsets = new ArrayList<>();

    /**
     * Writes the structure to a file as bytes
     * @param outputStream FileOutputStream of the path to save file
     * @param elements List of Elements to write
     * @param info Info element
     * @param documentId String of the Unique document Id
     * @param encryptionElementId int of the Encryption Element Object Id
     * @param encryptionKey byte array of the encryption key
     * @throws IOException throws IO Exception if the output stream cannot be written to
     */
    public void write(FileOutputStream outputStream, List<Element> elements, Info info, String documentId, int encryptionElementId, byte[] encryptionKey) throws IOException {
        // Write Header
        outputStream.write(HEADER.getBytes(StandardCharsets.ISO_8859_1));
        outputStream.write(MARKER);
        long pos = outputStream.getChannel().position();

        // Write Body
        for (Element element : elements) {
            if (element.getObjectId() > 0) {
                pos = outputStream.getChannel().position();
                offsets.add(pos);
                outputStream.write(element.toByte(encryptionKey));
            }
        }

        // Write XREF table
        pos = outputStream.getChannel().position();
        outputStream.write("xref\n".getBytes(StandardCharsets.ISO_8859_1));
        outputStream.write(("0 " + (offsets.size() + 1) + "\n").getBytes(StandardCharsets.ISO_8859_1));
        outputStream.write("0000000000 65535 f \n".getBytes(StandardCharsets.ISO_8859_1));
        for (long offset : offsets) {
            outputStream.write(String.format("%010d 00000 n \n", offset).getBytes(StandardCharsets.ISO_8859_1));
        }

        // Write Trailer
        outputStream.write("trailer\n".getBytes(StandardCharsets.ISO_8859_1));
        outputStream.write(("<< /Size " + (offsets.size() + 1) + "\n").getBytes(StandardCharsets.ISO_8859_1));
        if (encryptionElementId != 0) outputStream.write(("/Encrypt " + encryptionElementId + " 0 R\n").getBytes(StandardCharsets.ISO_8859_1));
        outputStream.write(("/Root 1 0 R\n" + "/Info " + info.getObjectId() + " 0 R\n").getBytes(StandardCharsets.ISO_8859_1));
        outputStream.write(("/ID [<" + documentId + "> <" + documentId + ">]\n").getBytes(StandardCharsets.ISO_8859_1));
        outputStream.write(("startxref\n" + pos + "\n").getBytes(StandardCharsets.ISO_8859_1));

        // Write End of File
        outputStream.write(EOF.getBytes(StandardCharsets.ISO_8859_1));
    }
}
