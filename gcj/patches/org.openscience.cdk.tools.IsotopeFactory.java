66,75c66
<         InputStream ins = null;
<         ObjIn in = null;
<         try {
<             ins = this.getClass().getClassLoader().getResourceAsStream("org/openscience/cdk/config/isotopes.xml");
<         } catch(Exception exc) {
<             throw new IOException("There was a problem getting org.openscience.cdk.config.isotopes.xml as a stream");
<         }
<         if (ins == null) throw new IOException("There was a problem getting org.openscience.cdk.config.isotopes.xml as a stream");
<         in = new ObjIn(ins, new Config().aliasID(false));
<         isotopes = (Vector) in.readObject();
---
>         isotopes = new Vector();
106,113c97
<         for (int f = 0; f < isotopes.size(); f++) {
<             if (((Isotope)isotopes.elementAt(f)).getSymbol().equals(symbol)) {
<                 if ((((Isotope)isotopes.elementAt(f))).getNaturalAbundance() == ((double)100)) {
<                     return (Isotope)((Isotope)isotopes.elementAt(f)).clone();
<                 }
<             }
<         }
<         return null;
---
>         return new Isotope(symbol);
124,129c108
<         Isotope isotope = null;
<         for (int f = 0; f < isotopes.size(); f++) {
<             if (((Isotope) isotopes.elementAt(f)).getSymbol().equals(symbol)) {
<                 al.add((Isotope) ((Isotope) isotopes.elementAt(f)).clone());
<             }
<         }
---
>         al.add(new Isotope(symbol));
142,148d120
<         for (int f = 0; f < isotopes.size(); f++) {
<             if (((Isotope) isotopes.elementAt(f)).getAtomicNumber() == atomicNumber) {
<                 if ((((Isotope)    isotopes.elementAt(f))).getNaturalAbundance() == ((double)100)) {
<                     return (Isotope) ((Isotope) isotopes.elementAt(f)).clone();
<                 }
<             }
<         }
168d139
<         atom.setAtomicMass(isotope.getAtomicMass());
170,172d140
<         atom.setExactMass(isotope.getExactMass());
<         atom.setAtomicNumber(isotope.getAtomicNumber());
<         atom.setNaturalAbundance(isotope.getNaturalAbundance());
