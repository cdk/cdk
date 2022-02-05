/*
 * =====================================
 *  Copyright (c) 2022 NextMove Software
 * =====================================
 */

package org.openscience.cdk.structgen.maygen;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.MDLV2000Writer;
import org.openscience.cdk.io.SDFWriter;
import org.openscience.cdk.layout.StructureDiagramGenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

final class SdfOutputConsumer implements Maygen.Consumer {

    private SDFWriter sdfw;
    private final File dir;
    private boolean coordinates;

    public SdfOutputConsumer(File dir) {
        this.dir = dir;
        if (dir == null)
            sdfw = new SDFWriter(System.out);
        else
            dir.mkdirs();
        setupOptions();
    }

    @Override
    public void configure(String name) {
        if (dir != null) {
            if (sdfw != null) {
                try {
                    sdfw.close();
                } catch (IOException e) {
                    System.err.println("ERROR: " + e.getMessage());
                }
            }
            try {
                sdfw = new SDFWriter(new FileOutputStream(new File(dir, name + ".sdf")));
                setupOptions();
            } catch (FileNotFoundException e) {
                System.err.println("ERROR: " + e.getMessage());
            }
        }
    }

    private void setupOptions() {
        if (sdfw == null)
            return;
        try {
            sdfw.getSetting(MDLV2000Writer.OptProgramName)
                .setSetting("MAYGEN");
            sdfw.getSetting(MDLV2000Writer.OptWriteDefaultProperties)
                .setSetting("false");
        } catch (CDKException e) {
        }
    }

    @Override
    public void consume(IAtomContainer mol) {
        try {
            if (coordinates)
                new StructureDiagramGenerator().generateCoordinates(mol);
            sdfw.write(mol);
        } catch (CDKException e) {
            System.err.println("ERROR: " + e.getMessage());
        }
    }

    @Override
    public void close() throws IOException {
        sdfw.close();
    }

    public void setCoordinates(boolean b) {
        this.coordinates = b;
    }
}
