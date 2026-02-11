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
 * Class to define an Acro Form
 */
public class AcroForm implements Element {
    private int objectId;
    private List<Element> fields;
    private List<Font> fonts;
    private boolean needAppearances = true;

    /**
     * Constructor - Initialize variables
     */
    public AcroForm() {
        this.fields = new ArrayList<>();
        this.fonts = new ArrayList<>();
    }

    /**
     * Adds a field to the acro form
     * @param field Element of the field object
     */
    public void addField(Element field) {
        fields.add(field);
    }

    /**
     * Adds a font to the acro form, if the font is present returns location of
     * the font, otherwise adds the font and returns the size
     * @param font Font object
     * @return int of the font position
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
        sb.append("<< /NeedAppearances " + (needAppearances == true ? "true" : "false") + "\n");
        sb.append("/Fields [");
        for (int i = 0; i < fields.size(); i++) {
            sb.append(fields.get(i).getObjectId() + " 0 R");
            if (i < fields.size() - 1) {
                sb.append(" ");
            }
        }
        sb.append("]\n");
        if (!fonts.isEmpty()) {
            sb.append("/DR << ");
            sb.append("/Font << ");
            for (int i = 0; i < fonts.size(); i++) {
                sb.append("/F" + (i + 1) + " " + fonts.get(i).getObjectId() + " 0 R ");
            }
            sb.append("/ZaDB << /BaseFont /ZapfDingbats /Subtype /Type1 /Type /Font >>\n");
            sb.append(">> >>\n");
        }
        sb.append(">>\n");
        sb.append("endobj\n");

        return sb.toString().getBytes(StandardCharsets.ISO_8859_1);
    }

    @Override
    public List<Element> buildElementList() {
        List<Element> elements = new ArrayList<>();
        elements.add(this);
        for (Element field : fields) {
            elements.add(field);
        }

        return elements;
    }
    
}
