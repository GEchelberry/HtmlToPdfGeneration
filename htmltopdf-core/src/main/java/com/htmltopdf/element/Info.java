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
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Class to hold the Information element for the PDF
 */
public class Info implements Element {
    private int objectId;
    private String title;
    private String author;
    private String subject;
    private String creator;
    private String producer;
    private String creationDate;

    /**
     * Constructor - Initalizes element with the creation date
     */
    public Info() {
        this.creationDate = formatDateTime();
    }

    /**
     * Get the document creation Date
     * @return String of the creation date
     */
    public String getCreationDate() {
        return creationDate;
    }

    /**
     * Set the document title
     * @param title String of the title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get the Document title
     * @return String of the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the document author
     * @param author String of the author name
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Get the document author
     * @return String of the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Set the document subject
     * @param subject String of the document subject
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Get the document subject
     * @return String of the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Set the document creator
     * @param creator String of the document creator
     */
    public void setCreator(String creator) {
        this.creator = creator;
    }

    /**
     * Get the document creator
     * @return String of the creator
     */
    public String getCreator() {
        return creator;
    }

    /**
     * Set the document producer
     * @param producer String of the producer
     */
    public void setProducer(String producer) {
        this.producer = producer;
    }

    /**
     * Get the document Producer
     * @return String of the producer
     */
    public String getProducer() {
        return producer;
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
        if (encryptionKey != null) {
            
        } else {
            sb.append("<< /Title (" + title + ")\n");
            sb.append("/Author (" + author + ")\n");
            sb.append("/Subject (" + subject + ")\n");
            sb.append("/Creator (" + creator + ")\n");
            sb.append("/Producer (" + producer + ")\n");
            sb.append("/CreationDate (" + creationDate + ")\n");
        }
        sb.append(">>\n");
        sb.append("endobj\n");

        return sb.toString().getBytes(StandardCharsets.ISO_8859_1);
    }

    @Override
    public List<Element> buildElementList() {
        // Not needed for this Element type
        throw new UnsupportedOperationException("Info had an Unimplemented method 'buildElementList'");
    }
    
    /**
     * Utility method to generate a properly formatted Timestamp
     * @return String of the current timestamp
     */
    private String formatDateTime() {
        ZonedDateTime now = ZonedDateTime.now();
        DateTimeFormatter pdfFormat = DateTimeFormatter.ofPattern("'D:'yyyyMMddHHmmssZ");
        String pdfDate = now.format(pdfFormat)
            .replaceFirst("(\\+|\\-)(\\d{2})(\\d{2})", "$1$2'$3'");

        return pdfDate;
    }
}
