package org.openscience.cdk.qsar.model;

import org.openscience.cdk.qsar.model.QSARModelException;

/**
 * Classes that implement this interface will build statistical models.
 * 
 * Currently the design of the modeling system is that classes based on
 * a given backend should be based of an abtract class that implements
 * this interface. See <code>RModel</code> as an example.
 *
 * @cdk.module qsar
 */

public interface Model {
   public void build() throws QSARModelException;
   public void predict() throws QSARModelException;
}

