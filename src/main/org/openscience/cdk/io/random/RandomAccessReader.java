/* $Revision:$
 * 
 * Copyright (C) 2005-2008   Nina Jeliazkova <nina@acad.bg>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.io.random;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.StringReader;

import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.IChemObjectReader;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.ReaderEvent;
import org.openscience.cdk.io.iterator.DefaultIteratingChemObjectReader;
import org.openscience.cdk.io.listener.IChemObjectIOListener;
import org.openscience.cdk.io.listener.IReaderListener;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Random access to text files of compounds. 
 * Reads the file as a text and builds an index file, if the index file doesn't already exist.
 * The index stores offset, length and a third field reserved for future use.
 * Subsequent access for a record N uses this index to seek the record and return the molecule.
 * Useful for very big files.
 *  
 * @author Nina Jeliazkova nina@acad.bg
 */
public abstract class RandomAccessReader extends DefaultIteratingChemObjectReader implements IRandomAccessChemObjectReader<IChemObject> {
    protected static LoggingTool logger = new LoggingTool(RandomAccessReader.class);
    protected RandomAccessFile raFile;
    protected IOSetting[] headerOptions = null;
    private final String filename;
    protected ISimpleChemObjectReader chemObjectReader;
    protected int indexVersion=1;
    /*
     * index[record][0]  - record offset in file
     * index[record][1]  - record length
     * index[record][2]  - number of atoms (if available)
     */
    protected long[][] index=null;
    protected int records;
    protected int currentRecord = 0;
    protected byte[] b;
    protected IChemObjectBuilder builder;
    protected boolean indexCreated = false;
    
    /**
     * Reads the file and builds an index file, if the index file doesn't already exist.
     * @param file
     * @param builder
     * @throws IOException
     */
    public RandomAccessReader(File file,IChemObjectBuilder builder) throws IOException {
        this(file,builder,null);
    }
    
    /**
    * Reads the file and builds an index file, if the index file doesn't already exist.
     * @param file
     * @param builder
     * @param listener
     * @throws IOException
     */
    public RandomAccessReader(File file,IChemObjectBuilder builder,IReaderListener listener) throws IOException {        
        super();
        this.filename = file.getAbsolutePath();
        this.builder = builder;
        setChemObjectReader(createChemObjectReader());
        if (listener != null) addChemObjectIOListener(listener);
        raFile = new RandomAccessFile(file,"r");
        records = 0;
        setIndexCreated(false);
        indexTheFile();

    }
    @Override
    protected void finalize() throws Throwable {
        try {
        close();
        } catch (Exception x) {
            
        }
        super.finalize();
    }
    /**
     * Returns the object at given record No. Record numbers are zero-based!
     */
    public synchronized IChemObject readRecord(int record) throws Exception {
    	String buffer = readContent(record);
        if (chemObjectReader == null) throw new CDKException("No chemobject reader!");
        else { 
            chemObjectReader.setReader(new StringReader(buffer));
            currentRecord = record;            
            return  processContent();
        }
    }
    /**
     * Reads the record text content into a String
     * @param record
     * @return
     * @throws Exception
     */
    protected String readContent(int record) throws Exception {
        logger.debug("Current record ",record);

        if ((record < 0) || (record >=records)) {
            throw new CDKException("No such record "+record);
        }
        //fireFrameRead();

        raFile.seek(index[record][0]);
        int length = (int)index[record][1];
        raFile.read(b,0,length);    	
        return new String(b,0,length);
    }
    /**
     * The reader is already set to read the record buffer, 
     * @return
     * @throws CDKException
     */
    protected IChemObject processContent() throws CDKException {
        return chemObjectReader.read(builder.newChemFile());
    }


    protected long[][] resize(long[][] index, int newLength) {
        long[][] newIndex = new long[newLength][3];
        for (int i=0; i < index.length;i++) {
            newIndex[i][0] = index[i][0];
            newIndex[i][1] = index[i][1];
            newIndex[i][2] = index[i][2];
        }
        return newIndex;
                                      
    }    
    protected abstract boolean isRecordEnd(String line);
        
    protected synchronized void saveIndex(File file) throws Exception {
    	if (records == 0) {file.delete(); return;}
        FileWriter out = new FileWriter(file);
        out.write(Integer.toString(indexVersion));
        out.write('\n');
        out.write(filename);
        out.write('\n');
        out.write(Long.toString(raFile.length()));
        out.write('\n');
        out.write(Integer.toString(records));
        out.write('\n');
        for (int i=0; i < records;i++) {
            out.write(Long.toString(index[i][0]));
            out.write('\t');
            out.write(Long.toString(index[i][1]));
            out.write('\t');
            out.write(Long.toString(index[i][2]));
            out.write('\n');
        }
        out.write(Integer.toString(records));
        out.write('\n');
        out.write(filename);
        out.write('\n');
        out.close();
    }
    
    protected synchronized void loadIndex(File file) throws Exception {
    	BufferedReader in = new BufferedReader(new FileReader(file));
        String version = in.readLine();
        try {
	        if (Integer.parseInt(version) != indexVersion) {
	        	in.close();
	        	throw new Exception("Expected index version "+indexVersion+" instead of "+version);
	        }
        } catch (Exception x) {
        	in.close();
        	throw new Exception("Invalid index version "+version);        	
        }
        String fileIndexed = in.readLine();
        if (!filename.equals(fileIndexed)) {
        	in.close();
        	throw new Exception("Index for " + fileIndexed + " found instead of "+filename + ". Creating new index.");
        }
        String line = in.readLine();
        int fileLength = Integer.parseInt(line);
        if (fileLength != raFile.length()) {
        	in.close();
        	throw new Exception("Index for file of size " + fileLength + " found instead of "+raFile.length());
        }
        line = in.readLine();
        int indexLength = Integer.parseInt(line);
        if (indexLength <= 0 ) {
        	in.close();
        	throw new Exception("Index of zero lenght! "+file.getAbsolutePath());
        }
        index = new long[indexLength][3];
        records = 0;
        int maxRecordLength = 0;
        for (int i=0; i < index.length;i++) {
        	line = in.readLine();
        	String[] result = line.split("\t");
        	for (int j=0;j<3;j++) 
        		try {
        			index[i][j] = Long.parseLong(result[j]);

        		} catch (Exception x) {
        			in.close();
        			throw new Exception("Error reading index! "+result[j],x);
        		}
    		        		
            if (maxRecordLength < index[records][1]) 
                maxRecordLength = (int) index[records][1];
            records++;
        }
        
        line = in.readLine();
        int indexLength2 = Integer.parseInt(line);
        if (indexLength2 <= 0 ) {in.close(); throw new Exception("Index of zero lenght!");}
        if (indexLength2 != indexLength ) { in.close(); throw new Exception("Wrong index length!");}
        line = in.readLine();
        if (!line.equals(filename)) {in.close(); throw new Exception("Index for " + line + " found instead of "+filename);}
        in.close();
        
        b = new byte[maxRecordLength];
        //fireFrameRead();
    }
    /**
     * The index file {@link #getIndexFile(String)} is loaded, if already exists, or created a new.
     * @throws Exception
     */
    protected synchronized void makeIndex() throws Exception {
    	File indexFile = getIndexFile(filename);
    	if (indexFile.exists()) 
    		try {
    			loadIndex(indexFile);
    			setIndexCreated(true);
    			return;
    		} catch (Exception x) {
    			logger.warn(x.getMessage());
    		}
    	indexCreated = false;    		
        long now = System.currentTimeMillis();
        int recordLength = 1000;
        int maxRecords = 1;
        int maxRecordLength = 0;
        maxRecords = (int)raFile.length()/recordLength;
        if (maxRecords == 0) maxRecords = 1;
        index = new long[maxRecords][3];

        String s = null;
        long start = 0;
        long end = 0;
        raFile.seek(0);
        records = 0;
        recordLength = 0;
        while ((s = raFile.readLine()) != null) {
            if (start == -1) start = raFile.getFilePointer(); 
            if (isRecordEnd(s)) {
                //fireFrameRead();
                if (records >= maxRecords) {
                    double averageLength = recordLength/records;
                    //records + (rf.length()-rf.getFilePointer())/averageLength
                    index = resize(index,records + 
                            (int) (records + (raFile.length()-records*raFile.getFilePointer())/recordLength));
            	}
                end += 4;
                index[records][0] = start;
                index[records][1] = end - start;
                index[records][2] = -1;
                if (maxRecordLength < index[records][1]) 
                    maxRecordLength = (int) index[records][1];
                records++;
                recordLength += end-start; 
               
                start = raFile.getFilePointer();
            } else {
                end = raFile.getFilePointer(); 
            }
            
        }
        b = new byte[maxRecordLength];
        //fireFrameRead();
        logger.info("Index created in "+ (System.currentTimeMillis()-now) + " ms.");
        try {
            saveIndex(indexFile);
        } catch (Exception x) {
            logger.error(x);
        }
    }

    /**
     * Opens the file index <filename>_cdk.index</filename> in a temporary folder, as specified by "java.io.tmpdir" property.
     * 
     * @param filename
     * @throws Exception
     */
	public static File getIndexFile(String filename) throws Exception {
		String tmpDir = System.getProperty("java.io.tmpdir");
        File f = new File(filename);
        File indexFile = new File(tmpDir,f.getName()+"_cdk.index");
        f = null;
        return indexFile;
	}

    /* (non-Javadoc)
     * @see java.io.Closeable#close()
     */
	@TestMethod("testClose")
  public void close() throws IOException {
        raFile.close();
        //TODO
        //removeChemObjectIOListener(listener)

    }

    public synchronized IChemObjectReader getChemObjectReader() {
        return chemObjectReader;
    }
    public abstract ISimpleChemObjectReader createChemObjectReader();
    
    public synchronized void setChemObjectReader(
    		ISimpleChemObjectReader chemObjectReader) {
        this.chemObjectReader = chemObjectReader;
    }
    /* (non-Javadoc)
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
        return currentRecord < (records-1);
    }
    public boolean hasPrevious() {
        return currentRecord > 0;
    }

    public IChemObject first() {
        try {
            return readRecord(0);
        } catch (Exception x) {
            logger.error(x);
            return null;
        }
    }

    public IChemObject last() {
        try {
            return readRecord(records-1);
        } catch (Exception x) {
            logger.error(x);
            return null;
        }
    }
    
    /* (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    public IChemObject next() {
        try {
            return readRecord(currentRecord+1);
        } catch (Exception x) {
            logger.error(x);
            return null;
        }
    }
    /* (non-Javadoc)
     */
    public IChemObject previous() {
        try {
            return readRecord(currentRecord-1);
        } catch (Exception x) {
            logger.error(x);
            return null;
        }
    }
    public void set(IChemObject arg0) {
    	
    	
    }
    public void add(IChemObject arg0) {
    	
    	
    }
    public int previousIndex() {
    	return currentRecord-1;
    }
    public int nextIndex() {
    	return currentRecord+1;
    }    
    public int size() {
        return records;
    }
    public void addChemObjectIOListener(IChemObjectIOListener listener) {
        super.addChemObjectIOListener(listener);
        if (chemObjectReader != null)
        chemObjectReader.addChemObjectIOListener(listener);
    }

    public void removeChemObjectIOListener(IChemObjectIOListener listener) {
        super.removeChemObjectIOListener(listener);
        if (chemObjectReader != null)
        chemObjectReader.removeChemObjectIOListener(listener);
    }

    public synchronized int getCurrentRecord() {
        return currentRecord;
    }

	public synchronized boolean isIndexCreated() {
		return indexCreated;
	}

	public synchronized void setIndexCreated(boolean indexCreated) {
		this.indexCreated = indexCreated;
		notifyAll();
	}
	private void indexTheFile() {
        try {
        	setIndexCreated(false);
        	makeIndex();
            currentRecord = 0;
            raFile.seek(index[0][0]);
        	setIndexCreated(true);
        } catch (Exception x) {
        	setIndexCreated(true);
        }
	}
    @Override
    public String toString() {
        return filename;
    }
    
   
}

class RecordReaderEvent extends ReaderEvent {
    /**
	 * 
	 */
	private static final long serialVersionUID = 572155905623474487L;
	protected int record = 0;
    public RecordReaderEvent(Object source,int record) {
        super(source);
        this.record = record;
    }
    public synchronized int getRecord() {
        return record;
    }
}
