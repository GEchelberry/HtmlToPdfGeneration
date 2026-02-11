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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;

import com.htmltopdf.fonts.Font;

/**
 * Utility methods used in PDF generation
 */
public class Util {
    
    /**
     * Utility method to convert a byte array to a
     * Hex string for writing to PDF
     * @param bytes byte array of the data
     * @return String of Hex
     */
    public static String byteToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }

        return sb.toString();
    }

    /**
     * Utility method to compress a byte array
     * @param content byte array of the content to compress
     * @return byte array of the compressed content
     */
    public static byte[] compressStream(byte[] content) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (DeflaterOutputStream dos = new DeflaterOutputStream(baos)) {
            dos.write(content);
        } catch (IOException ioex) {
            throw new RuntimeException("IO Exception encountered while compressing content");
        }
        return baos.toByteArray();
    }

    /**
     * Utility method to uncompress a byte array
     * @param content byte array of compressed content
     * @return byte array of uncompressed content
     */
    public static byte[] uncompressStream(byte[] content) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (InflaterOutputStream ios = new InflaterOutputStream(baos)) {
            ios.write(content);
        } catch (IOException ioex) {
            throw new RuntimeException("IO Exception encountered while uncompressing content");
        }
        return baos.toByteArray();
    }

    /**
     * Utility method to escape text to allow PDF format
     * @param text String of text
     * @return String of escaped text
     */
    public static String escapeText(String text) {
        if (text == null || text.equals("")) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            switch (c) {
                case '(':
                    sb.append("\\(");
                    break;
                case ')':
                    sb.append("\\)");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                default:
                    if (c < 0x20 || c > 0x7E) {
                        sb.append(String.format("\\%03o", (int) c));
                    } else {
                        sb.append(c);
                    }
                    break;
            }
        }

        return sb.toString();
    }

    /**
     * Get the height of a text string
     * @param font Font the font of the text
     * @param size int size of the text
     * @return int the text height
     */
    public static int getTextHeight(Font font, int size) {
        int ascent = font.getFontDecriptor().getAscent();
        int descent = font.getFontDecriptor().getDescent();
        int height = ((ascent - descent) * size) / font.getUnitsPerEm();
        return height;
    }

    /**
     * Get the length of a text string
     * @param text String text
     * @param font Font font object
     * @param fontSize int fontSize
     * @return int length of the text
     */
    public static int getTextLength(String text, Font font, int fontSize) {
        return (fontSize * font.getTextWidth(text)) / 1000;
    }

    /**
     * Utility to convert a hex string to a 16 byte array
     * @param hex Stirng of the hex
     * @return byte array
     */
    public static byte[] hexStringToBytes(String hex) {
        int len = hex.length();
        if (len % 2 != 0) {
            throw new IllegalArgumentException("Hex String must have even length");
        }

        byte[] bytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i + 1), 16));
        }
        return bytes;
    }
}
