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

import com.htmltopdf.fonts.Font;

/**
 * Class to hold all the page information
 */
public class Page implements Element {
    protected int objectId;
    protected Element parent;
    protected String mediaBox;
    protected List<ContentStream> contents;
    protected List<Font> fonts;
    protected List<XObject> xObjects;
    protected List<Element> annotations;
    protected String tabs = "/S";
    protected int structParents;
    protected boolean addWatermark;

    /**
     * Constructor - initalize variables
     * @param width int of the width of the page
     * @param height int of the height of the page
     */
    public Page(int width, int height) {
        contents = new ArrayList<>();
        fonts = new ArrayList<>();
        xObjects = new ArrayList<>();
        annotations = new ArrayList<>();
        this.mediaBox = "[0 0 " + width + " " + height + "]";
    }

    /**
     * Constructor - initialize variables
     * @param size Page.Size of the desired page size
     */
    public Page(Size size) {
        contents = new ArrayList<>();
        fonts = new ArrayList<>();
        xObjects = new ArrayList<>();
        annotations = new ArrayList<>();
        this.mediaBox = "[0 0 " + size.x + " " + size.y + "]";
    }

    /**
     * Sets the parent element
     * @param parent Element for the parent object
     */
    public void setParent(Element parent) {
        this.parent = parent;
    }

    /**
     * Sets the page struct parents
     * @param structParents int of the struct parents
     */
    public void setStructParents(int structParents) {
        this.structParents = structParents;
    }

    /**
     * Get the struct parents value
     * @return
     */
    public int getStructParents() {
        return structParents;
    }

    /**
     * Adds a font to the page. If font has already been added the index
     * of the font is returned. Otherwise, the font is added to the array
     * and the size is returned
     * @param font Font of the desired font
     * @return int of the index of the font
     */
    public int addFont(Font font) {
        for (int i = 0; i < fonts.size(); i++) {
            if (fonts.get(i).getObjectId() == font.getObjectId()) {
                return i + 1;
            }
        }

        fonts.add(font);
        return fonts.size();
    }

    /**
     * Add content to a page
     * @param content ContentStream of the content
     */
    public void addContent(ContentStream content) {
        contents.add(content);
    }

    /**
     * Add an Annotation to the page
     * @param annotation Element of the annotation
     */
    public void addAnnotation(Element annotation) {
        annotations.add(annotation);
    }

    /**
     * Add an X Object to the page
     * @param xObject XObject to be added
     */
    public void addXObject(XObject xObject) {
        xObjects.add(xObject);
    }

    /**
     * Gets the last content stream of the page
     * @return ContentStream of the last content stream
     */
    public Element getLastcontentStream() {
        return contents.get(contents.size() - 1);
    }

    /**
     * Sets boolean to add watermark to the page
     * @param addWatermark boolean True to add the watermark
     */
    public void setAddWatermark(boolean addWatermark) {
        this.addWatermark = addWatermark;
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
        sb.append("<< /Type /Page\n");
        sb.append("/Parent " + parent.getObjectId() + " 0 R\n");
        sb.append("/MediaBox " + mediaBox + "\n");
        if (!contents.isEmpty()) {
            sb.append("/Contents ");
            for (Element e : contents) {
                sb.append(e.getObjectId() + " 0 R\n");
            }
        }
        if (!xObjects.isEmpty() || !fonts.isEmpty() || addWatermark) {
            sb.append("/Resources <<\n");
            if (!fonts.isEmpty()) {
                sb.append("/Font << ");
                for (int i = 0; i < fonts.size(); i++) {
                    sb.append("/F" + (i + 1) + " " + fonts.get(i).getObjectId() +  " 0 R ");
                }
                sb.append(">>\n");
            }
            if (!xObjects.isEmpty()) {
                sb.append("/XObject << ");
                for (int i = 0; i < xObjects.size(); i++) {
                    sb.append("/Im" + xObjects.get(i).getIdentifier() + " " + xObjects.get(i).getObjectId() + " 0 R ");
                }
                sb.append(">>\n");
            }
            if (addWatermark) {
                sb.append("/ExtGState <<\n");
                sb.append("/GS1 <<\n");
                sb.append("/Type /ExtGState\n");
                sb.append("/ca 0.10\n");
                sb.append("/CA 0.10\n");
                sb.append(">>\n");
                sb.append(">>\n");
            }
            sb.append(">>\n");
        }
        if (!annotations.isEmpty()) {
            sb.append("/Annots [");
            for (int i = 0; i < annotations.size(); i++) {
                sb.append(annotations.get(i).getObjectId() + " 0 R");
                if (i == annotations.size() - 1) {
                    sb.append("]\n");
                } else {
                    sb.append(" ");
                }
            }
        }
        sb.append("/Tabs " + tabs + "\n");
        sb.append("/StructParents " + structParents + "\n");
        sb.append(">>\n");
        sb.append("endobj\n");

        return sb.toString().getBytes(StandardCharsets.ISO_8859_1);
    }

    @Override
    public List<Element> buildElementList() {
        List<Element> elements = new ArrayList<>();
        elements.add(this);
        if (!contents.isEmpty()) elements.addAll(contents);
        if (!xObjects.isEmpty()) elements.addAll(xObjects);
        for (Element element : annotations) {
            elements.addAll(element.buildElementList());
        }

        return elements;
    }
    
    public static enum Size {
        A4(595, 842),
        LETTER(612, 792),
        LEGAL(612, 1008);
        private int x;
        private int y;
        
        /**
         * Constructor - Initialize values
         * @param x int of the width
         * @param y int of the hieght
         */
        Size(int x, int y) {
            this.x = x;
            this.y = y;
        }

        /**
         * Get the width of the page size
         * @return int of the width
         */
        public int getX() {
            return x;
        }

        /**
         * Get the height of the page size
         * @return int of the height
         */
        public int getY() {
            return y;
        }
    }
}
