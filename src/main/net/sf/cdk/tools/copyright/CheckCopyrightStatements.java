/* $Revision: 6707 $ $Author: egonw $ $Date: 2006-07-30 16:38:18 -0400 (Sun, 30 Jul 2006) $
 *
 * Copyright (C) 2006-2008  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package net.sf.cdk.tools.copyright;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author egonw
 */
public class CheckCopyrightStatements {

    private enum CopyrightField {
        YEAR_INFO,
        AUTHOR_NAME,
        AUTHOR_EMAIL
    }

    public void processJavaSourceFiles(File path) {
        if (path.isDirectory()) {
            for (File file : path.listFiles()) {
                processJavaSourceFiles(file);
            }
        } else if (path.isFile() && path.getPath().endsWith(".java") &&
                !path.getPath().contains("net" + File.separator + "sf")) {
            Map<String,List<Map<CopyrightField,String>>> copyrights = getCopyrightInfo(path);
            checkCopyrightStatements(copyrights);
        }
    }

    private void checkCopyrightStatements(Map<String,List<Map<CopyrightField, String>>> copyrights ) {
        String fileName = copyrights.keySet().iterator().next();
        List<Map<CopyrightField, String>> copyrightList = copyrights.get(fileName);
        if (copyrightList.size() == 0) {
            System.out.println(fileName + ": missing copyright line.");
        }
        for (Map<CopyrightField,String> holder : copyrightList) {
            // check year
            if (holder.get(CopyrightField.YEAR_INFO) == null) {
                System.out.println(fileName + ": missing copyright year.");
            }
            // check copyright holder
            if (holder.get(CopyrightField.AUTHOR_NAME) == null) {
                System.out.println(fileName + ": missing copyright holder name.");
            } else {
                String holderName = holder.get(CopyrightField.AUTHOR_NAME);
                // check holder != CDK
                if (holderName.contains("Chemistry") &&
                    holderName.contains("Development") &&
                    holderName.contains("Kit")) {
                    System.out.println(fileName + ": copyright is not a legal entity");
                } else if (holder.get(CopyrightField.AUTHOR_EMAIL) == null) {
                    System.out.println(fileName + ": copyright holder is not identified by an email address");
                }
            }
        }
    }

    public Map<String,List<Map<CopyrightField,String>>> getCopyrightInfo(File file) {
        Map<String,List<Map<CopyrightField,String>>> results = new HashMap<String, List<Map<CopyrightField,String>>>();
        List<Map<CopyrightField,String>> copyrights = new ArrayList<Map<CopyrightField,String>>();
        try {
            BufferedReader reader = new BufferedReader(
                new FileReader(file)
            );
            String line = null;
            boolean foundPackageClause = false;
            while ((line = reader.readLine()) != null && !foundPackageClause) {
                foundPackageClause = line.contains("package");
                if (line.contains("Copyright")) {
                    Map<CopyrightField,String> copyrightProperties = new HashMap<CopyrightField, String>();
                    Pattern copyrightPattern = Pattern.compile(".*Copyright\\s*\\(C\\)\\s*(.*)");
                    Matcher generalMatch = copyrightPattern.matcher(line);
                    if (generalMatch.matches()) {
                        String interestingInfo = generalMatch.group(1).trim();
                        String year = null;
                        String author = null;
                        String email = null;
                        Pattern yearsPattern = Pattern.compile("(\\d{4}-\\d{4}).*");
                        Pattern yearPattern = Pattern.compile("(\\d{4}).*");
                        Matcher yearMatch = yearPattern.matcher(interestingInfo);
                        Matcher yearsMatch = yearsPattern.matcher(interestingInfo);
                        if (yearsMatch.matches() || yearMatch.matches()) {
                            year = yearsMatch.matches() ? yearsMatch.group(1) : yearMatch.group(1);
                            copyrightProperties.put(CopyrightField.YEAR_INFO, year);
                            if (interestingInfo.length() > year.length()) {
                                interestingInfo = interestingInfo.substring(year.length()+1).trim();
                            } else {
                                interestingInfo = "";
                            }
                        }
                        if (year != null) {
                            Pattern authorPattern = Pattern.compile("([^<]*).*");
                            Matcher authorMatch = authorPattern.matcher(interestingInfo);
                            if (authorMatch.matches()) {
                                author = authorMatch.group(1).trim();
                                copyrightProperties.put(CopyrightField.AUTHOR_NAME, author);
                                if (interestingInfo.length() > author.length()) {
                                    interestingInfo = interestingInfo.substring(author.length()+1).trim();
                                } else {
                                    interestingInfo = "";
                                }
                            }
                        }
                        if (author != null) {
                            Pattern emailPattern = Pattern.compile("<([^>]*).*");
                            Matcher emailMatch = emailPattern.matcher(interestingInfo);
                            if (emailMatch.matches()) {
                                email = emailMatch.group(1).trim();
                                copyrightProperties.put(CopyrightField.AUTHOR_EMAIL, email);
                            }
                        }
                    }
                    copyrights.add(copyrightProperties);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        results.put(toAPIPath(file.getPath()), copyrights);
        return results;
    }

    public static void main(String[] args) {
        CheckCopyrightStatements processor = new CheckCopyrightStatements();
        processor.processJavaSourceFiles(new File("src/main"));
    }

    private String toAPIPath(String className) {
        className = className.replaceFirst("src" + File.separator + "main" + File.separator, "");
        className = className.replaceFirst(".java", "");
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<className.length(); i++) {
            if (className.charAt(i) == File.separatorChar) {
                sb.append('.');
            } else {
                sb.append(className.charAt(i));
            }
        }
        return sb.toString();
    }

}
