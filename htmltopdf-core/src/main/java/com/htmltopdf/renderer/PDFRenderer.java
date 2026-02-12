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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.fontbox.ttf.HeaderTable;
import org.apache.fontbox.ttf.HorizontalHeaderTable;
import org.apache.fontbox.ttf.OS2WindowsMetricsTable;
import org.apache.fontbox.ttf.PostScriptTable;
import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TrueTypeFont;

import com.htmltopdf.element.Catalog;
import com.htmltopdf.element.Element;
import com.htmltopdf.element.EncryptionElement;
import com.htmltopdf.element.Info;
import com.htmltopdf.element.Page;
import com.htmltopdf.element.Pages;
import com.htmltopdf.element.StructTreeRoot;
import com.htmltopdf.fonts.Font;
import com.htmltopdf.fonts.FontDecriptor;
import com.htmltopdf.fonts.FontFile;
import com.htmltopdf.fonts.FontName;

/**
 * Class that manages all the PDF structure object
 */
public class PDFRenderer {
    private int objectId = 1;
    private int parentTreeNextKey = -1;
    private int nextMcid = 0;
    private Catalog catalog;
    private List<Font> fonts;
    private Info info;
    private EncryptionElement encryptionElement;

    /**
     * Constructor - Initialize renderer
     */
    public PDFRenderer() {
        fonts = new ArrayList<>();
        catalog = new Catalog();
        catalog.setObjectId(getNextObjectId());
        addStructTreeRoot();
        addPages();
    }

    /**
     * Sets the document version
     * @param version float of the version
     */
    public void setVersion(float version) {
        catalog.setVersion(version);
    }

    /**
     * Set the document information object
     * @param info Info object
     */
    public void setInfo(Info info) {
        this.info = info;
    }

    /**
     * Sets the encryption element object
     * @param encryptionElement EncryptionElement object
     */
    public void setEncryptionElement(EncryptionElement encryptionElement) {
        this.encryptionElement = encryptionElement;
    }

    /**
     * Gets the current object Id and then increments by 1
     * @return int of the object Id
     */
    public int getNextObjectId() {
        int id = objectId;
        objectId++;
        return id;
    }

    /**
     * Get the next MCID and the increments the MCID by 1
     * @return int of the next mcid
     */
    public int getNextMcid() {
        int mcid = nextMcid;
        nextMcid++;
        return mcid;
    }

    /**
     * Gets the next parent tree key then increments by 1
     * @return int of the next parent tree key
     */
    public int getNextParentTreeKey() {
        int key = parentTreeNextKey;
        parentTreeNextKey++;
        return key;
    }

    /**
     * Get the Catalog object
     * @return Catalog object
     */
    public Catalog getCatalog() {
        return catalog;
    }

    public void addNewPage(Page page) {
        page.setObjectId(getNextObjectId());
        page.setParent(catalog.getPages());
        page.setStructParents(getNextParentTreeKey());
        catalog.getStructTreeRoot().setParentTreeNextKey(parentTreeNextKey);
        nextMcid = 0;
        catalog.getPages().addKid(page);
    }

    /**
     * Adds a font to the document
     * This is for fonts that are copied from
     * a PDF. Not for use with fonts used in generation
     * from within this library, use getFont() instead
     * @param font Font object
     */
    public void addFont(Font font) {
        fonts.add(font);
    }


    /**
     * Gets the font object to add to a page
     * @param fontName FontName for the desired font
     * @return Font object
     * @throws FileNotFoundException throws if the font file is not found
     * @throws IOException throws if other I/O exception occurs
     */
    @SuppressWarnings("unlikely-arg-type")
    public Font getFont(FontName fontName) throws FileNotFoundException, IOException {
        for (Font font : fonts) {
            if (font.equals(fontName)) {
                return font;
            }
        }

        Font newFont = new Font(getNextObjectId(), fontName);
        TTFParser parser = new TTFParser();
        InputStream file = new FileInputStream(fontName.getPath());
        try (TrueTypeFont ttFont = parser.parseEmbedded(file)) {
            newFont.setUnitsPerEm(ttFont.getHeader().getUnitsPerEm());
            setRangeOfGlyphs(ttFont, newFont);
            newFont.buildWidthArray();

            FontDecriptor fontDecriptor = new FontDecriptor(fontName.getName());
            fontDecriptor.setObjectId(getNextObjectId());
            setFontDescriptor(ttFont, fontDecriptor);
            newFont.setFontDescriptor(fontDecriptor);

            FontFile fontFile = new FontFile(fontName.getPath());
            fontFile.setObjectId(getNextObjectId());
            fontDecriptor.setFontFile(fontFile);
            fonts.add(newFont);
        }

        return newFont;
    }

    public void write(FileOutputStream outputStream) throws IOException {
        List<Element> elements = catalog.buildElementList();
        for (Font font : fonts) {
            elements.addAll(font.buildElementList());
        }
        elements.add(info);
        elements.sort(Comparator.comparingInt(Element::getObjectId));
        PDFWriter writer = new PDFWriter();
        writer.write(outputStream, elements, info, catalog.getMetaData().getDocumentId(), encryptionElement != null ? encryptionElement.getObjectId() : 0, encryptionElement != null ? encryptionElement.getEncryptionKey() : null);
    }

    /**
     * Adds the Pages to the catalog
     */
    private void addPages() {
        Pages pages = new Pages(getNextObjectId());
        catalog.setPages(pages);
    }

    /**
     * Initializes the StructTreeRoot and adds it to the catalog
     */
    private void addStructTreeRoot() {
        StructTreeRoot structTreeRoot = new StructTreeRoot();
        structTreeRoot.setObjectId(getNextObjectId());
        structTreeRoot.setParentTreeNextKey(getNextParentTreeKey());
        catalog.setStructTreeRoot(structTreeRoot);
    }

    /**
     * Sets the range of glyph ids for a new font
     * @param ttFont TrueTypeFont object
     * @param font Font object
     * @throws IOException throws IOException if file I/O fails
     */
    private void setRangeOfGlyphs(TrueTypeFont ttFont, Font font) throws IOException {
        int firstChar = 255;
        int lastChar = 0;

        for (int code = 32; code <= 126; code++) {
            int glyphId = ttFont.getUnicodeCmapLookup().getGlyphId(code);
            if (glyphId > 0) {
                if (code < firstChar)
                    firstChar = code;
                if (code > lastChar)
                    lastChar = code;
            }
        }

        font.setFirstChar(firstChar);
        font.setLastChar(lastChar);
    }

    /**
     * Initializes the Font Descriptor object
     * @param ttFont TrueTypeFont object
     * @param fontDecriptor FontDescriptor object
     * @throws IOException throws IOException on file I/O error
     */
    private void setFontDescriptor(TrueTypeFont ttFont, FontDecriptor fontDecriptor) throws IOException {
        OS2WindowsMetricsTable metricsTable = ttFont.getOS2Windows();
        PostScriptTable postScriptTable = ttFont.getPostScript();

        // Set Flags
        int flags = 0;

        // Monospaced
        if (metricsTable != null && metricsTable.getPanose() != null && metricsTable.getPanose()[3] == 9) {
            flags |= 1;
        }

        // Italic
        if (postScriptTable != null && postScriptTable.getItalicAngle() != 0f) {
            flags |= 64;
        }

        // Symboic/Serif hueristic
        flags |= 32;
        fontDecriptor.setFlags(flags);

        // Set Font Box
        HeaderTable headerTable = ttFont.getHeader();
        fontDecriptor.setFontBox(String.format("[%d %d %d %d]", headerTable.getXMin(), headerTable.getYMin(), headerTable.getXMax(), headerTable.getYMax()));

        // Set Ascent and Descent
        HorizontalHeaderTable horizontalHeaderTable = ttFont.getHorizontalHeader();
        fontDecriptor.setAscent(horizontalHeaderTable.getAscender());
        fontDecriptor.setDescent(horizontalHeaderTable.getDescender());

        // Set Cap Height
        fontDecriptor.setCapHeight((metricsTable != null && metricsTable.getCapHeight() > 0 ? metricsTable.getCapHeight() : horizontalHeaderTable.getAscender()));

        // Set Italic Angle
        fontDecriptor.setItalicAngle((postScriptTable != null ? postScriptTable.getItalicAngle() : 0f));
    }
}
