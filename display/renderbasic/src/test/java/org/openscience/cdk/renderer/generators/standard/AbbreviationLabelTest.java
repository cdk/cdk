/*
 * Copyright (c) 2015 John May <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.renderer.generators.standard;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class AbbreviationLabelTest {

    @Test
    void carboxylicacid() {
        List<String> tokens = new ArrayList<>();
        Assertions.assertTrue(AbbreviationLabel.parse("COOH", tokens));
        assertThat(tokens.size(), is(4));
        assertThat(tokens, hasItems("C", "O", "O", "H"));
    }

    @Test
    void carboxylate() {
        List<String> tokens = new ArrayList<>();
        Assertions.assertTrue(AbbreviationLabel.parse("COO-", tokens));
        assertThat(tokens.size(), is(4));
        assertThat(tokens, hasItems("C", "O", "O", "-"));
    }

    @Test
    void trifluromethyl() {
        List<String> tokens = new ArrayList<>();
        Assertions.assertTrue(AbbreviationLabel.parse("CF3", tokens));
        assertThat(tokens.size(), is(2));
        assertThat(tokens, hasItems("C", "F3"));
    }

    @Test
    void triphenylmethyl() {
        List<String> tokens = new ArrayList<>();
        Assertions.assertTrue(AbbreviationLabel.parse("CPh3", tokens));
        assertThat(tokens.size(), is(2));
        assertThat(tokens, hasItems("C", "Ph3"));
    }

    @Test
    void tertbutyls() {
        List<String> tokens = new ArrayList<>(1);
        for (String str : Arrays.asList("tBu", "tertBu", "t-Bu", "t-Butyl", "tertButyl")) {
            tokens.clear();
            Assertions.assertTrue(AbbreviationLabel.parse(str, tokens), str);
            assertThat(tokens.size(), is(1));
        }
    }

    @Test
    void peglinker() {
        List<String> tokens = new ArrayList<>();
        Assertions.assertTrue(AbbreviationLabel.parse("CH2CH2OCH2CH2O", tokens));
        assertThat(tokens.size(), is(10));
        assertThat(tokens, hasItems("C", "H2", "C", "H2", "O", "C", "H2", "C", "H2", "O"));
    }

    @Test
    void parseFeacac3() {
        List<String> tokens = new ArrayList<>();
        Assertions.assertTrue(AbbreviationLabel.parse("Fe(acac)3", tokens));
        assertThat(tokens.size(), is(5));
        assertThat(tokens, hasItems("Fe", "(", "acac", ")", "3"));
    }

    @Test
    void formatFeacac3() {
        List<String> tokens = new ArrayList<>();
        Assertions.assertTrue(AbbreviationLabel.parse("Fe(acac)3", tokens));
        List<AbbreviationLabel.FormattedText> formatted = AbbreviationLabel.format(tokens);
        AbbreviationLabel.reduce(formatted, 0, formatted.size());
        assertThat(formatted.get(0).text, is("Fe(acac)"));
        assertThat(formatted.get(0).style, is(0));
        assertThat(formatted.get(1).text, is("3"));
        assertThat(formatted.get(1).style, is(-1));
    }

    @Test
    void formatRubpy3Cl2() {
        List<String> tokens = new ArrayList<>();
        Assertions.assertTrue(AbbreviationLabel.parse("Ru(bpy)3Cl2", tokens));
        List<AbbreviationLabel.FormattedText> formatted = AbbreviationLabel.format(tokens);
        AbbreviationLabel.reduce(formatted, 0, formatted.size());
        assertThat(formatted.get(0).text, is("Ru(bpy)"));
        assertThat(formatted.get(0).style, is(0));
        assertThat(formatted.get(1).text, is("3"));
        assertThat(formatted.get(1).style, is(-1));
        assertThat(formatted.get(2).text, is("Cl"));
        assertThat(formatted.get(2).style, is(0));
        assertThat(formatted.get(3).text, is("2"));
        assertThat(formatted.get(3).style, is(-1));
    }

    @Test
    void CO2Et() {
        List<String> tokens = new ArrayList<>();
        Assertions.assertTrue(AbbreviationLabel.parse("CO2Et", tokens));
        assertThat(tokens.size(), is(3));
        assertThat(tokens, hasItems("C", "O2", "Et"));
    }

    @Test
    void parseBrackets() {
        List<String> tokens = new ArrayList<>();
        Assertions.assertTrue(AbbreviationLabel.parse("N(CH2CH2O)CH2", tokens));
        assertThat(tokens.size(), is(10));
        assertThat(tokens, hasItems("N", "(", "C", "H2", "C", "H2", "O", ")", "C", "H2"));
    }

    @Test
    void reversingBrackets() {
        List<String> tokens = new ArrayList<>();
        Assertions.assertTrue(AbbreviationLabel.parse("N(CH2CH2O)CH2", tokens));
        AbbreviationLabel.reverse(tokens);
        assertThat(String.join("", tokens), is("H2C(OH2CH2C)N"));
    }

    @Test
    void reversingFormatPOOHOEt() {
        List<String> tokens = new ArrayList<>();
        Assertions.assertTrue(AbbreviationLabel.parse("PO(OH)OEt", tokens));
        AbbreviationLabel.reverse(tokens);
        AbbreviationLabel.format(tokens);
        assertThat(String.join("", tokens), is("EtO(HO)OP"));
    }

    @Test
    void reversingBracketsWithNumbers() {
        List<String> tokens = new ArrayList<>();
        Assertions.assertTrue(AbbreviationLabel.parse("B(OH)2", tokens));
        AbbreviationLabel.reverse(tokens);
        assertThat(String.join("", tokens), is("(HO)2B"));
    }

    @Test
    void hydrateFormatting() {
        List<String> tokens = new ArrayList<>();
        Assertions.assertTrue(AbbreviationLabel.parse("SnCl4.2H2O", tokens));
        assertThat(tokens, is(Arrays.asList("Sn", "Cl4", ".", "2", "H2", "O")));
    }

    @Test
    void nonAbbreviationLabel() {
        List<String> tokens = new ArrayList<>();
        Assertions.assertFalse(AbbreviationLabel.parse("A Random Label - Don't Reverse", tokens));
        assertThat(tokens.size(), is(1));
    }

    @Test
    void formatOPO3() {
        List<String> tokens = Arrays.asList("O", "P", "O3", "-2");
        List<AbbreviationLabel.FormattedText> texts = AbbreviationLabel.format(tokens);
        AbbreviationLabel.reduce(texts, 0, texts.size());
        assertThat(texts.size(), is(3));
        assertThat(texts.get(0).text, is("OPO"));
        assertThat(texts.get(0).style, is(AbbreviationLabel.STYLE_NORMAL));
        assertThat(texts.get(1).text, is("3"));
        assertThat(texts.get(1).style, is(AbbreviationLabel.STYLE_SUBSCRIPT));
        assertThat(texts.get(2).text, is("2−"));
        assertThat(texts.get(2).style, is(AbbreviationLabel.STYLE_SUPSCRIPT));
    }

    @Test
    void formatTBu() {
        List<String> tokens = Arrays.asList("tBu");
        List<AbbreviationLabel.FormattedText> texts = AbbreviationLabel.format(tokens);
        assertThat(texts.size(), is(2));
        assertThat(texts.get(0).text, is("t"));
        assertThat(texts.get(0).style, is(AbbreviationLabel.STYLE_ITALIC));
        assertThat(texts.get(1).text, is("Bu"));
        assertThat(texts.get(1).style, is(AbbreviationLabel.STYLE_NORMAL));
    }

    @Test
    void NEt3DotHCl() {
        List<String> tokens = new ArrayList<>();
        Assertions.assertTrue(AbbreviationLabel.parse("NEt3·HCl", tokens));
        assertThat(tokens.size(), is(5));
        assertThat(tokens.get(0), is("N"));
        assertThat(tokens.get(1), is("Et3"));
        assertThat(tokens.get(2), is("·"));
        assertThat(tokens.get(3), is("H"));
        assertThat(tokens.get(4), is("Cl"));
        List<AbbreviationLabel.FormattedText> formatted = AbbreviationLabel.format(tokens);
        AbbreviationLabel.reduce(formatted, 0, formatted.size());
        assertThat(formatted.size(), is(3));
        assertThat(formatted.get(0).text, is("NEt"));
        assertThat(formatted.get(0).style, is(AbbreviationLabel.STYLE_NORMAL));
        assertThat(formatted.get(1).text, is("3"));
        assertThat(formatted.get(1).style, is(AbbreviationLabel.STYLE_SUBSCRIPT));
        assertThat(formatted.get(2).text, is("·HCl"));
        assertThat(formatted.get(2).style, is(AbbreviationLabel.STYLE_NORMAL));
    }

    @Test
    void formatOPO3H2() {
        List<String> tokens = Arrays.asList("O", "P", "O3", "H2");
        List<AbbreviationLabel.FormattedText> texts = AbbreviationLabel.format(tokens);
        AbbreviationLabel.reduce(texts, 0, texts.size());
        assertThat(texts.size(), is(4));
        assertThat(texts.get(0).text, is("OPO"));
        assertThat(texts.get(0).style, is(0));
        assertThat(texts.get(1).text, is("3"));
        assertThat(texts.get(1).style, is(-1));
        assertThat(texts.get(2).text, is("H"));
        assertThat(texts.get(2).style, is(0));
        assertThat(texts.get(3).text, is("2"));
        assertThat(texts.get(3).style, is(-1));
    }

    @Test
    void hydrate() {
        List<String> tokens = new ArrayList<>();
        AbbreviationLabel.parse("•H2O", tokens);
        assertThat(tokens, is(Arrays.asList("•", "H2", "O")));
    }

    @Test
    void het() {
        // 'Het' not 'He'lium and 't'erts
        Assertions.assertFalse(AbbreviationLabel.parse("Het", new ArrayList<>()));
    }

    @Test
    void parseChargeOnly() {
        Assertions.assertFalse(AbbreviationLabel.parse("+", new ArrayList<>()));
    }

    @Test
    void parseNumberOnly() {
        Assertions.assertFalse(AbbreviationLabel.parse("1", new ArrayList<>()));
    }

    @Test
    void nonAsciiLabel() {
        // phenyl
        Assertions.assertFalse(AbbreviationLabel.parse("苯基", new ArrayList<>()));
    }
}
