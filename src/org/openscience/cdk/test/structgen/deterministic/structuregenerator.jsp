<%@ page import="java.util.*" %>
<%@ page import="org.openscience.cdk.*" %>
<%@ page import="org.openscience.cdk.tools.*" %>
<%@ page import="org.openscience.cdk.structgen.deterministic.GENMDeterministicGenerator" %>
<%@ page import="org.openscience.cdk.structgen.deterministic.SmilesViewerforDeterministicGenerator" %>
<%@ page import="org.openscience.cdk.structgen.deterministic.*" %>
<%@ page import="org.openscience.cdk.applications.swing.*" %>
<%@ page import="org.openscience.cdk.Molecule" %>
<%@ page import="org.openscience.cdk.applications.swing.MoleculeListViewer" %>
<%@ page import="org.openscience.cdk.applications.swing.MoleculeViewer2D" %>
<%@ page import="org.openscience.cdk.layout.StructureDiagramGenerator" %>
<%@ page import="org.openscience.cdk.smiles.SmilesParser" %>

<HTML>
<HEAD>
</HEAD>
<P ALIGN=CENTER><FONT FACE="Georgia, serif"><FONT SIZE=6 STYLE="font-size: 22pt">Deterministic
Structure Generator</FONT></FONT></P>


<BODY>
<% 
long elapsedTime=0;
int number=0;
int i=0;
Vector smiles=new Vector();
String molecularformula=request.getParameter("molecularformula");
String mf=molecularformula.toUpperCase();
MFAnalyser mfa = new MFAnalyser(mf);
int[] mF=new int[12];
 mF[1]=mfa.getAtomCount("C");
 mF[2]=mfa.getAtomCount("H");
 mF[3]=mfa.getAtomCount("O");
 mF[4]=mfa.getAtomCount("N");
 mF[5]=mfa.getAtomCount("S");
 mF[6]=mfa.getAtomCount("P");
 mF[7]=mfa.getAtomCount("Si");
 mF[8]=mfa.getAtomCount("F");
 mF[9]=mfa.getAtomCount("Cl");
 mF[10]=mfa.getAtomCount("Br");
 mF[11]=mfa.getAtomCount("I");
 mF[0]=2*mF[1]+mF[4]+mF[6]+2*mF[7]+2-mF[2]-mF[8]-mF[9]-mF[10]-mF[11];
 if(mF[0]<0){
%>
	not a correct formula!
<%}
else
{
	long startingTime=System.currentTimeMillis();
	GENMDeterministicGenerator dsg=new GENMDeterministicGenerator(mf);
	long endingTime=System.currentTimeMillis();
	elapsedTime=(endingTime-startingTime)/1000;
	number=dsg.getNumberOfStructure();
	smiles=dsg.getSMILES();
}
%>

<P ALIGN=CENTER >
There are <b><%= number %></b> constutional isomers with molecular formula. </P>
	<P ALIGN=CENTER>Total computational time is <b><%= elapsedTime %></b>S.</P>
</P>

<TABLE>
<TABLE ALIGN=RIGHT BORDER="1" CELLPADDING="3" CELLSPACING="1"> 
<TR> <TH>SN</TH> 
<TH>SMILES of the structure</TH>
 </TR> 
<% for(i=0;i<smiles.size();i++){%>

<TR>
	<TD>
		<CENTER><%= i+1 %></CENTER>
	</TD>
	<TD>
		<CENTER><%= smiles.get(i) %></CENTER>
	</TD>
</TR>

<%}%>
</TABLE>
<Table ALIGN=LEFT BORDER="1" CELLPADDING="3" CELLSPACING="1"> 
	<TR>An applet to display the structure.Ony putting the left SMILES into the textfield.</TR>
	<TR>
	<APPLET CODE="org.openscience.cdk.structgen.deterministic.SmilesViewerforDeterministicGenerator.class" ARCHIVE="jar/xalan-2.0.1.jar" WIDTH=300 HEIGHT=200>
	</TR>
</TABLE>
</BODY>
</HTML>