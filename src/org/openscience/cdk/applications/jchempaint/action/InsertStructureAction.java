package org.openscience.cdk.applications.jchempaint.action;

import org.openscience.cdk.applications.jchempaint.JChemPaintEditorPanel;

import java.awt.event.ActionEvent;

/**
 * Opens a new empty JChemPaintFrame.
 *
 * @cdk.module jchempaint
 * @author     steinbeck
 * @cdk.created    1997
 */
public class InsertStructureAction extends JCPAction {


    private static final long serialVersionUID = -7685519078261241187L;

    /**
     *  Opens an empty JChemPaint frame.
     *
     *@param  e  Description of the Parameter
     */
    public void actionPerformed(ActionEvent e)
    {
        if (((JChemPaintEditorPanel)jcpPanel).getShowInsertTextField()) ((JChemPaintEditorPanel)jcpPanel).setShowInsertTextField(false);
        else ((JChemPaintEditorPanel)jcpPanel).setShowInsertTextField(true);
    }
}
