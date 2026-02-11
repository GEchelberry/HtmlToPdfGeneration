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
 * Class to hold the information for a widget element
 */
public class Widget implements Element {
    public static final String TEXT = "Tx";
    public static final String BUTTON = "Btn";
    public static final String SIGNATURE = "Sig";
    private int objectId;
    private Page page;
    private String rect;
    private String type;
    private String fieldName;
    private String toolTip;
    private int flag;
    private int formFlag;
    private String value = "";
    private int structParents;
    private String font;
    private int size;
    private int refId;
    private int alignment = 0;
    private List<AppearanceStream> appearanceStreams;
    private String apState = null;
    private boolean isDateField = false;

    /**
     * Constructor - Initializes type and variables
     * @param type String of the widget type
     */
    public Widget(String type) {
        this.type = type;
        appearanceStreams = new ArrayList<>();
    }

    /**
     * Sets the Page of the widget
     * @param page Page object for the page
     */
    public void setPage(Page page) {
        this.page = page;
    }

    /**
     * Sets the Rect for the widget
     * @param x1 float lower left corner X
     * @param y1 float lower left corner Y
     * @param x2 float upper right corner X
     * @param y2 float upper right corner Y
     */
    public void setRect(float x1, float y1, float x2, float y2) {
        this.rect = "[" + x1 + " " + y1 + " " + x2 + " " + y2 + "]";
    }

    /**
     * Sets the field name for the widget
     * @param fieldName String of the text
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * Get the field name of the widget
     * @return String of the field name
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Set the widget flag
     * @param flag int of the flag value
     */
    public void setFlag(int flag) {
        this.flag = flag;
    }

    /**
     * Sets the form flag value
     * @param formFlag int of the form flag
     */
    public void setFormFlag(int formFlag) {
        this.formFlag = formFlag;
    }

    /**
     * Sets the value of the widget (Visible text)
     * @param value String of the value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Sets the struct parents for the widget
     * @param structParents int of the struct parent id
     */
    public void setStructParents(int structParents) {
        this.structParents = structParents;
    }

    /**
     * Get the Struct Parents Id
     * @return int of the struct parents id
     */
    public int getStructParents() {
        return structParents;
    }

    /**
     * Sets the font for the widget
     * @param font String of the font Identifier
     * @param size int of the font size
     */
    public void setFont(String font, int size) {
        this.font = font;
        this.size = size;
    }

    /**
     * Get the Font Identifier for the widget
     * @return String of the font identifier
     */
    public String getFontIdentifier() {
        return font;
    }

    /**
     * Sets the reference Id for the widget
     * @param refId int of the reference Id
     */
    public void setRefId(int refId) {
        this.refId = refId;
    }

    /**
     * Get the reference Id for the widget
     * @return int of the reference Id
     */
    public int getRefId() {
        return refId;
    }

    /**
     * Sets the widget tool tip
     * @param toolTip String of the tool tip
     */
    public void setToolTip(String toolTip) {
        this.toolTip = toolTip;
    }

    /**
     * Adds an Appearance stream to the widget
     * @param appearanceStream AppearanceStream element to add to the widget
     */
    public void addAppearanceStream(AppearanceStream appearanceStream) {
        appearanceStreams.add(appearanceStream);
    }

    /**
     * Sets the state of a checkbox, Checked or unchecked
     * @param apState String of the Appearance State
     */
    public void setApState(String apState) {
        this.apState = apState;
    }

    /**
     * Sets the alignment of the widget value
     * @param alignment int of the alignment 0-left, 1-center, 2-right
     */
    public void setAlignment(int alignment) {
        this.alignment = alignment;
    }

    /**
     * Flag to determine if the field is a date field
     * TRUE will add the javascript date field functionality
     * @param isDateField boolean is a field a date field
     */
    public void setIsDateField(boolean isDateField) {
        this.isDateField = isDateField;
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
        sb.append("<< /Type /Annot\n");
        sb.append("/Subtype /Widget\n");
        sb.append("/P " + page.getObjectId() + " 0 R\n");
        if (encryptionKey != null) {
            byte[] text = ("/" + font + " " + size + " Tf 0 0 0 rg\n").getBytes(StandardCharsets.ISO_8859_1);
            text = PdfR4V4Security.encryptString(encryptionKey, objectId, 0, text);
            sb.append("/DA <" + Util.byteToHex(text) + ">\n");
        } else {
            sb.append("/DA (/" + font + " " + size + " Tf 0 0 0 rg)\n");
        }
        sb.append("/FT /" + type + "\n");
        sb.append("/F " + flag + "\n");
        sb.append("/Ff " + formFlag + "\n");
        sb.append("/Q " + alignment + "\n");
        if (encryptionKey != null) {
            byte[] encryptText = PdfR4V4Security.encryptString(encryptionKey, objectId, 0, fieldName.getBytes(StandardCharsets.ISO_8859_1));
            byte[] encryptToolTip = PdfR4V4Security.encryptString(encryptionKey, objectId, 0, toolTip.getBytes(StandardCharsets.ISO_8859_1));
            sb.append("/T <" + Util.byteToHex(encryptText) + ">\n");
            sb.append("/TU <" + Util.byteToHex(encryptToolTip) + ">\n");
        } else {
            sb.append("/T (" + Util.escapeText(fieldName) + ")\n");
            sb.append("/TU (" + Util.escapeText(toolTip) + ")\n");
        }
        if (isDateField) {
            if (encryptionKey != null) {
                String javaScript = "AFDate_FormatEx(\"mm/dd/yyyy\");";
                byte[] encrypted = PdfR4V4Security.encryptString(encryptionKey, objectId, 0, javaScript.getBytes(StandardCharsets.ISO_8859_1));
                sb.append("/AA << /F << /S /JavaScript /JS <" + Util.byteToHex(encrypted) + "> >> >>\n");
            } else {
                sb.append("/AA << /F << /S /JavaScript / JS (AFDate_FormatEX(\"mm/dd/yyyy\");) >> >>\n");
            }
        }
        if (apState != null) {
            sb.append("/V /" + value + "\n");
            sb.append("/AS /" + apState + "\n");
            if (encryptionKey != null) {
                byte[] encrypted = PdfR4V4Security.encryptString(encryptionKey, objectId, 0, "8".getBytes(StandardCharsets.ISO_8859_1));
                sb.append("/MK << /CA <" + Util.byteToHex(encrypted) + "> >>\n");
            } else {
                sb.append("/MK << /CA (8) >>\n");
            }
        } else {
            if (type != Widget.SIGNATURE) {
                if (encryptionKey != null) {
                    byte[] encrypted = PdfR4V4Security.encryptString(encryptionKey, objectId, 0, value.getBytes(StandardCharsets.ISO_8859_1));
                    sb.append("/V <" + Util.byteToHex(encrypted) + ">\n");
                } else {
                    sb.append("/V (" + value + ")\n");
                }
            }
        }
        sb.append("/Rect " + rect + "\n");
        sb.append("/StructParent " + structParents + "\n");
        if (!appearanceStreams.isEmpty()) {
            if (appearanceStreams.size() == 1 & appearanceStreams.get(0).getType() == null) {
                sb.append("/AP << /N " + appearanceStreams.get(0).getObjectId() + " 0 R >>");
            } else {
                sb.append("/AP << /D << ");
                for (AppearanceStream ap : appearanceStreams) {
                    sb.append("/" + ap.getType() + " " + ap.getObjectId() + " 0 R ");
                }
                sb.append(">> ");
                for (AppearanceStream ap : appearanceStreams) {
                    if (ap.getType().equals(AppearanceStream.ON)) {
                        sb.append("/N << /" + ap.getType() + " " + ap.getObjectId() + " 0 R >>");
                    }
                }
                sb.append(">>\n");
            }
        }
        sb.append(">>\nendobj\n");

        return sb.toString().getBytes(StandardCharsets.ISO_8859_1);
    }

    @Override
    public List<Element> buildElementList() {
        List<Element> elements = new ArrayList<>();
        elements.add(this);
        elements.addAll(appearanceStreams);

        return elements;
    }
    
}
