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
import java.util.List;

import net.sf.cdk.tools.doclets.CDKBugTaglet;

import com.github.ojdcheck.test.IClassDocTester;
import com.github.ojdcheck.test.ITestReport;
import com.github.ojdcheck.test.TestReport;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Tag;

/**
 * OpenJavaDocCheck test that tests if the content of the cdk.bug
 * tag is a valid integer.
 *
 * @see CDKBugTaglet
 */
public class IncorrectBugNumberTagletTest implements IClassDocTester {

    /**
     * {@inheritDoc}
     */
    public String getDescription() {
        return "Tests if the @cdk.bug content is a valid number.";
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "CDK Bug Taglet Number Test";
    }

    /**
     * {@inheritDoc}
     */
    public List<ITestReport> test(ClassDoc classDoc) {
        List<ITestReport> reports = new ArrayList<ITestReport>();
        Tag[] tags = classDoc.tags("cdk.bug");
        for (Tag tag : tags) {
            try {
                Integer.valueOf(tag.text());
            } catch (NumberFormatException exception) {
                reports.add(
                    new TestReport(
                        this, classDoc,
                        "Invalid integer given as bug number in @cdk.bug tag.",
                        tag.position().line(), null
                    )
                );
            }
        }
        MethodDoc[] methodDocs = classDoc.methods();
        for (MethodDoc methodDoc : methodDocs) {
            tags = methodDoc.tags("cdk.bug");
            for (Tag tag : tags) {
                try {
                    Integer.valueOf(tag.text());
                } catch (NumberFormatException exception) {
                    reports.add(
                        new TestReport(
                            this, classDoc,
                            "Invalid integer given as bug number in @cdk.bug tag.",
                            tag.position().line(), null
                        )
                    );
                }
            }
        }
        return reports;
    }

    /**
     * {@inheritDoc}
     */
    public Priority getPriority() {
        return Priority.ERROR;
    }

	@Override
	public String getURL() {
		// There is not web page yet
		return null;
	}

}
