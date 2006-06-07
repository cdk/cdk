package org.openscience.cdk.applications.jchempaint.action;

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JComponent;

import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.applications.jchempaint.JChemPaintPanel;
import org.openscience.cdk.controller.Controller2DModel;

/**
 * Action to change element
 *
 * @cdk.module jchempaint
 * @author     shk3
 */
public class ElementAction extends JCPAction {

	private static final long serialVersionUID = -5121082734491378450L;
	
	private String symbol;
	
	public ElementAction(String symbol, JChemPaintPanel jcpp){
		super(jcpp);
		this.symbol=symbol;
	}
	
	
    public void actionPerformed(ActionEvent e) {
        logger.info("  type  ", type);
        logger.debug("  source ", e.getSource());
        JChemPaintModel jcpModel = jcpPanel.getJChemPaintModel();
        Controller2DModel renderModel = jcpModel.getControllerModel();
        renderModel.setDrawElement(symbol);
        renderModel.setDrawMode(Controller2DModel.ELEMENT);
        ((JButton)jcpPanel.lastAction.get(0)).setBackground(Color.LIGHT_GRAY);
		((JComponent) e.getSource()).setBackground(Color.GRAY);
        jcpPanel.lastAction.set(0,(JComponent) e.getSource());
    }

}
