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

package com.htmltopdf;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.htmltopdf.element.ContentStream;
import com.htmltopdf.element.Info;
import com.htmltopdf.element.MarkedContentRecord;
import com.htmltopdf.element.Num;
import com.htmltopdf.element.Outline;
import com.htmltopdf.element.Outlines;
import com.htmltopdf.element.Page;
import com.htmltopdf.element.ParentTree;
import com.htmltopdf.element.StructElement;
import com.htmltopdf.element.TextContent;
import com.htmltopdf.element.XmpMetaData;
import com.htmltopdf.fonts.Font;
import com.htmltopdf.fonts.FontName;
import com.htmltopdf.renderer.PDFRenderer;

public class TestPDFRenderer {
    
    @Test
    public void testRenderer() throws IOException {
        render();
    }
    
    private void render() throws IOException {
        List<Num> numbers = new ArrayList<>();
        PDFRenderer renderer = new PDFRenderer();
        renderer.setVersion(2.0f);

        Info infoElement = new Info();
        infoElement.setObjectId(renderer.getNextObjectId());
        infoElement.setTitle("Testing PDF");
        infoElement.setAuthor("Gregory Echelberry");
        infoElement.setSubject("PDF for accessibility testing");
        infoElement.setProducer("PDF Writer version 1.0");
        infoElement.setCreator("PDF Writer");
        renderer.setInfo(infoElement);

        Outlines outlines = new Outlines();
        outlines.setObjectId(renderer.getNextObjectId());
        renderer.getCatalog().setOutlines(outlines);

        Page page = new Page(Page.Size.LETTER);
        renderer.addNewPage(page);

        Outline outline = new Outline();
        outline.setObjectId(renderer.getNextObjectId());
        outline.setTitle("Test Bookmark");
        outline.setPosition(0, 500);
        outline.setParent(outlines);
        outline.setPage(page);
        outlines.addNewOutline(outline);

        StructElement document = new StructElement(StructElement.DOCUMENT);
        document.setObjectId(renderer.getNextObjectId());
        document.setParent(renderer.getCatalog().getStructTreeRoot());
        renderer.getCatalog().getStructTreeRoot().addKid(document);

        StructElement header = new StructElement(StructElement.H);
        header.setObjectId(renderer.getNextObjectId());
        header.setPage(page);
        header.setParent(document);
        document.addKid(header);
        
        ContentStream contentStream = new ContentStream(renderer.getNextObjectId());
        contentStream.setCompressContent(false);
        page.addContent(contentStream);
        
        Font font = renderer.getFont(FontName.COURIER);
        int mcid = renderer.getNextMcid();
        TextContent textContent = new TextContent(page, StructElement.H, font, 8, mcid);
        textContent.addLines(Arrays.asList("Test header"));
        textContent.setPosition(25, 500);
        contentStream.addContent(textContent);

        MarkedContentRecord mcr = new MarkedContentRecord(page);
        mcr.setObjectId(renderer.getNextObjectId());
        mcr.setMcid(mcid);
        header.addKid(mcr);

        Num nums = new Num();
        nums.addReference(header);
        numbers.add(nums);

        ParentTree parentTree = new ParentTree();
        parentTree.setObjectId(renderer.getNextObjectId());
        for (Num num : numbers) {
            num.setObjectId(renderer.getNextObjectId());
            parentTree.addNums(num);
        }

        XmpMetaData metaData = new XmpMetaData();
        metaData.setObjectId(renderer.getNextObjectId());
        metaData.setInfo(infoElement);
        renderer.getCatalog().setMetaData(metaData);
        renderer.getCatalog().getStructTreeRoot().setParentTree(parentTree);

        try (FileOutputStream outputStream = new FileOutputStream("TestDocument.pdf")) {
            renderer.write(outputStream);
        }
    }
}
