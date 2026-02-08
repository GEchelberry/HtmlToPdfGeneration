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

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

/**
 * Class that holds the XMP meta data
 */
public class XmpMetaData implements Element {
    private int objectId;
    private Info info;
    private String documentId;

    /**
     * Constructor - Sets the Unique document ID
     */
    public XmpMetaData() {
        documentId = generatePDFID();
    }

    /**
     * Sets the Info object
     * @param info Info object
     */
    public void setInfo(Info info) {
        this.info = info;
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
        byte[] stream = buildMetadata();

        StringBuilder object = new StringBuilder();
        object.append(objectId + " 0 obj\n");
        object.append("<< /Type /Metadata\n");
        object.append("/Subtype /XML\n");
        object.append("/Length " + stream.length + "\n");
        object.append(">>\nstream\n");

        byte[] begin = object.toString().getBytes(StandardCharsets.ISO_8859_1);

        StringBuilder ending = new StringBuilder();
        ending.append("endstream\nendobj\n");

        byte[] end = ending.toString().getBytes(StandardCharsets.ISO_8859_1);

        byte[] obj = new byte[begin.length + stream.length + end.length];
        System.arraycopy(begin, 0, obj, 0, begin.length);
        System.arraycopy(stream, 0, obj, begin.length, stream.length);
        System.arraycopy(end, 0, obj, begin.length + stream.length, end.length);

        return obj;
    }

    @Override
    public List<Element> buildElementList() {
        // Not used by this Element type
        throw new UnsupportedOperationException("XmpMetaData has Unimplemented method 'buildElementList'");
    }
    
     /**
     * Utility class to build the XMP Metadata
     * @return byte array of the metadata
     */
    private byte[] buildMetadata() {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xpacket begin=\"\" id=\"W5M0MpCehiHzreSzNTczkc9d\"?>\n");
        sb.append("<x:xmpmeta xmlns:x=\"adobe:ns:meta/\">\n");
        sb.append("\t<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n");
        sb.append("\t\t<rdf:Description xmlns:dc=\"http://purl.org/dc/elements/1.1/\">\n");
        sb.append("\t\t\t<dc:title>\n");
        sb.append("\t\t\t\t<rdf:Alt>\n");
        sb.append("\t\t\t\t\t<rdf:li xml:lang=\"x-default\">" + info.getTitle() + "</rdf:li>\n");
        sb.append("\t\t\t\t</rdf:Alt>\n");
        sb.append("\t\t\t</dc:title>\n");
        sb.append("\t\t\t<dc:creator>\n");
        sb.append("\t\t\t\t<rdf:Seq>\n");
        sb.append("\t\t\t\t\t<rdf:li>" + info.getCreator() + "</rdf:li>\n");
        sb.append("\t\t\t\t</rdf:Seq>\n");
        sb.append("\t\t\t</dc:creator>\n");
        sb.append("\t\t\t<dc:subject>\n");
        sb.append("\t\t\t\t<rdf:Bag>\n");
        sb.append("\t\t\t\t\t<rdf:li>" + info.getSubject() + "</rdf:li>\n");
        sb.append("\t\t\t\t\t<rdf:li>PDF/UA</rdf:li>\n");
        sb.append("\t\t\t\t</rdf:Bag>\n");
        sb.append("\t\t\t</dc:subject>\n");
        sb.append("\t\t</rdf:Description>\n");
        sb.append("\t\t<rdf:Description xmlns:pdfuaid=\"http://www.aiim.org/pdfua/ns/id/\">\n");
        sb.append("\t\t\t<pdfuaid:part>1</pdfuaid:part>\n");
        sb.append("\t\t</rdf:Description>\n");
        sb.append("\t\t<rdf:Description xmlns:pdf=\"http://ns.adobe.com/pdf/1.3/\">\n");
        sb.append("\t\t\t<pdf:Producer>" + info.getProducer() + "</pdf:Producer>\n");
        sb.append("\t\t\t<pdf:Creator>" + info.getCreator() + "</pdf:Creator>\n");
        sb.append("\t\t\t<pdf:CreationDate>" + info.getCreationDate() + "</pdf:CreationDate>\n");
        sb.append("\t\t\t<pdf:ModDate>" + info.getCreationDate() + "</pdf:ModDate>\n");
        sb.append("\t\t</rdf:Description>\n");
        sb.append("\t\t<rdf:Description xmlns:xmpMM=\"http://ns.adobe.com/xap/1.0/mm/\">\n");
        sb.append("\t\t\t<xmpMM:DocumentID>uuid:" + documentId + "</xmpMM:DocumentID>\n");
        sb.append("\t\t\t<xmpMM:InstanceID>uuid:" + documentId + "</xmpMM:InstanceID>\n");
        sb.append("\t\t</rdf:Description>\n");
        sb.append("\t</rdf:RDF>\n");
        sb.append("</x:xmpmeta>\n");
        sb.append("<?xpacket end=\"w\"?>\n");

        return sb.toString().getBytes(StandardCharsets.US_ASCII);
    }
    
    /**
     * Generates a UUID for the document
     * @return String of the UUID
     */
    private String generatePDFID() {
        try {
            String seed = UUID.randomUUID().toString() + System.currentTimeMillis();
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest(seed.getBytes("UTF-8"));

            // Convert to uppercase hex string (no spaces)
            StringBuilder hex = new StringBuilder();
            for (byte b : digest) {
                hex.append(String.format("%02X", b));
            }

            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("No such algorithm to generate UUID");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("No such encoding to generate the UUID");
        }
    }
}
