/*
 * =====================================
 *  Copyright (c) 2022 NextMove Software
 * =====================================
 */
package org.openscience.cdk.test.io;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.io.IChemObjectIO;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.listener.IChemObjectIOListener;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.isomorphism.matchers.IRGroupQuery;

import static org.mockito.Mockito.mock;

/**
 * TestCase for CDK IO classes.
 *
 * @cdk.module test-io
 */
public abstract class ChemObjectIOTest extends CDKTestCase {

    protected static IChemObjectIO chemObjectIO;

    public static void setChemObjectIO(IChemObjectIO aChemObjectIO) {
        chemObjectIO = aChemObjectIO;
    }

    @Test
    public void testChemObjectIOSet() {
        Assertions.assertNotNull(chemObjectIO, "You must use setChemObjectIO() to set the IChemObjectIO object.");
    }

    @Test
    public void testGetFormat() {
        IResourceFormat format = chemObjectIO.getFormat();
        Assertions.assertNotNull(format, "The IChemObjectIO.getFormat method returned null.");
    }

    // FIXME add IRgroupQuery.class
    protected static final Class<?>[] acceptableChemObjectClasses = {
            IChemFile.class,
            IChemModel.class,
            IAtomContainer.class,
            IReaction.class,
            IRGroupQuery.class};

    protected static IChemObject[] acceptableChemObjects() {
        return new IChemObject[]{
                mock(IChemFile.class),
                mock(IChemModel.class),
                mock(IAtomContainer.class),
                mock(IReaction.class),
                mock(IRGroupQuery.class)
        };
    }

    @Test
    public void testAcceptsAtLeastOneChemObject() {
        boolean oneAccepted = false;
        for (IChemObject obj : acceptableChemObjects()) {
            if (chemObjectIO.accepts(obj.getClass())) {
                oneAccepted = true;
            }
        }
        Assertions.assertTrue(oneAccepted, "At least one of the following IChemObect's should be accepted: IChemFile, IChemModel, IAtomContainer, IReaction, IRGroupQuery");
    }

    /** @cdk.bug 3553780 */
    @Test
    @SuppressWarnings("unchecked")
    public void testAcceptsAtLeastOneChemObjectClass() {
        boolean oneAccepted = false;
        for (Class<?> cls : acceptableChemObjectClasses) {
            if (chemObjectIO.accepts((Class<? extends IChemObject>)cls)) {
                oneAccepted = true;
            }
        }
        Assertions.assertTrue(oneAccepted, "At least one of the following IChemObect's should be accepted: IChemFile, IChemModel, IAtomContainer, IReaction, IRGroupQuery");
    }

    @Test
    public void testClose() throws Exception {
        chemObjectIO.close();
    }

    @Test
    public void testGetIOSetting() {
        IOSetting[] settings = chemObjectIO.getIOSettings();
        for (IOSetting setting : settings) {
            Assertions.assertNotNull(setting);
            Assertions.assertNotNull(setting.getDefaultSetting());
            Assertions.assertNotNull(setting.getName());
            Assertions.assertNotNull(setting.getQuestion());
            Assertions.assertNotNull(setting.getLevel());
        }
    }

    @Test
    public void testAddChemObjectIOListener() {
        MyListener listener = new MyListener();
        chemObjectIO.addChemObjectIOListener(listener);
    }

    class MyListener implements IChemObjectIOListener {

        private int timesCalled = 0;

        @Override
        public void processIOSettingQuestion(IOSetting setting) {
            timesCalled++;
        }
    }

    @Test
    public void testRemoveChemObjectIOListener() {
        MyListener listener = new MyListener();
        chemObjectIO.addChemObjectIOListener(listener);
        chemObjectIO.removeChemObjectIOListener(listener);
    }

}
