/*
 * =====================================
 *  Copyright (c) 2022 NextMove Software
 * =====================================
 */

package org.openscience.cdk.structgen.maygen;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smiles.SmiFlavor;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

final class SmiOutputConsumer implements Maygen.Consumer {

    private final ILoggingTool logger = LoggingToolFactory.createLoggingTool(SmiOutputConsumer.class);

    private final SmilesGenerator smigen = new SmilesGenerator(SmiFlavor.Default);
    private final File dir;
    private BufferedWriter wtr;

    public SmiOutputConsumer(File dir) {
        this.dir = dir;
        if (dir == null)
            wtr = new BufferedWriter(new OutputStreamWriter(System.out,
                    StandardCharsets.UTF_8));
        else
            dir.mkdirs();
    }

    public SmiOutputConsumer(Writer writer) {
        this.dir = null;
        wtr = new BufferedWriter(writer);
    }

    @Override
    public void configure(String name) {
        if (dir != null) {
            if (wtr != null) {
                try {
                    wtr.close();
                } catch (IOException e) {
                    logger.error(e);
                }
            }
            try {
                wtr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(dir, name + ".smi")),
                        StandardCharsets.UTF_8));
            } catch (FileNotFoundException e) {
                logger.error(e);
            }
        }
    }

    @Override
    public void consume(IAtomContainer mol) {
        try {
            String smi = smigen.create(mol);
            wtr.write(smi + "\n");
        } catch (CDKException | IOException e) {
            logger.error(e);
        }
    }

    @Override
    public void close() throws IOException {
        wtr.close();
    }
}
