import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.structgen.IStructureGenerationListener;
import org.openscience.cdk.structgen.deterministic.GENMDeterministicGenerator;


public class PerfDetermStructGen {

	GENMDeterministicGenerator gdg;
	StructureGenerationCounterListener myListener;
	
	public PerfDetermStructGen(String formula) throws Exception {
		gdg = new GENMDeterministicGenerator("" + formula,"");
		System.out.println("MF: " + formula);
		// myListener = new StructureGenerationCounterListener(); 
		// gdg.addListener(myListener);
	}
	
	public void run() throws Exception {
		long startTime = System.currentTimeMillis();
		gdg.generate();
		long endTime = System.currentTimeMillis();
		System.out.println("Time consumed (ms): " + (endTime - startTime));
		System.out.println("Structures found: " + gdg.getNumberOfStructures());
	}
	
	public static void main(String[] args) throws Exception {
		PerfDetermStructGen test = new PerfDetermStructGen(args[0]);
		test.run();
//		System.in.read();
	}
	
	class MyStructureGenerationListener implements IStructureGenerationListener {

		private List structures;
		
		public MyStructureGenerationListener() {
			structures = new ArrayList();
		}
		
		public void stateChanged(List list) throws Exception {
			structures.addAll(list);
		}
		
		public List getStructures() {
			return structures;
		}
		
	}

	class StructureGenerationCounterListener implements IStructureGenerationListener {

		private int count;
		
		public StructureGenerationCounterListener() {
			count = 0;
		}
		
		public void stateChanged(List list) throws Exception {
			count += list.size();
		}
		
		public int getCount() {
			return count;
		}
		
	}
}
