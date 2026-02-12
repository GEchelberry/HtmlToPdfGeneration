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

import com.htmltopdf.renderer.Util;
import com.htmltopdf.security.PdfR4V4Security;

/**
 * Class to hold the Structure data
 */
public class StructElement implements Element {
    public static final String DOCUMENT = "Document";
    public static final String SECT = "Sect";
    public static final String H = "H";
    public static final String H1 = "H1";
    public static final String H2 = "H2";
    public static final String H3 = "H3";
    public static final String H4 = "H4";
    public static final String H5 = "H5";
    public static final String H6 = "H6";
    public static final String P = "P";
    public static final String SPAN = "Span";
    public static final String LINK = "Link";
    public static final String FIGURE = "Figure";
    public static final String TABLE = "Table";
    public static final String TH = "TH";
    public static final String TR = "TR";
    public static final String TD = "TD";
    public static final String LIST = "L";
    public static final String LIST_ITEM = "LI";
    public static final String LBODY = "LBody";
    public static final String LBL = "Lbl";

    private int objectId;
    private String type;
    private Element parent;
    private Page page;
    private List<Element> kids;
    private String altText;
    private String bBox;
    private int rowspan = 0;
    private int colspan = 0;

    /**
     * Constructor - Initialize variables
     * @param type
     */
    public StructElement(String type) {
        this.type = type;
        this.kids = new ArrayList<>();
    }

    /**
     * Gets the type of the Structure Element
     * @return String type of the Element
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the parent element
     * @param parent Element of the parent
     */
    public void setParent(Element parent) {
        this.parent = parent;
    }

    /**
     * Gets the elements parent element
     * @return Element of the parent
     */
    public Element getParent() {
        return parent;
    }

    /**
     * Sets the Elements Page
     * @param page Page of the elements page
     */
    public void setPage(Page page) {
        this.page = page;
    }

    /**
     * Adds a child element to the structure
     * @param kid Element of the kid
     */
    public void addKid(Element kid) {
        kids.add(kid);
    }

    /**
     * Sets the alternate text of the structure element
     * @param altText String of the alternate text
     */
    public void setAltText(String altText) {
        this.altText = altText;
    }

    /**
     * Sets the elements bounding box
     * @param x1 int lower left corner X
     * @param y1 int lower left corner Y
     * @param x2 int upper right corner X
     * @param y2 int upper right corner Y
     */
    public void setBoundingBox(int x1, int y1, int x2, int y2) {
        this.bBox = "[" + x1 + " " + y1 + " " + x2 + " " + y2 + "]";
    }

    /**
     * Sets the row span for a table cell
     * @param rowspan int the number of rows to span
     */
    public void setRowSpan(int rowspan) {
        this.rowspan = rowspan;
    }

    /**
     * Sets the number of columns a table cell spans
     * @param colspan int the number of columns to span
     */
    public void setColumnSpan(int colspan) {
        this.colspan = colspan;
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
        sb.append("<< /Type /StructElem\n");
        sb.append("/S /" + type + "\n");
        sb.append("/P " + parent.getObjectId() + " 0 R\n");
        if (page != null) sb.append("/Pg " + page.getObjectId() + " 0 R\n");
        if (!kids.isEmpty()) {
            sb.append("/K [");
            for (int i = 0; i < kids.size(); i++) {
                sb.append(kids.get(i).getObjectId() + " 0 R");
                if (i == kids.size() - 1) {
                    sb.append("]\n");
                } else {
                    sb.append(" ");
                }
            }
        }
        if (type.equals(TD)) {
            sb.append("/A << /O / Table" + (rowspan != 0 ? " /RowSpan " + rowspan : "") + (colspan != 0 ? " /ColSpan " + colspan : "") + ">>\n");
        }
        if (type.equals(TH)) {
            if (colspan != 0) {
                sb.append("/A << /O /Table /Colspan " + colspan + " /Scope /Column >>\n");
            } else {
                sb.append("/A << /O /Table /Scope /Column >>\n");
            }
        }
        if (type.equals(TABLE)) {
            if (encryptionKey != null) {
                byte[] encrytped = PdfR4V4Security.encryptString(encryptionKey, objectId, 0, altText.getBytes(StandardCharsets.ISO_8859_1));
                sb.append("/A << /O /Table /Summary <" + Util.byteToHex(encrytped) + "> >>\n");
            } else {
                sb.append("/A << /O /Table /Summary (" + altText + ") >>\n");
            }
        } else {
            if (altText != null) {
                if (encryptionKey != null) {
                    byte[] encrypted = PdfR4V4Security.encryptString(encryptionKey, objectId, 0, altText.getBytes(StandardCharsets.ISO_8859_1));
                    sb.append("/Alt <" + Util.byteToHex(encrypted) + ">\n");
                } else {
                    sb.append("/Alt (" + altText + ")\n");
                }
            }
        }
        if (bBox != null) {
            sb.append("/A << /BBox " + bBox + " /O /Layout >>\n");
        }
        sb.append(">>\n");
        sb.append("endobj\n");

        return sb.toString().getBytes(StandardCharsets.ISO_8859_1);
    }

    @Override
    public List<Element> buildElementList() {
        List<Element> elements = new ArrayList<>();
        elements.add(this);
        for (Element child : kids) {
            elements.addAll(child.buildElementList());
        }

        return elements;
    }
    
}
