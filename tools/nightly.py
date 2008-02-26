#!/usr/bin/python

#
# Rajarshi Guha <rguha@indiana.edu>
# 04/30/2006
#
# Requires a Unix system, for now
#
# Requires on the path:   java, ant, svn, nice, rm, tar
# Optionally on the path: dot (from graphviz)
#
# Python requirements: Python 2.4, libxml & libxslt bindings
#
# Environment requirements: JAVA_HOME and ANT_HOME should be set
#
# Also you should have Beanshell and JGraphT installed and specified
# in the classpath variable below. However if specified as "" or None
# then no dependency graph is generated
#
# Code coverage is now measured using Emma. The emma.jar should be
# specified in the classpath variable below
#
# Update 05/01/2006 - Added suggestions from Egon: link to sf.net,
#                     added a title to the HTML page, links to
#                     build output, link to a JUnit summary, link
#                     to dependency graphs
# Update 05/05/2006 - Added JAPI comparison. Checked for required
#                     executables and env vars
# Update 05/07/2006 - Added a command line option to prevent mail
#                     from being sent. Also replaced the system() to
#                     tar with calls to the tarfile python module.
#                     Added some more checks
# Update 05/08/2006 - Added checks for JVM segfaults
# Update 05/13/2006 - Added the output of ant info
# Update 05/15/2006 - Added a link to the CDK SVN commits page
# Update 05/16/2006 - Added code to generate and provide the source distribution
# Update 05/17/2006 - Updated the code to use a seperate class for HTML tables
#                     and cells. This allows easier generation of table contents
#                     and not have to worry about <tr> and <td> tags and so on.
#                     Also added a link to the keyword list
# Update 06/04/2006 - Some bugfixes to the output as well as some more checks for
#                     robustness
# Update 06/05/2006 - Fixed the keyword task
# Update 06/07/2006 - Added code to parse the output of pmd-unused reports and add
#                     a summary page to the corresponding section
# Update 06/08/2006 - Reorganized to summarize PMD reports in terms of total number
#                     of violations and linked to actual PMD report pages. Updated
#                     keywords section to point to the local API docs
# Update 06/26/2006 - Added column totals to the JUnit summary page
# Update 07/03/2006 - Added the bug analysis section. Also trapped exceptions from the
#                     bug analysis code
# Update 07/31/2006 - Added success rate to junit summary. Added some code to cleanup
#                     japize files
# Update 08/01/2006 - Made some error cases more consistent
# Update 09/18/2006 - Added current time to the title
# Update 09/20/2006 - Updated to wrap Junit result files in HTML tags
# Update 09/24/2006 - Updated to include revision info in the title
# Update 11/27/2006 - Fixed a bug in parsing the Junit log output. Also provides
#                     visual identification of JUnit crashes
# Update 11/29/2006 - Added code coverage with Emma
# Update 12/10/2006 - Added code to allow for sending emails via a SMTP server
#                     that requires a password/login. Also added a check for
#                     Gmail server, since they require a TLS setup. Also added
#                     code to redact the email password and login's if specified
# Update 12/20/2006 - Fixed the regexes to take into account new modules
# Update 02/10/2007 - Updated to change the value of version in the property file
#                     rather than hard code it as a date stamp in the build script
# Update 02/19/2007 - Updated to process CDKCite errors
# Update 10/26/2007 - Updated for new test.modulesuites package

import string, sys, os, os.path, time, re, glob, shutil, pickle
import tarfile, StringIO
from email.MIMEText import MIMEText
import email.Utils
import smtplib

#################################################################
#
# User definable variables
#
#################################################################

# should point to an SVN repo, within which doing
# ant dist-all should work
nightly_repo = '/home/rguha/src/java/cdk-nightly/cdk/'

# should point to a directory in which this script
# is to be placed and will contain log files
nightly_dir = '/home/rguha/src/java/cdk-nightly/'

# points to a web accessible directory where the
# nightly build site will be generated
nightly_web = '/home/rguha/public_html/code/java/nightly/'
#nightly_web = '/home/rajarshi/public_html/tmp/'


# Jars that are required for Ant to run. In general
# you should only need to point to the relevant JUnit
# jar file, since we don't really use any esoteric Ant tasks
ant_libs = '/home/rguha/src/java/cdk-nightly/cdk/develjar/junit-4.3.1.jar'

# Optional
# required to generate the dependency graph. Should
# contain the path to the BeanShell and JGraphT jar files
# if not required set to "" or None
classpath = '/home/rguha/src/java/beanshell/bsh.jar:/home/rguha/src/java/cdk-nightly/cdk/jar/jgrapht-0.6.0.jar:/home/rguha/src/java/emma/lib/emma.jar'

# Optional
# path to the japitools directory for API comparison
# if not required set to "" or None
japitools_path = '/home/rguha/src/java/japitools'

# Optional
# path to the last stable CDK distribution jar
# if not required set to "" or None
last_stable = '/home/rguha/src/java/cdk-0-99-1.jar'

per_line = 8

# Optional
# variables required for sending mail, if desired.
# should be self explanatory. Set to "" or None if
# you dont want to send mail
smtpServerName = 'smtp.gmail.com'
fromName = 'nightly.py <rguha@indiana.edu>'
toName = 'cdk-devel@lists.sourceforge.net'

# If your server needs authorization, set them here
# if not, just set them to None
smtpLogin = 'rajarshi.guha'
smtpPassword = 'beeb1e'


# A list of links that will be included on the final build
# page. Each item of the list should be a tuple of the link
# text and the actual URL. If empty there will be no links section
links = [ ('1.0.x Nightly Build', 'http://cheminfo.informatics.indiana.edu/~rguha/code/java/nightly-1.0.x/')]

#################################################################
#
# NO NEED TO CHANGE ANYTHING BELOW HERE
#
#################################################################

pickle_file = os.path.join(nightly_dir, 'nightly.pickle')

today = time.localtime()
todayStr = '%04d%02d%02d' % (today[0], today[1], today[2])
todayNice = '%04d-%02d-%02d' % (today[0], today[1], today[2])
dryRun = False
haveXSLT = True
noMail = False

# check to see if we have libxml2 and libxslt
try:
    import libxml2
    import libxslt
except ImportError:
    haveXSLT = False
    print 'Will not tranlate PMD XML output'

class TableCell:

    _valign = 'top'
    _align = 'left'
    _bgcolor = None
    _text = None
    _klass = None

    def __init__(self, text, valign = 'top', align = 'left', klass=None):
        self._text = text
        self._valign = valign
        self._align = align
        self._klass = klass

    def setVAlign(self, pos):
        self._valign = pos

    def setAlign(self, pos):
        self._align = pos

    def appendText(self, text):
        self._text = self._text + text

    def getText(self):
        return self._text

    def __repr__(self):
        if self._klass:
            return """
            <td class=\"%s\" align=\"%s\" valign=\"%s\">
            %s
            </td>
            """ % (self._klass, self._align, self._valign, self._text)
        else: return """
        <td align=\"%s\" valign=\"%s\">
        %s
        </td>
        """ %  (self._align, self._valign, self._text)

class HTMLTable:

    rows = []
    currentRow = None

    headerCells = []

    _border = 0
    _cellspacing = 5
    
    def __init__(self, border = 0, cellspacing = 5):
        self._border = border
        self._cellspacing = cellspacing

    def getCellCount(self):
        return len(rows[curretRow])

    def addRow(self):
        self.rows.append( [] )
        if self.currentRow == None: self.currentRow = 0
        else: self.currentRow = self.currentRow+1

    def addRule(self):
        self.addRow()
        self.addCell("HTMLTABLE_HRULE")

    def addHeaderCell(self, text, valign='top', align='left', klass = None):
        self.headerCells.append(TableCell(text, valign, align, klass))
        
    def addCell(self, text, whichRow = None, valign='top', align='left', klass = None):
        """
        Adds a cell to the current row if whichRow == None
        """
        if not text: return
        if whichRow == None:
            cellList = self.rows[self.currentRow]
            cellList.append(TableCell(text, valign, align, klass))
            self.rows[self.currentRow] = cellList
        elif whichRow > self.currentRow: raise IndexError("Invalid row index was specified")
        else:
            cellList = self.rows[whichRow]
            cellList.append(TableCell(text, valign, align, klass))
            self.rows[whichRow] = cellList            
            

    def  appendToCell(self, text, newline = True):
        """
        Appends text to the current cell in the current row
        """
        if not text: return
        cellList = self.rows[self.currentRow]
        if newline: cellList[ len(cellList)-1 ].appendText("<br>"+text)
        else: cellList[ len(cellList)-1 ].appendText(text)
        self.rows[self.currentRow] = cellList
        

    def __repr__(self):
        # we want to pad the shorter rows with empty cells
        maxRowLen = max(map(len, self.rows))
        
        table = StringIO.StringIO()
        table.write("<table border=%d cellspacing=%d>\n" % (self._border, self._cellspacing))

        # write out the header row
        table.write("<thead>\n<tr>\n")
        for headerCell in self.headerCells: table.write(headerCell)
        table.write("</tr></thead>\n")

        # now  write the rest of the rows
        for row in self.rows:
            table.write("<tr>\n")

            if len(row) == 1 and row[0].getText() == 'HTMLTABLE_HRULE':
                table.write('<td colspan=%d><hr></td>\n</tr>\n' % (maxRowLen))
                continue
            
            for cell in row:
                table.write(cell)
                
            if len(row) != maxRowLen:
                for i in range(maxRowLen-len(row)):
                    table.write(TableCell(""))
                    
            table.write("\n</tr>\n")
        table.write("\n</table>\n")
        return table.getvalue()

def sendMail(message, subject = 'CDK Nightly Trunk Build Failed'):
    if fromName == "" or fromName == None \
       or toName == "" or toName == None \
       or smtpServerName == "" or smtpServerName == None:
        print 'Skipping mail'
        return 
    
    try:        
        msg = MIMEText(message)
        msg['Subject'] = '%s - %s' % (subject,todayNice)
        msg['Message-id'] = email.Utils.make_msgid()
        msg['From'] = fromName
        msg['To'] = toName

        server = smtplib.SMTP(smtpServerName)

        if smtpServerName.find('gmail.com') != -1:
            server.ehlo()
            server.starttls()
            server.ehlo()

        if smtpLogin and smtpPassword:
            server.login(smtpLogin, smtpPassword)
            
        server.sendmail(fromName, toName, msg.as_string())
        server.quit()
        print 'Sent mail to %s' % (toName)
    except Exception, e:
        print e
    
def transformXML2HTML(src, dest, xsltFile, pmd=True):
    if haveXSLT: # if we have the proper libs do the xform
        print '    Transforming %s' % (src)
        styleDoc = libxml2.parseFile(xsltFile)
        style = libxslt.parseStylesheetDoc(styleDoc)
        doc = libxml2.parseFile(src)
        result = style.applyStylesheet(doc, None)
        htmlString = style.saveResultToString(result)        
        style.freeStylesheet()
        doc.freeDoc()
        result.freeDoc()

        # we need to add a bit to the HTML output to indicate what file was processed
        # if we are doing the transform for PMD
        if pmd:
            prefix = os.path.basename(dest).split('.')[0]
            htmlString = re.sub('Report</div>', 'Report [<i>module - %s</i>]</div>' % (prefix), htmlString)
            
        f = open(dest, 'w')
        f.write(htmlString)
        f.close()
        
    else: # cannot xform, so just copy the XML file
        shutil.copyfile(src, dest)
    
def writeJunitSummaryHTML(stats, stable=True):
    pagetype = 'Stable'
    if not stable: pagetype = 'Unstable'
        
    summary = """
    <html>
    <head>
    <title>CDK JUnit Test Summary (%s) [%s]</title>
    <style type="text/css">
    tr.crash { background-color: #F778A1;}
    </style>
    </head>
    <body>
    <center>
    <h2>CDK JUnit Test Summary (%s) [%s]</h2>
    <table border=0 cellspacing=5 cellpadding=3>
    <thead>
    <tr>
    <td valign="top"><b>Module</b></td>
    <td valign="top"><b>Number<br>of Tests</b></td>
    <td valign="top"><b>Failed</b></td>
    <td valign="top"><b>Errors</b></td>
    <td valign="top"><b>Success<br>Rate (%%)</b></td>
    </tr>
    </thead>
    <tr>
    <td colspan=5><hr></td>
    </tr>
    """ % (todayNice, pagetype, todayNice, pagetype)

    totalTest = 0
    totalFail = 0
    totalError = 0
    
    for entry in stats:
        if stable and (entry[0] in ['experimental','builder3d','structgen']): continue
        if not stable and entry[0] not in [ 'experimental', 'builder3d', 'structgen']: continue
        
        if int(entry[1]) != -1:
            totalTest = totalTest + int(entry[1])
            totalFail = totalFail + int(entry[2])
            totalError = totalError + int(entry[3])
        else:
            summary = summary + "<tr class='crash'>"
            summary = summary + "<td align=\"left\"><a href=\"test/result-%s.html\">%s</a> [<b>CRASHED</b>]</td>" % (entry[0], entry[0])
            summary = summary + "<td></td><td></td><td></td><td></td>"
            summary = summary + "</tr>"
            continue
        
        summary = summary + "<tr>"
        summary = summary + "<td align=\"left\"><a href=\"test/result-%s.html\">%s</a></td>" % (entry[0], entry[0])
        for i in entry[1:]:
            summary = summary + "<td align=\"right\">%s</td>" % (i)
        if float(entry[1]) != 0:
            summary = summary + "<td align=\"right\">%.2f</td>" % (100*(float(entry[1])-float(entry[2])-float(entry[3]))/float(entry[1]))
        else:
            summary = summary + "<td align=\"right\">NA</td>" 

        summary = summary + "</tr>"

    summary = summary + """
    <tr>
    <td colspan=5><hr></td>
    </tr>
    <tr>
    <td><b>Totals</b></td>
    <td align=\"right\">%d</td>
    <td align=\"right\">%d</td>
    <td align=\"right\">%d</td>
    <td align=\"right\">%.2f</td>
    </tr>
    <tr>
    <td colspan=5><hr></td>
    </tr>
    </table>
    </center>
    </body>
    </html>""" % (totalTest, totalFail, totalError, (float(totalTest-totalFail-totalError)/float(totalTest))*100)

    summary += """
    </center>
    </body>
    </html>"""
    return summary

def parseJunitOutput(summaryFile, stable=True):
    f = open(os.path.join(nightly_dir,'test.log'), 'r')
    stats = []
    foundModuleEntry = False
    
    while True:
        line = f.readline()
        if not line: break
        if string.find(line, 'test-module') == 0:
            foundModuleEntry = True
        if foundModuleEntry:
            foundModuleEntry = False
            moduleName = f.readline()
            moduleStats = None
            while True:
                moduleStats = f.readline()
                if string.find(moduleStats, '[junit] Tests run:') != -1: break
            if moduleStats.find("[junit] Tests run:") == -1:
                stats.append( (moduleName.split()[5], -1, -1, -1) )
                continue

            # parse the stats and name of the module
            moduleStats = moduleStats.split()
            nTest = moduleStats[3][:-1]
            nFail = moduleStats[5][:-1]
            nError = moduleStats[7][:-1]
            stats.append( (moduleName.split()[5], nTest, nFail, nError) )
    f.close()

    # get an HTML summary
    summary = writeJunitSummaryHTML(stats, stable)
    
    # write out this HTML
    fileName = os.path.join(nightly_web, summaryFile)
    f = open(fileName, 'w')
    f.write(summary)
    f.close()

def parsePMDOutput(pmdReportDir, title=""):

    reportDir = os.path.join(nightly_repo, 'reports', pmdReportDir)
    if not os.path.exists(reportDir):
        print 'Couldnt find %s' % (reportDir)
        return None
    print '    Parsing PMD report files (%s)' % (pmdReportDir)    
    xmlFiles = glob.glob(os.path.join(reportDir, '*.xml'))
    xmlFiles.sort()
    o = StringIO.StringIO()
    o.write("""
    <html>
    <head>
    <title>%s (%s) </title>
    </head>
    <body>
    <center>
    <h2>%s (%s) </h2>
    <table border=0 cellspacing=2>
    <thead>
    <tr>
    <td><b>Module</b></td><td><b>Number of Violations</b></td>
    </tr>
    </thead>
    <tr>
    <td colspan=2><hr></td>
    </tr>
    """ % (title, todayNice, title, todayNice))
    for xmlFile in xmlFiles:
        moduleName = os.path.basename(xmlFile).split('.')[0]
        f = open(xmlFile, 'r')
        vcount = 0
        for line in f:
            if line.find('<violation') != -1: vcount = vcount + 1
        o.write("""
        <tr>
        <td><a href=\"%s/%s.html\">%s</a></td>
        <td align='center'>%d</td>
        <tr>
        """ % (pmdReportDir, moduleName, moduleName, vcount))
    o.write("""
    <tr>
    <td colspan=2><hr></td>
    </tr>   
    </table>
    </center>
    </body>
    </html>""")
    return o.getvalue()

                
def segvOccured(dir):
    """
    Look in dir to see if there are any hs_* files
    which indicate a SEGV in the JVM. If found
    return True else return False
    """
    hsfiles = glob.glob(os.path.join(dir, 'hs_*'))
    if len(hsfiles) == 0: return False
    else: return True
    
def checkIfAntJobFailed(logFileName):
    """
    Returns True if the specified log file does
    not contain the string 'BUILD SUCCESSFUL'
    otherwise returns False
    """
    f = open(logFileName, 'r')
    loglines = f.readlines()
    f.close()
    loglines = string.join(loglines)
    if loglines.find('BUILD SUCCESSFUL') == -1:
        return True
    else: return False

def getLogFilePath(logFileName):
    """
    Creates the full path for a specified log file.

    The log files are always placed in the NIGHTLY_DIR which can
    be changed by the user"""
    
    return os.path.join(nightly_dir, logFileName)

def updateSVN():
    olddir = os.getcwd()
    os.chdir(nightly_repo)
    status = os.system('svn update > %s' % getLogFilePath('svn.log'))
    if status == 0:
        print 'svn ok'
        os.chdir(olddir)        
        return True
    else:
        print 'svn failed'
        os.chdir(olddir)
        return False

def runAntJob(cmdLine, logFileName, jobName, verbose=False):
    if verbose: print cmdLine

    olddir = os.getcwd()
    os.chdir(nightly_repo)

    # clean out any hs_err* files from the repo directory
    hsfiles = glob.glob(os.path.join(nightly_repo, 'hs_*'))
    for hsfile in hsfiles:
        os.unlink(hsfile)
    
    os.system('%s > %s' % (cmdLine, getLogFilePath(logFileName)))

    # if a JVM segfault occured we've failed
    if segvOccured(nightly_repo):
        print '%s failed (JVM segfault)' % (jobName)
        return False
    
    # if for some reason a log file was not written we've failed
    if not os.path.exists(getLogFilePath(logFileName)):
        print '%s failed (no run log)' % (jobName)
        return False
        
    if checkIfAntJobFailed( getLogFilePath(logFileName) ):
        print '%s failed (compile error)' % (jobName)
        os.chdir(olddir)
        return False
    else:
        print '%s ok' % (jobName)
        os.chdir(olddir)
        return True

def generateCDKDepGraph():
    olddir = os.getcwd()
    os.chdir(nightly_repo)

    if classpath == "" or classpath == None:
        print 'classpath not specified. Skipping dependency graph'
        return None
    
    if string.find(classpath, 'bsh.jar') == -1 or \
           string.find(classpath, 'jgrapht') == -1:
        print 'Did not find bsh.jar or the jgrapht jar in \'classpath\''
        return None

    if not executableExists('dot'):
        print 'dot not found. Skipping dependency graph'
        return None
    
    os.system('java -cp %s bsh.Interpreter tools/deptodot.bsh > cdkdep.dot' % (classpath))
    os.system('/usr/bin/dot -Tpng cdkdep.dot -o %s/cdkdep.png' % (nightly_web))
    os.system('/usr/bin/dot -Tps cdkdep.dot -o %s/cdkdep.ps' % (nightly_web))
    os.unlink('cdkdep.dot')

    celltext = []
    celltext.append("Dependency Graph:")
    celltext.append("<a href=\"cdkdep.png\">PNG</a> <a href=\"cdkdep.ps\">PS</a>")

    os.chdir(olddir)
    return celltext


def writeTemporaryPage():
    f = open(os.path.join(nightly_web, 'index.html'), 'w')
    f.write("""
     <html>
    <head>
      <title>
      CDK Nightly Build
      </title>
      <style>
      <!--
        tr:hover { background-color: #efefef; }
      //-->
      </style>
    <head>
    <body>
    <center>
    <h2>CDK Nightly Build</h2>
    <p>
    <br><br>
    Regenerating Build - Please come back in a while
    <center>
    </body>
    </html>""")
    f.close()
    
def copyLogFile(fileName, srcDir, destDir):
    if os.path.exists( os.path.join(srcDir, fileName) ):
        shutil.copyfile(os.path.join(srcDir, fileName),
                        os.path.join(destDir, fileName))            
        return "<a href=\"%s\">%s</a>" % (fileName, fileName)
    else: return None

def executableExists(executable):
    found = False
    paths = os.environ['PATH']
    paths = paths.split(os.pathsep)
    if len(paths) == 0: return False
    for aPath in paths:
        testPath = os.path.join(aPath, executable)
        if os.path.exists(testPath) and os.path.isfile(testPath):
            found = True
            break
    return found

def generateJAPI():

    olddir = os.getcwd()
    os.chdir(nightly_dir)
    
    if japitools_path == "" or japitools_path == None:
        print 'japitools_path not specified. Skipping japi'
        return None

    java_home = None
    try:
        java_home = os.environ['JAVA_HOME']
    except KeyError, ke:
        print 'java_home not specified. Skipping japi'
        return None
    
    if last_stable == "" or last_stable == None:
        print 'last_stable not specified. Skipping japi'
        return None

    if not os.path.isfile(last_stable):
        print 'last_stable specified an invalid file. Skipping japi'
        return None

    # get the paths to the japi binaries
    japize = os.path.join(japitools_path, 'bin', 'japize')
    japicompat = os.path.join(japitools_path, 'bin', 'japicompat')

    # get path to rt.jar
    rtjar = None
    if os.path.exists(os.path.join(java_home, 'jre', 'lib', 'rt.jar')):
        rtjar = os.path.join(java_home, 'jre', 'lib', 'rt.jar')
    elif os.path.exists(os.path.join(java_home, 'lib', 'rt.jar')):
        rtjar = os.path.join(java_home, 'jre', 'lib', 'rt.jar')

    if rtjar == None:
        print 'Cannot find rt.jar. Skipping japi comparison'
        return page

    oldName = os.path.basename(last_stable).split('.')[0]
    oldJapize = os.path.join(nightly_dir, '%s.japi.gz' % (oldName))
    newName = 'cdk-svn-%s' % (todayStr)    
    newJar  = os.path.join(nightly_repo, 'dist', 'jar', 'cdk-svn-%s.jar' % (todayStr))
    newJapize = os.path.join(nightly_dir, '%s.japi.gz' % (newName)) 

    # run japize on the old cdk and the new one
    os.system('%s as %s apis %s %s +org.openscience.cdk 2> japize.log'
              % (japize, oldJapize, last_stable, rtjar))
    os.system('%s as %s apis %s %s +org.openscience.cdk 2>> japize.log'
              % (japize, newJapize, newJar, rtjar))

    # in case there was an error
    if (os.path.getsize(oldJapize) == 0 or os.path.getsize(newJapize) == 0):
        return None
    
    # do the comparison
    os.system('%s -vh -o apicomp.html %s %s 2> japi.log'
              % (japicompat, oldJapize, newJapize))

    # do some regex sub on the apicomp output
    lines = open(os.path.join(nightly_dir, 'apicomp.html') , 'r').readlines()
    lines = string.join(lines)
    lines = re.sub(nightly_dir, '', lines)
    f = open(os.path.join(nightly_dir, 'apicomp.html'), 'w')
    f.write(lines)
    f.close()
    
    # copy output
    srcFile = os.path.join(nightly_dir, 'apicomp.html')
    destFile = os.path.join(nightly_web, 'apicomp.html')
    shutil.copyfile(srcFile, destFile)

    # copy japi css so we get a nice webpage
    srcFile = os.path.join(japitools_path, 'design', 'japi.css')
    destFile = os.path.join(nightly_web, 'japi.css')
    shutil.copyfile(srcFile, destFile)

    # copy the comparison log file
    srcFile = os.path.join(nightly_dir, 'japi.log')
    destFile = os.path.join(nightly_web, 'japi.log')
    shutil.copyfile(srcFile, destFile)

    # make an entry on the page
    celltexts = []
    celltexts.append("<a href=\"http://www.kaffe.org/~stuart/japi/\">JAPI Comparison")
    celltexts.append("<a href=\"apicomp.html\">Summary</a>")
    celltexts.append("<a href=\"japi.log\">japicompat.log</a>")

    # cleanup
    os.unlink(newJapize)
    os.unlink(oldJapize)
    os.unlink('apicomp.html')
    
    os.chdir(olddir)
    
    return celltexts

def transformJunitResultToHTML(filename):
    s = StringIO.StringIO()
    lines = open(filename, 'r').readlines()
    lines = string.join(lines)

    ## make some subs to get valid HTML
    lines = lines.replace("<","&lt;")
    line = lines.replace(">", "&gt;")
    s.write("""
    <html>
    <body>
    <pre>
    %s
    </pre>
    </body>
    </html>
    """  % ( lines ))
    return s.getvalue()

def doCodeCoverage():
    olddir = os.getcwd()
    os.chdir(nightly_repo)

    # check that we have emma.jar
    comps = classpath.split(':')
    foundEmmaJar = False
    for i in comps:
        if i.find('emma.jar') != -1:
            foundEmmaJar = True
    if not foundEmmaJar:
        print '    emma.jar should be in the classpath variable for code coverage'
        os.chdir(olddir)
        return None

    # now lets get a list of the test class holders
    f = glob.glob('src/org/openscience/cdk/test/modulesuites/M*Tests.java')
    f.sort()
    f = [x.split('.')[0] for x in [os.path.basename(x) for x in f]]

    if not f:
        print '    Strange! No test suite classes found.'
        os.chdir(olddir)
        return None

    # lets do all the emma runs
    if not os.path.exists('emma'): os.mkdir('emma')
    for testsuite in f:
        if testsuite.find('smiles') != -1: continue
        print '    Analyzing %s' % (testsuite)

        cmd = """java -cp %s emmarun -cp develjar/junit-4.3.1.jar:dist/jar/cdk-test.jar:dist/jar/cdk-svn-%s.jar:dist/jar/cdk-test-svn-%s.jar -ix +org.openscience.cdk.* -r html -sp src junit.textui.TestRunner org.openscience.cdk.test.%s &> tmp.log && cat tmp.log >> ../emma.log""" % (classpath, todayStr, todayStr, testsuite)
        os.system(cmd)
        if os.path.exists('coverage'):
            shutil.copytree('coverage', (os.path.join('emma', testsuite)))
            os.system('rm -rf coverage')

    os.unlink('tmp.log')
    
    # lets move the whole emma directory to the web dir
    shutil.copytree('emma', os.path.join(nightly_web, 'emma'))                                                 
    os.system('rm -rf emma')
    
    # copy the log file
    srcFile = os.path.join(nightly_dir, 'emma.log')
    destFile = os.path.join(nightly_web, 'emma.log')
    shutil.copyfile(srcFile, destFile)
    os.unlink(os.path.join(nightly_dir, 'emma.log'))
    
    os.chdir(olddir)

    # lets make the links for the web page
    celltext = []
    celltext.append("""Code <a href="http://emma.sourceforge.net/">coverage</a>""")
    f = glob.glob(os.path.join(nightly_web, 'emma', '*'))
    f.sort()

    tnameregex = re.compile('M(?P<tname>[a-zA-Z0-9]*)Tests')
    s = ''
    count = 1
    for covdir in f:
        base = os.path.basename(covdir)
        matches = tnameregex.search(base)
        tname =  matches.group('tname')

        s = s + '<a href="emma/%s">%s</a> ' % (base, tname)
        if count % per_line == 0:
            s = s + "<br>"
        count += 1
    celltext.append(s)
    celltext.append('<a href="emma.log">emma.log</a>')
    return celltext

def updateVersion():
    # update the build.props file
    f = open(os.path.join(nightly_repo, 'build.props'), 'r')
    lines = [x.strip() for x in f.readlines()]
    f.close()
    
    if lines[0].find('version') != 0:
        return False
    
    tmp = lines[0].split('=')
    if tmp[1] == 'svn-%s' % (todayStr):
        return True # it's the same day so nothing to do!
    
    lines[0] = '%s=svn-%s' % (tmp[0], todayStr)
    f = open(os.path.join(nightly_repo, 'build.props'), 'w')
    f.write('%s\n' % (string.join(lines, '\n')))
    f.close()

    # do we need to do a commit?
    
    return True
        
def getSVNRevision():
    tmp = [x.strip() for x in open(os.path.join(nightly_dir,'svn.log'), 'r').readlines()]
    revision = None
    for line in tmp:
        if line.startswith('At revision'):
            revision = line.split()[2][:-1]
            break
        elif line.startswith('Updated to revision'):
            revision = line.split()[3][:-1]
            break
    return revision
                
if __name__ == '__main__':
    if 'help' in sys.argv:
        print """
        Usage: nightly.py [ARGS]

        ARGS can be:

          help   - this message
          dryrun - do a dry run. This does not sync with SVN or run ant tasks. It is expected
                   that you have stuff from a previous run available and is mainly for testing
          nomail - if specified no mail will be sent in response to build errors
        """
        sys.exit(0)

    # check for the presence of required executable
    executableList = ['java', 'ant', 'tar', 'nice', 'svn', 'rm']
    ret = map( executableExists, executableList )
    if False in executableList:
        print 'Could not find one or more required executables: '+executableList
        sys.exit(-1)
    else:
        print """
    Found required executables"""
        

    # check for certain environment variables
    try:
        tmp = os.environ['JAVA_HOME']
        tmp = os.environ['ANT_HOME']
        print """
    Found required environment variables"""
    except KeyError, ke:
        print 'JAVA_HOME & ANT_HOME must be set in the environment'
        sys.exit(-1)
        
    # are we going to do a dry run?
    if 'dryrun' in [x.lower() for x in sys.argv] or 'dry' in [x.lower() for x in sys.argv]:
        dryRun = True

    if 'nomail' in [x.lower() for x in sys.argv]:
        noMail = True


    # print out some status stuff
    print """
    Variable settings
    
      nightly_repo = %s
      nightly_dir  = %s
      nightly_web  = %s
    """ % (nightly_repo, nightly_dir, nightly_web)

    successSrc = True
    successDist = True
    successTestDist = True
    successTest = True
    successJavadoc = True
    successKeyword = True
    successDoccheck = True
    successPMD = True
    successPMDUnused = True
    successPMDMigrate = True
    successSVN = True
    successJCP = True

    currentRevision = None
    oldRevision = None
    
    start_dir = os.getcwd()
    os.chdir(nightly_dir)

    if not dryRun:
        
        # before cleaning anything, lets get some old data
        # try and load the pickle of the previous test summaries
        # else get a summary of the test results from the previous run
        oldReports = None
        if os.path.exists(pickle_file):
            print 'Loading data from persistent storage'
            data = pickle.load(open(pickle_file, 'r'))
            oldReports = data[0]
            oldRevision = data[1]
        else:
            print 'Processing old data'
            reports = glob.glob(os.path.join(nightly_repo, 'reports', '*.txt'))
            reports.sort()
            oldReports = []
            for report in reports:
                for line in open(report, 'r'):
                    if line.startswith('Testcase:'):
                        oldReports.append(''.join(line.split(':')[:2]))
            oldRevision = getSVNRevision()
            if not oldRevision:
                print 'Error getting the SVN revision. Exiting'
                sys.exit(-1)

        # clean out any files from a previous SEGV in the repo dir
        hsfiles = glob.glob(os.path.join(nightly_repo, 'hs_*'))
        for hsfile in hsfiles:
            os.unlink(hsfile)
        
        # clean up log files in the run dir
        logfiles = glob.glob(os.path.join(nightly_dir, '*.log'))
        for logfile in logfiles:
            os.unlink(logfile)

        # clean up source distribution files            
        logfiles = glob.glob(os.path.join(nightly_repo, 'cdk-source*'))
        for logfile in logfiles:
            os.unlink(logfile)

        # clean up japize files
        logfiles = glob.glob(os.path.join(nightly_dir, '*.japi.gz'))
        for logfile in logfiles:
            os.unlink(logfile)

        # go into the repo and sync with SVN
        successSVN = updateSVN()

        # if we failed, report it and use previous build info
        if not successSVN:
            print 'Could not connect to SVN. Skipping nightly build'
            f = open(os.path.join(nightly_web, 'index.html'), 'r')
            lines = string.join(f.readlines())
            f.close()
            newlines = re.sub("<h2>CDK Nightly Build",
                          """<center><b><h3>Could not connect to SVN. Using yesterdays build</h3></b></center>
                          <hr>
                          <p>
                          <h2>CDK Nightly Build""", lines)
            f = open(os.path.join(nightly_web, 'index.html'), 'w')
            f.write(newlines)
            f.close()
            os.chdir(start_dir)
            sys.exit(0)

        # get the latest revision number
        currentRevision = getSVNRevision()
        if not currentRevision:
            print 'Error getting the SVN revision. Exiting'
            sys.exit(-1)

	# if there is no change in rev number since our
	# last run, we can skip the run
        print 'Old revision = %s Current Revision = %s' % (oldRevision, currentRevision)
	if oldRevision == currentRevision:
	    print '  No commits since last run. Exiting'
            sys.exit(0)

        status = updateVersion()
        if not status:
            print "Error parsing build.props. Could not a valid version line. Exiting"
            sys.exit(-1)

        # compile the distro
        successDist = runAntJob('nice -n 19 ant -lib %s clean dist-large' % (ant_libs), 'build.log', 'distro')
        if successDist: # if we compiled, do the rest of the stuff
            successJCP = runAntJob('nice -n 19 ant -lib %s -f build-jchempaint.xml' % (ant_libs), 'jcp.log', 'jcp')
            successTestDist = runAntJob('nice -19 ant -lib %s dist-test-large' % (ant_libs), 'testdist.log', 'testdist')
            successSrc = runAntJob('nice -19 ant -lib %s  sourcedist' % (ant_libs), 'srcdist.log', 'srcdist')
            successTest = runAntJob('nice -n 19 ant -lib %s -DrunSlowTests=false test-all' % (ant_libs), 'test.log', 'test') 
            successJavadoc = runAntJob('export CLASSPATH && nice -n 19  ant -lib %s -f javadoc.xml' % (ant_libs), 'javadoc.log', 'javadoc')
            successKeyword = runAntJob('nice -n 19 ant -lib %s  -f doc/javadoc/build.xml keyword.index' % (ant_libs), 'keyword.log', 'keywords')
            successDoccheck = runAntJob('nice -n 19 ant -lib %s  -f javadoc.xml doccheck' % (ant_libs), 'doccheck.log', 'doccheck')
            successPMD = runAntJob('nice -n 19 ant -lib %s -f pmd.xml pmd' % (ant_libs), 'pmd.log', 'pmd')
            successPMDUnused = runAntJob('nice -n 19 ant -lib %s  -f pmd-unused.xml' % (ant_libs), 'pmdu.log', 'pmdu')            
            successPMDMigrate = runAntJob('nice -n 19 ant -lib %s  -f pmd-migrating.xml' % (ant_libs), 'pmdm.log', 'pmdu')            
        else: # if the distro could not be built, there's not much use doing the other stuff
            print 'Distro compile failed. Generating error page'
            srcFile = os.path.join(nightly_dir, 'build.log')
            destFile = os.path.join(nightly_web, 'build.log.fail')
            shutil.copyfile(srcFile, destFile)
	    lines = None
	    if os.path.exists(os.path.join(nightly_web, 'index.html')):
            	f = open(os.path.join(nightly_web, 'index.html'), 'r')
            	lines = string.join(f.readlines())
            	f.close()
	    else: lines = '<html><body><h2>CDK Nightly Build</h2></body></html>'
            if lines.find("Could not compile the sources") == -1:
                newlines = re.sub("<h2>CDK Nightly Build",
                                  """<center><b><h3>Could not compile the sources -
                                  <a href=\"build.log.fail\">build.log</a>
                                  </h3></b></center>
                                  <hr>
                                  <p>
                                  <h2>CDK Nightly Build""", lines)
                f = open(os.path.join(nightly_web, 'index.html'), 'w')
                f.write(newlines)
                f.close()

            # before finishing send of an email with the last 20 lines of build.log
            f = open(os.path.join(nightly_dir, 'build.log'), 'r')
            lines = f.readlines()
            f.close()
            if not noMail: sendMail(string.join(lines[-20:]))

            # finally done!
            os.chdir(start_dir)
            sys.exit(0)
    else:
        print 'Doing dry run'





    # so we have done a build (hopefully). Get rid of the old stuff
    # and set up a temporary page.    
    os.system('rm -rf %s/*' % (nightly_web))
    writeTemporaryPage()

    currTime = time.localtime()
    currTime = "%02d:%02d" % (currTime[3], currTime[4])	
    page = """
    <html>
    <head>
      <title>
      CDK Nightly Build - %s (%s) [SVN Revision %s]
      </title>
      <style>
      <!--
        tr:hover { background-color: #efefef; }
        .tdfail { background-color: #ea3f3f; }
      //-->
      </style>
    <head>
    <body>
    <center>
    <h2>CDK Nightly Build - %s (%s EST)
    <i>[<a href=\"https://sourceforge.net/svn/?group_id=20024\">SVN</a>
    Revision %s]</i></h2>
    """ % (todayNice, currTime, currentRevision, todayNice, currTime, currentRevision)

    resultTable = HTMLTable()
    resultTable.addHeaderCell("")
    resultTable.addHeaderCell("")
    resultTable.addHeaderCell("<b>Extra Info</b>", align='center')

    # lets now make the web site for nightly builds
    if successDist:
        print '  Generating distro section'
        distSrc = os.path.join(nightly_repo, 'dist', 'jar', 'cdk-svn-%s.jar' % (todayStr))
        distDest = os.path.join(nightly_web, 'cdk-svn-%s.jar' % (todayStr))
        shutil.copyfile(distSrc, distDest)

        resultTable.addRow()
        resultTable.addCell("Combined CDK jar files:")
        resultTable.addCell("<a href=\"cdk-svn-%s.jar\">cdk-svn-%s.jar</a>" % (todayStr, todayStr))

        logEntryText = copyLogFile('build.log', nightly_dir, nightly_web)
        if logEntryText:
            resultTable.addCell(logEntryText)
            resultTable.appendToCell("<a href=\"http://cia.vc/stats/project/cdk/cdk\">SVN commits</a>")
        else:
            resultTable.addCell("<br><a href=\"http://cia.vc/stats/project/cdk/cdk\">SVN commits</a>")

    if successJCP:
        jcpSrc = glob.glob(os.path.join(nightly_repo, 'dist', 'jar', 'jchempaint*.jar'))[0]
        jcpDest = os.path.join(nightly_web, 'jchempaint-svn-%s.jar' % (todayStr))
        shutil.copyfile(jcpSrc, jcpDest)

        resultTable.addRow()
        resultTable.addCell('JChemPaint')
        resultTable.addCell("<a href='jchempaint-svn-%s.jar'>jchempaint-svn-%s.jar</a>" % (todayStr, todayStr))
        logEntryText = copyLogFile('jcp.log', nightly_dir, nightly_web)
        if logEntryText:
            resultTable.addCell(logEntryText)        

    if successSrc:
        print '  Generating source distro section'
        srcSrc = os.path.join(nightly_repo, 'cdk-src+libs-svn-%s.tar.gz' % (todayStr))
        srcDest = os.path.join(nightly_web, 'cdk-src+libs-svn-%s.tar.gz' % (todayStr))
        shutil.copyfile(srcSrc, srcDest)
        srcSrc = os.path.join(nightly_repo, 'cdk-src+libs-svn-%s.zip' % (todayStr))
        srcDest = os.path.join(nightly_web, 'cdk-src+libs-svn-%s.zip' % (todayStr))
        shutil.copyfile(srcSrc, srcDest)

        resultTable.addRow()
        resultTable.addCell("CDK Source files:")
        resultTable.addCell("<a href=\"cdk-src+libs-svn-%s.tar.gz\">Compressed tar file</a>" % (todayStr))
        resultTable.appendToCell("<a href=\"cdk-src+libs-svn-%s.zip\">ZIP file</a>" % (todayStr))

        # check whether we can copy the run output
        resultTable.addCell(copyLogFile('srcdist.log', nightly_dir, nightly_web))
        
    # Lets tar up the java docs and put them away
    resultTable.addRow()
    resultTable.addCell("Javadocs:")
    if successJavadoc:
        print '  Generating javadoc section'
        destFile = os.path.join(nightly_web, 'javadoc-%s.tgz' % (todayStr))

        # tar up the javadocs
        olddir = os.getcwd()
        os.chdir(os.path.join(nightly_repo,'doc'))
        tfile = tarfile.open(destFile, 'w:gz')
        tfile.add('api')
        tfile.close()
        os.chdir(olddir)
        
        shutil.copytree('%s/doc/api' % (nightly_repo),
                        '%s/api' % (nightly_web))


        resultTable.addCell("<a href=\"javadoc-%s.tgz\">Tarball</a>" % (todayStr))
        resultTable.appendToCell("<a href=\"api\">Browse online</a>")

        # check whether we can copy the run output
        resultTable.addCell(copyLogFile('javadoc.log', nightly_dir, nightly_web))

        # process the javadoc log to get CDKCite errors
        f = open(os.path.join(nightly_dir, 'javadoc.log'), 'r')
        o = open(os.path.join(nightly_dir, 'citation.log'), 'w')
        o.write("""
        <html>
        <head><title>CDK Javadoc Citation Errors</title></head>
        <body>
        <h2><center>CDK Javadoc Citation Errors</center></h2>
        """)
        count = 0
        for line in f:
            if line.find('CDKCiteERROR: ') > 0:
                line = re.sub('\\[javadoc\\] ', '', line)
                o.write(line+"<br>\n")
                count += 1
        o.write("</body></html>")
        f.close()
        o.close()
        if count > 0:
            shutil.copyfile(os.path.join(nightly_dir, 'citation.log'),
                            os.path.join(nightly_web, 'citation.log'))
            resultTable.appendToCell('<a href="citation.log">Citations Errors</a>')
        else: os.unlink(os.path.join(nightly_dir, 'citation.log'))
        
        # if the key word run did OK, add the link to the xformed keyword list
        if successKeyword and os.path.exists(os.path.join(nightly_repo, 'keyword.index.xml')):
            srcFile = os.path.join(nightly_repo, 'keyword.index.xml')
            destFile = os.path.join(nightly_web, 'keywords.html')
            xsltFile = os.path.join(nightly_repo, 'doc', 'javadoc', 'keywordIndex2HTML.xsl')
            transformXML2HTML(srcFile, destFile, xsltFile, False)
            resultTable.appendToCell("<a href=\"keywords.html\">Keywords</a>")

            # replace the cdk.sf.net with local reference
            f = open(os.path.join(nightly_web, 'keywords.html'), 'r')
            lines = string.join(f.readlines())
            f.close()
            f = open(os.path.join(nightly_web, 'keywords.html'), 'w')
            f.write(re.sub('http://cdk.sf.net/','', lines))
            f.close()
    else:
        resultTable.addCell("<b>FAILED</b>", klass="tdfail")
        resultTable.addCell(copyLogFile('javadoc.log', nightly_dir, nightly_web))
    resultTable.addRule()
    
    # generate the dependency graph entry
    print '  Generating dependency graph'
    celltexts = generateCDKDepGraph()
    if celltexts:
        resultTable.addRow()
        for celltext in celltexts: resultTable.addCell(celltext)

    # get the JUnit test results
    resultTable.addRow()
    resultTable.addCell("<a href=\"http://www.junit.org/index.htm\">JUnit</a> results:")

    if successTest:
        print '  Generating JUnit section'

        # we'll want to do a diff against the previous runs results,
        # but only if it is not a dry run
        nTestFixed = 'NA'
        nTestFails = 'NA'
        if not dryRun:
            reports = glob.glob(os.path.join(nightly_repo, 'reports', '*.txt'))
            reports.sort()
            newReports = []
            for report in reports:
                for line in open(report, 'r'):
                    if line.startswith('Testcase:'):
                        newReports.append(''.join(line.split(':')[:2]))

            import difflib
            diff = difflib.unified_diff(oldReports, newReports)
            nTestFixed = -1
            nTestFails = -1
            for i in diff:
                if i.startswith('-'): nTestFixed += 1
                if i.startswith('+'): nTestFails += 1
                
            # dump the new report to the pickle file
            # so that it's the old report for the
            # next run. Byt only do so, if the current version
	    # is newer than the older version
	    if currentRevision != oldRevision:		
	        data = [newReports, currentRevision]
        	pickle.dump(data, open(pickle_file, 'w'))                

            # dump out a nice HTML diff page
            oldReports = [x.replace('Testcase', '') for x in oldReports]
            newReports = [x.replace('Testcase', '') for x in newReports]

            oldReports = [x.replace('org.openscience.cdk.test', 'o.o.c.t') for x in oldReports]
            newReports = [x.replace('org.openscience.cdk.test', 'o.o.c.t') for x in newReports]

            htmlDiff = difflib.HtmlDiff()
            difffile = htmlDiff.make_file(oldReports, newReports, numlines=0, context=True)
            difffile = difffile.replace('<title></title>',
                                        """
                                        <title>Fixed and New JUnit Failures</title>""")
            difffile = difffile.replace("<body>",
                                        """
                                        <body>
                                        <center><h2>Fixed and New JUnit Failures
                                        </center></h2>
                                        """)
            difffile= difffile.replace("<tbody>",
                                       """
                                       <tbody>
                                       <tr>
                                       <td></td>
                                       <td></td>                                                                  
                                       <td align='center'><b>Tests fixed since Rev %s</b></td>
                                       <td></td>
                                       <td></td>                                                                                                         
                                       <td align='center'><b>New failures in Rev %s</b></td>
                                       </tr>
                                       """ % (oldRevision, currentRevision), 1)
            f = open(os.path.join(nightly_web,'junitdiff.html'), 'w')
            f.write(difffile)
            f.close()


        
        
        # make the directory for reports
        testDir = os.path.join(nightly_web, 'test')
        os.mkdir(testDir)

        # copy the individual report files
        reportFiles = glob.glob(os.path.join(nightly_repo, 'reports', 'result-*'))
        for report in reportFiles:
            newfile = transformJunitResultToHTML(report)
            newfilename = os.path.basename(report).split('.')[0]+".html"
            dest = os.path.join(testDir, newfilename)
            f = open(dest, 'w')
            f.write(newfile)
            f.close()

        repFiles = glob.glob(os.path.join(testDir,'result-*.html'))
        repFiles.sort()
        count = 1
        s = ""
        for repFile in repFiles:
            title = string.split(os.path.basename(repFile),'.')[0]
            title = string.split(title, '-')[1]
            s = s+"<a href=\"test/%s\">%s</a>\n" % (os.path.basename(repFile), title)
            if count % per_line == 0:
                s += "<br>"
            count += 1    
        resultTable.addCell(s)

        # summarize JUnit test results - it will go into nightly_web
        parseJunitOutput('junitsummary.html')
        parseJunitOutput('junitsummary-unstable.html', stable=False)
        
        # check whether we can copy the run output and link to the summary
        if os.path.exists( os.path.join(nightly_dir, 'test.log') ):
            shutil.copyfile(os.path.join(nightly_dir, 'test.log'),
                            os.path.join(nightly_web, 'test.log'))
            resultTable.addCell("<a href=\"test.log\">test.log</a>")
            resultTable.appendToCell("<a href=\"junitsummary.html\">Stable</a>")
            resultTable.appendToCell("<a href=\"junitsummary-unstable.html\">Unstable</a>")

            if not dryRun:
                resultTable.appendToCell("<br>No. old fails fixed since r%s = %s" % (oldRevision,str(nTestFixed)))
                resultTable.appendToCell("No. new fails since r%s = %s" % (oldRevision,str(nTestFails)))
                resultTable.appendToCell("<a href='%s'>Comparison</a>" % ('junitdiff.html'))
    else:
        resultTable.addCell("<b>FAILED</b>", klass="tdfail")
        if os.path.exists( os.path.join(nightly_dir, 'test.log') ):
            shutil.copyfile(os.path.join(nightly_dir, 'test.log'),
                            os.path.join(nightly_web, 'test.log'))
            resultTable.addCell("<a href=\"test.log\">test.log</a>")
        f = open(os.path.join(nightly_dir, 'test.log'), 'r')
        lines = f.readlines()
        f.close()
        if not noMail: sendMail(string.join(lines[-20:]), 'JUnit test failed')


    # do the code coverage
    if successTestDist:
        print '  Performing code coverage'
        #celltexts = doCodeCoverage()
        #if celltexts:
        #    resultTable.addRow()
        #    for celltext in celltexts: resultTable.addCell(celltext)
    else:
        resultTable.addRow()
        resultTable.addCell("""Code <a href="http://emma.sourceforge.net/">coverage</a>""")
        resultTable.addCell("<b>FAILED</b>", klass="tdfail")
        if os.path.exists( os.path.join(nightly_dir, 'emma.log') ):
            shutil.copyfile(os.path.join(nightly_dir, 'emma.log'),
                            os.path.join(nightly_web, 'emma.log'))
            resultTable.addCell("<a href=\"emma.log\">emma.log</a>")
        
    
    # get the results of doccheck
    resultTable.addRow()
    resultTable.addCell("<a href=\"http://java.sun.com/j2se/javadoc/doccheck/index.html\">DocCheck</a> Results:")
    if successDoccheck:
        print '  Generating DocCheck section'
        shutil.copytree('%s/reports/javadoc' % (nightly_repo),
                        '%s/javadoc' % (nightly_web))
        subdirs = os.listdir('%s/reports/javadoc' % (nightly_repo))
        subdirs.sort()
        count = 1
        s = ''
        for dir in subdirs:
            s = s+"<a href=\"javadoc/%s/\">%s</a>\n" % (dir, dir)
            if count % per_line == 0: s += "<br>"
            count += 1
        resultTable.addCell(s)
    else:
        resultTable.addCell("<b>FAILED</b>", klass="tdfail")
        if os.path.exists( os.path.join(nightly_dir, 'doccheck.log') ):
            shutil.copyfile(os.path.join(nightly_dir, 'doccheck.log'),
                            os.path.join(nightly_web, 'doccheck.log'))
            resultTable.addCell("<a href=\"doccheck.log\">doccheck.log</a>")
        

    # get the results of the PMD analysis
    resultTable.addRow()
    resultTable.addCell("<a href=\"http://pmd.sourceforge.net/\">PMD</a> results:<br><i><b>All</b></i>")
    if successPMD:
        print '  Generating PMD section'
        # make the PMD dir in the web dir
        os.mkdir(os.path.join(nightly_web,'pmd'))

        # transform the PMD XML output to nice HTML
        xmlFiles = glob.glob(os.path.join(nightly_repo,'reports/pmd/*.xml'))
        xmlFiles.sort()
        count = 1
        s = ''
        for xmlFile in xmlFiles:
            prefix = os.path.basename(xmlFile).split('.')[0]
            htmlFile = os.path.join(nightly_web, 'pmd', prefix)+'.html'
            xsltFile = os.path.join(nightly_repo,'pmd','wz-pmd-report.xslt')
            transformXML2HTML(xmlFile, htmlFile, xsltFile)
            s = s+"<a href=\"pmd/%s\">%s</a>\n" % (os.path.basename(htmlFile), prefix)
            if count % per_line == 0: s += "<br>"
            count += 1
        resultTable.addCell(s)
        
        pmdSummary = parsePMDOutput('pmd', 'CDK PMD Summary')
        if not pmdSummary == None:
            o = open(os.path.join(nightly_web, 'pmdsummary.html'), 'w')
            o.write(pmdSummary)
            o.close()
            resultTable.addCell('<a href="pmdsummary.html">Summary</a>')
        
    else: # PMD stage failed for some reason
        resultTable.addCell("<b>FAILED</b>", klass="tdfail")
        if os.path.exists( os.path.join(nightly_dir, 'pmd.log') ):
            shutil.copyfile(os.path.join(nightly_dir, 'pmd.log'),
                            os.path.join(nightly_web, 'pmd.log'))
            resultTable.addCell("<a href=\"pmd.log\">pmd.log</a>")

    resultTable.addRow()
    resultTable.addCell("<ahref=\"http://pmd.sourceforge.net/\">PMD</a> results:<br><i><b>Migration (1.5)</b></i>")
    if successPMDMigrate:
	print '  Generating PMD-Migrating section'
        os.mkdir(os.path.join(nightly_web,'pmd-migrating'))
        # transform the PMD XML output to nice HTML
        xmlFiles = glob.glob(os.path.join(nightly_repo,'reports/pmd-migrating/*.xml'))
        xmlFiles.sort()
        count = 1
        s = ''
        for xmlFile in xmlFiles:
            prefix = os.path.basename(xmlFile).split('.')[0]
            htmlFile = os.path.join(nightly_web, 'pmd-migrating', prefix)+'.html'
            xsltFile = os.path.join(nightly_repo,'pmd','wz-pmd-report.xslt')
            transformXML2HTML(xmlFile, htmlFile, xsltFile)
            s = s+"<a href=\"pmd-migrating/%s\">%s</a>\n" % (os.path.basename(htmlFile), prefix)
            if count % per_line == 0: s += "<br>"
            count += 1
        resultTable.addCell(s)

        pmdSummary = parsePMDOutput('pmd-migrating', 'CDK PMD Migrating Code Summary')
        if not pmdSummary == None:
            o = open(os.path.join(nightly_web, 'pmdmsummary.html'), 'w')
            o.write(pmdSummary)
            o.close()
            resultTable.addCell('<a href="pmdmsummary.html">Summary</a>')

    else: # PMD stage failed for some reason
        resultTable.addCell("<b>FAILED</b>", klass="tdfail")
        if os.path.exists( os.path.join(nightly_dir, 'pmdm.log') ):
            shutil.copyfile(os.path.join(nightly_dir, 'pmdm.log'),
                            os.path.join(nightly_web, 'pmdm.log'))
            resultTable.addCell("<a href=\"pmdm.log\">pmdm.log</a>")


	        
    resultTable.addRow()
    resultTable.addCell("<a href=\"http://pmd.sourceforge.net/\">PMD</a> results:<br><i><b>Unused Code</b></i>")
    if successPMDUnused:
        print '  Generating PMD-Unused section'
        # make the PMD dir in the web dir
        os.mkdir(os.path.join(nightly_web,'pmd-unused'))

        # transform the PMD XML output to nice HTML
        xmlFiles = glob.glob(os.path.join(nightly_repo,'reports/pmd-unused/*.xml'))
        xmlFiles.sort()
        count = 1
        s = ''
        for xmlFile in xmlFiles:
            prefix = os.path.basename(xmlFile).split('.')[0]
            htmlFile = os.path.join(nightly_web, 'pmd-unused', prefix)+'.html'
            xsltFile = os.path.join(nightly_repo,'pmd','wz-pmd-report.xslt')
            transformXML2HTML(xmlFile, htmlFile, xsltFile)
            s = s+"<a href=\"pmd-unused/%s\">%s</a>\n" % (os.path.basename(htmlFile), prefix)
            if count % per_line == 0: s += "<br>"
            count += 1
        resultTable.addCell(s)

        pmdSummary = parsePMDOutput('pmd-unused', 'CDK PMD Unused Code Summary')
        if not pmdSummary == None:
            o = open(os.path.join(nightly_web, 'pmdusummary.html'), 'w')
            o.write(pmdSummary)
            o.close()
            resultTable.addCell('<a href="pmdusummary.html">Summary</a>')
            
    else: # PMD stage failed for some reason
        resultTable.addCell("<b>FAILED</b>", klass="tdfail")
        if os.path.exists( os.path.join(nightly_dir, 'pmdu.log') ):
            shutil.copyfile(os.path.join(nightly_dir, 'pmdu.log'),
                            os.path.join(nightly_web, 'pmdu.log'))
            resultTable.addCell("<a href=\"pmdu.log\">pmdu.log</a>")            

    # try and run japitools
    print '  Generating JAPI comparison'
    celltexts = generateJAPI()
    if celltexts:
        resultTable.addRow()
        for celltext in celltexts: resultTable.addCell(celltext)
        
    # run the bug analysis code
    print '  Performing bug analysis'
    sys.path.append( os.path.join(nightly_repo, 'tools') )
    import analyzeBugs
    try:
        analyzeBugs.analyzeBugs( os.path.join(nightly_web, 'bugs.html'), os.path.join(nightly_repo, 'src') )
        resultTable.addRow()
        resultTable.addCell("Bug Analysis")
        resultTable.addCell("<a href=\"bugs.html\">Results</a>")
    except IOError, ioe:
        print ioe
    except RuntimeError, rte:
        print rte

    ## Add a links section
    if links:
        resultTable.addRow()
        resultTable.addCell("Links");
        s = ''
        for text, link in links:
            s = s + "<a href='%s'>%s<a><br>" % (link, text)
        resultTable.addCell(s)
        
    # copy this script to the nightly we dir. The script should be in nightly_dir
    # we also want to redact the smtp password/login lines
    f = open(os.path.join(nightly_dir,'nightly.py'), 'r')
    script = string.join(f.readlines())
    f.close()
    script = re.sub('smtpLogin =\s?\'.*\'', 'smtpLogin = "XXX"', script)
    script = re.sub('smtpPassword =\s?\'.*\'', 'smtpPassword = "XXX"', script)    
    f = open(os.path.join(nightly_web, 'nightly.py'), 'w')
    f.write(script)
    f.close()

    # close up the HTML and write out the web page
    olddir = os.getcwd()
    os.chdir(nightly_repo)
    os.system('ant info > %s' % (os.path.join(nightly_web, 'antinfo.txt')))

    resultTable.addRule()
    resultTable.addRow()
    resultTable.addCell("<i>Build details</i>")
    resultTable.addCell("<i>Red Hat Enterprise Linux</i>")
    resultTable.appendToCell("<i>Sun JDK 1.5.0</i>")
    resultTable.appendToCell("<i>Ant 1.6.2</i>")
    resultTable.appendToCell("<a href=\"antinfo.txt\">ant info</a>")

    page = page + str(resultTable)
    page = page + """
        <br><br><br>Generated by <a href=\"nightly.py\">nightly.py</a>
        <p>
        <a href=\"http://sourceforge.net/projects/cdk/\"><img alt=\"SourceForge.net Logo\" 
        border=\"0\" height=\"31\" width=\"88\" 
        src=\"http://sourceforge.net/sflogo.php?group_id=20024&type=5&type=1\"></a>
        </center>
        </body>
        </html>"""
    f = open(os.path.join(nightly_web, 'index.html'), 'w')
    f.write(page)
    f.close()
    

    # lets get rid of the build.props file
    # so that the next build will get the latest from svn
    try:
        os.unlink(os.path.join(nightly_repo, 'build.props'))
    except OSError, e:
        pass
    
    # go back to where we started
    os.chdir(start_dir)

    sys.exit(0)
