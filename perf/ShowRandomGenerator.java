import java.util.Iterator;

import javax.swing.JFrame;

import org.openscience.cdk.applications.swing.MoleculeListViewer;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.structgen.RandomGenerator;
import org.openscience.cdk.structgen.VicinitySampler;
import org.openscience.cdk.structgen.stochastic.operator.CrossoverMachine;


public class ShowRandomGenerator {

	private MoleculeListViewer listViewer;
	
	public ShowRandomGenerator() throws Exception {
		listViewer = new MoleculeListViewer();
        listViewer.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        SmilesParser parser = new SmilesParser(NoNotificationChemObjectBuilder.getInstance());
        
        IMolecule mol = parser.parseSmiles("CNC(C(=O)(C)C");
        listViewer.addStructure(mol, true, true, "parent");

        int i = 0;
    	RandomGenerator machine = new RandomGenerator(mol);
        while (i < 5) {
            machine.mutate(mol);
        	if (ConnectivityChecker.isConnected(mol)) {
        		i++;
        		listViewer.addStructure(mol, true, true, "gen " + i);
        	}
        }
	}

	public static void main(String[] args) throws Exception {
		new ShowRandomGenerator();		
	}
	
}
