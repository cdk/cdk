/* Copyright (C) 2009  Egon Willighagen <egonw@users.sf.net>
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */
package net.sf.cdk.tools.checkdoctest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sf.cdk.tools.doclets.CDKModuleTaglet;

import com.github.ojdcheck.test.IClassDocTester;
import com.github.ojdcheck.test.ITestReport;
import com.github.ojdcheck.test.TestReport;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Tag;

/**
 * OpenJavaDocCheck test that warns of a class JavaDoc does not contain
 * the cdk.module tag needed by the build system.
 *
 * @see CDKModuleTaglet
 */
public class MissingModuleTagletTest implements IClassDocTester {

    /**
     * {@inheritDoc}
     */
    public String getDescription() {
        return "Tests the content of the module taglet content.";
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "CDK Module Taglet Test";
    }

    /**
     * {@inheritDoc}
     */
    public List<ITestReport> test(ClassDoc classDoc) {
        if (classDoc.containingClass() != null) {
            return Collections.emptyList();
        }

        List<ITestReport> reports = new ArrayList<ITestReport>();
        Tag[] tags = classDoc.tags("cdk.module");
        if (tags.length == 0) {
            reports.add(
                new TestReport(
                    this, classDoc,
                    "Missing @cdk.module tag.",
                    classDoc.position().line(), null
                )
            );
        }
        return reports;
    }

    /**
     * {@inheritDoc}
     */
    public Priority getPriority() {
        return Priority.WARNING;
    }

	@Override
	public String getURL() {
		// There is not web page yet
		return null;
	}

}
