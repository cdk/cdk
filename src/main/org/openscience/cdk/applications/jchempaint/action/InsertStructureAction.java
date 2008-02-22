package org.openscience.cdk.applications.jchempaint.action;

import org.openscience.cdk.applications.jchempaint.JChemPaintEditorPanel;

import java.awt.event.ActionEvent;

/**
 * Opens a new empty JChemPaintFrame.
 *
 * @author Rajarshi Guha
 * @cdk.module jchempaint
 * @cdk.svnrev  $Revision: 9162 $
 * @cdk.created 2006-09-26
 */
public class InsertStructureAction extends JCPAction {


    private static final long serialVersionUID = -7685519078261241187L;

    /**
     * Hide or show the structure entry text field.
     *
     * @param e Description of the Parameter
     */
    public void actionPerformed(ActionEvent e) {
        if (((JChemPaintEditorPanel) jcpPanel).getShowInsertTextField())
            ((JChemPaintEditorPanel) jcpPanel).setShowInsertTextField(false);
        else ((JChemPaintEditorPanel) jcpPanel).setShowInsertTextField(true);
    }
}
