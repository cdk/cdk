package org.openscience.cdk.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * An annotation for source classes to indicate the specific test class and method that tests the source class.
 *
 * @cdk.author Rajarshi Guha
 * @cdk.svnrev $Revision: 9162 $
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface TargetMethod {
    String value();
}
