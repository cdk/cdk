package uk.ac.ebi.beam;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/** @author John May */
public class AtomBuilderTest {

    @Test public void aliphatic_element_c() throws Exception {
        Atom a = AtomBuilder.aliphatic(Element.Carbon)
                            .build();
        assertThat(a.element(), is(Element.Carbon));
        assertThat(a.isotope(), is(-1));
        assertThat(a.charge(), is(0));
        assertThat(a.atomClass(), is(0));
        assertThat(a.aromatic(), is(false));
    }

    @Test public void aliphatic_element_n() throws Exception {
        Atom a = AtomBuilder.aliphatic(Element.Nitrogen)
                            .build();
        assertThat(a.element(), is(Element.Nitrogen));
        assertThat(a.isotope(), is(-1));
        assertThat(a.charge(), is(0));
        assertThat(a.atomClass(), is(0));
        assertThat(a.aromatic(), is(false));
    }

    @Test(expected = NullPointerException.class)
    public void aliphatic_element_null() throws Exception {
        Atom a = AtomBuilder.aliphatic((Element) null)
                            .build();
    }

    @Test public void aromatic_element_c() throws Exception {
        Atom a = AtomBuilder.aromatic(Element.Carbon)
                            .build();
        assertThat(a.element(), is(Element.Carbon));
        assertThat(a.isotope(), is(-1));
        assertThat(a.charge(), is(0));
        assertThat(a.atomClass(), is(0));
        assertThat(a.aromatic(), is(true));
    }

    @Test public void aromatic_element_n() throws Exception {
        Atom a = AtomBuilder.aromatic(Element.Nitrogen)
                            .build();
        assertThat(a.element(), is(Element.Nitrogen));
        assertThat(a.isotope(), is(-1));
        assertThat(a.charge(), is(0));
        assertThat(a.atomClass(), is(0));
        assertThat(a.aromatic(), is(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void aromatic_element_cl() throws Exception {
        Atom a = AtomBuilder.aromatic(Element.Chlorine)
                            .build();
    }

    @Test(expected = NullPointerException.class)
    public void aromatic_element_null() throws Exception {
        Atom a = AtomBuilder.aromatic((Element) null)
                            .build();
    }

    @Test public void aliphatic_symbol_c() throws Exception {
        Atom a = AtomBuilder.aliphatic("C")
                            .build();
        assertThat(a.element(), is(Element.Carbon));
        assertThat(a.isotope(), is(-1));
        assertThat(a.charge(), is(0));
        assertThat(a.atomClass(), is(0));
        assertThat(a.aromatic(), is(false));
    }

    @Test public void aliphatic_symbol_n() throws Exception {
        Atom a = AtomBuilder.aliphatic("N")
                            .build();
        assertThat(a.element(), is(Element.Nitrogen));
        assertThat(a.isotope(), is(-1));
        assertThat(a.charge(), is(0));
        assertThat(a.atomClass(), is(0));
        assertThat(a.aromatic(), is(false));
    }

    @Test(expected = NullPointerException.class)
    public void aliphatic_symbol_null() throws Exception {
        Atom a = AtomBuilder.aliphatic((String) null)
                            .build();
    }

    @Test public void aromatic_symbol_c() throws Exception {
        Atom a = AtomBuilder.aromatic("C")
                            .build();
        assertThat(a.element(), is(Element.Carbon));
        assertThat(a.isotope(), is(-1));
        assertThat(a.charge(), is(0));
        assertThat(a.atomClass(), is(0));
        assertThat(a.aromatic(), is(true));
    }

    @Test public void aromatic_symbol_n() throws Exception {
        Atom a = AtomBuilder.aromatic("N")
                            .build();
        assertThat(a.element(), is(Element.Nitrogen));
        assertThat(a.isotope(), is(-1));
        assertThat(a.charge(), is(0));
        assertThat(a.atomClass(), is(0));
        assertThat(a.aromatic(), is(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void aromatic_symbol_cl() throws Exception {
        Atom a = AtomBuilder.aromatic("Cl")
                            .build();
    }

    @Test(expected = NullPointerException.class)
    public void aromatic_symbol_null() throws Exception {
        Atom a = AtomBuilder.aromatic((String) null)
                            .build();
    }

    @Test public void create_symbol_aliphatic_c() throws Exception {
        Atom a = AtomBuilder.create("C")
                            .build();
        assertThat(a.element(), is(Element.Carbon));
        assertThat(a.isotope(), is(-1));
        assertThat(a.charge(), is(0));
        assertThat(a.atomClass(), is(0));
        assertThat(a.aromatic(), is(false));
    }

    @Test public void create_symbol_aromatic_c() throws Exception {
        Atom a = AtomBuilder.create("c")
                            .build();
        assertThat(a.element(), is(Element.Carbon));
        assertThat(a.isotope(), is(-1));
        assertThat(a.charge(), is(0));
        assertThat(a.atomClass(), is(0));
        assertThat(a.aromatic(), is(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void create_symbol_non_aromatic() throws Exception {
        Atom a = AtomBuilder.create("cl")
                            .build();
    }

    @Test
    public void create_symbol_defaultToUnknown() throws Exception {
        Atom a = AtomBuilder.create("N/A")
                            .build();
        assertThat(a.element(), is(Element.Unknown));
    }

    @Test
    public void create_symbol_null() throws Exception {
        Atom a = AtomBuilder.create((String) null)
                            .build();
        assertThat(a.element(), is(Element.Unknown));
    }

    @Test public void aliphatic_charged_carbon_minus2() {
        Atom a = AtomBuilder.aliphatic(Element.Carbon)
                            .charge(-2)
                            .build();
        assertThat(a.element(), is(Element.Carbon));
        assertThat(a.charge(), is(-2));
    }

    @Test public void aliphatic_charged_carbon_plus2() {
        Atom a = AtomBuilder.aliphatic(Element.Carbon)
                            .charge(+2)
                            .build();
        assertThat(a.element(), is(Element.Carbon));
        assertThat(a.charge(), is(+2));
    }

    @Test public void aliphatic_charged_carbon_anion() {
        Atom a = AtomBuilder.aliphatic(Element.Carbon)
                            .anion()
                            .build();
        assertThat(a.element(), is(Element.Carbon));
        assertThat(a.charge(), is(-1));
    }

    @Test public void aliphatic_charged_carbon_plus1() {
        Atom a = AtomBuilder.aliphatic(Element.Carbon)
                            .cation()
                            .build();
        assertThat(a.element(), is(Element.Carbon));
        assertThat(a.charge(), is(+1));
    }

    @Test public void aliphatic_carbon_13() {
        Atom a = AtomBuilder.aliphatic(Element.Carbon)
                            .isotope(13)
                            .build();
        assertThat(a.element(), is(Element.Carbon));
        assertThat(a.isotope(), is(13));
    }

    @Test public void aliphatic_carbon_14() {
        Atom a = AtomBuilder.aliphatic(Element.Carbon)
                            .isotope(13)
                            .build();
        assertThat(a.element(), is(Element.Carbon));
        assertThat(a.isotope(), is(13));
    }

    @Test public void aliphatic_carbon_class1() {
        Atom a = AtomBuilder.aliphatic(Element.Carbon)
                            .atomClass(1)
                            .build();
        assertThat(a.element(), is(Element.Carbon));
        assertThat(a.atomClass(), is(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void aliphatic_carbon_class_negative() {
        Atom a = AtomBuilder.aliphatic(Element.Carbon)
                            .atomClass(-10)
                            .build();
    }

    @Test
    public void aliphatic_carbon_3_hydrogens() {
        Atom a = AtomBuilder.aliphatic(Element.Carbon)
                            .hydrogens(3)
                            .build();
        assertThat(a.element(), is(Element.Carbon));
        assertThat(a.hydrogens(), is(3));
    }

    @Test(expected = IllegalArgumentException.class)
    public void aliphatic_carbon_negative_hydrogens() {
        Atom a = AtomBuilder.aliphatic(Element.Carbon)
                            .hydrogens(-3)
                            .build();
    }

    @Test public void aromatic_unknown_from_element() {
        assertNotNull(AtomBuilder.aromatic(Element.Unknown)
                                 .build());
    } 
    
    @Test public void aromatic_unknown_from_symbol() {
        assertNotNull(AtomBuilder.aromatic("*")
                                 .build());
        assertNotNull(AtomBuilder.aromatic("R")
                                 .build());
    }
}
