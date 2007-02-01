#!/usr/bin/python

# Rajarshi Guha <rajarshi.guha@gmail.com>
# 07/02/2006

import string, re, urllib, os, sys
       
def getBugPage(pageNum, which = 'sf'):
    filename = 'page%d.html' % (pageNum)
    offset = (pageNum - 1) * 50
    if which == 'sf':
        url = 'http://sourceforge.net/tracker/index.php?func=browse&group_id=20024&atid=120024&set=custom&_assigned_to=0&_status=1&_category=100&_group=100&order=artifact_id&sort=DESC&offset=%d' % (offset)
        msg = 'Getting SF bugs %d to %d ...\r' % (offset, offset+49)
    else:
        url = 'http://sourceforge.net/tracker/index.php?func=browse&group_id=20024&atid=120024&set=custom&_assigned_to=0&_status=100&_category=100&_group=100&order=artifact_id&sort=DESC&offset=%d' % (offset)
        msg = 'Getting all bugs %d to %d ...\r' % (offset, offset+49)
        
    sys.stdout.write(msg)
    sys.stdout.flush() 

    data = urllib.urlopen(url)
    return data.read()

def sortAsNumber(alist):
    alist = [int(x) for x in alist]
    alist.sort()
    return [str(x) for x in alist]

def analyzeBugs(outputFile, cdkSrcDir):

    openBugs = {}
    allBugs = {}

    pageCount = 0
    while True:
        pageCount += 1
        data = getBugPage(pageCount, 'sf')
        if not data:
            raise IOError, "No data was received from SF"
        
        bugre = re.compile('func=detail&amp;aid=(?P<aid>[0-9]*)&amp;group_id=(?P<gid>[0-9]*)&amp;atid=(?P<tid>[0-9]*)')
        bugs = re.findall(bugre, data)

        for bug in bugs:
            openBugs[bug[0]] = 'open'
        if data.find('Next 50') == -1: break

    # now do for all the bugs
    pageCount = 0
    while True:
        pageCount += 1
        data = getBugPage(pageCount, 'all')
        if not data:
            raise IOError, "No data was received from SF"
        
        bugre = re.compile('func=detail&amp;aid=(?P<aid>[0-9]*)&amp;group_id=(?P<gid>[0-9]*)&amp;atid=(?P<tid>[0-9]*)')        
        bugs = re.findall(bugre, data)

        for bug in bugs:
            allBugs[bug[0]] = 'open'
        if data.find('Next 50') == -1: break    


    # now do the analysis

    testedBugs = {}
    markedBugs = {}

    # find all Java files
    javaFiles = []
    dirname = os.path.join(cdkSrcDir, 'org')
    
    for root, dirs, files in os.walk(dirname):
        for filename in files:
            if filename.endswith('.java'):
                javaFiles.append( os.path.join(root, filename) )


    if len(javaFiles) == 0:
        raise RuntimeError, "No Java source files found under %s" % (dirname)
    
    for filename in javaFiles:
        data = open(filename, 'r').readlines()
        data = ''.join(data)
        bugre = re.compile('cdk\.bug\s+(?P<id>\d+)')
        results = re.findall(bugre, data)

        if filename.find( os.path.join('cdk', 'test') ) != -1:
            for result in results:
                testedBugs[result] = 'tested'
        else:
            for result in results:
                markedBugs[result] = filename

    print '                                                          \r'

    print 'Found bugs on SF = %d' % (len(allBugs.keys()))
    print ' of which %d are open\n' % (len(openBugs.keys()))
    print 'Found %d marked bugs' % (len(markedBugs.keys()))
    print 'Found %d tested bugs' % (len(testedBugs.keys()))
    print '\n'

    # generate page
    page = """
    <html>
    <head>
    <title>
    CDK Bug Analysis
    </title>
    </head>
    <body>
    <center><h2>Mark up of Open Bugs in JavaDoc</h2></center>
    Every open bug is supposed to be marked with @cdk.bug in the JavaDoc off the buggy class and/or method.
    <h3>Open bugs not marked in source</h3>
    <ul>
    """

    keys = sortAsNumber(openBugs.keys())
    count  = 0
    for key in keys:
        if key in markedBugs.keys():
            pass # it was marked, so OK
        else:
            page = page + """
            <li>Open bug <a href=\"http://sourceforge.net/tracker/index.php?func=detail&aid=%s&group_id=20024&atid=120024\">#%s</a> is <b><i>not</i></b> marked in the source code with @cdk.bug!</li>\n
            """ % (key, key)
            count += 1
    page += "</ul>\n"

    print 'Open bugs that are not marked with @cdk.bug =  %d' % (count)

    page += "<h3>Marked bugs that are now fixed</h3>\n<ul>\n"
    keys = sortAsNumber(markedBugs.keys())
    count  = 0
    for key in keys:
        if key in openBugs.keys():
            pass # all is fine
        elif key in allBugs.keys():
            page += """
            <li>Marked bug <a href=\"http://sourceforge.net/tracker/index.php?func=detail&aid=%s&group_id=20024&atid=120024\">#%s</a> in %s is fixed!</li>\n
            """ % (key, key, markedBugs[key])
            count += 1
        else:
            page += """
            <li>Marked bug #%s is not reported within the CDK project.</li>
            """ % (key)
    page += "</ul>\n"

    print "Marked bugs that are now fixed = %d" % (count)

    page += "<center><h2>JUnit testing of Reported Bugs</h2></center>\n";

    page += """
    Every reported bug should be tested by a JUnit test.
    """

    page += "<h3>Open bugs without JUnit tests</h3>\n<ul>\n"
    keys = sortAsNumber(allBugs.keys())
    count = 0
    for key in keys:
        if key in testedBugs.keys():
            pass
        elif key in openBugs.keys():
            page += """
            <li>Bug <a href=\"http://sourceforge.net/tracker/index.php?func=detail&aid=%s&group_id=20024&atid=120024\">#%s</a> is not JUnit tested!</li>
            """ % (key,key)
            count += 1
    page += """
    </ul>
    """

    print "Open bugs that are not JUnit tested = %d\n" % (count)
    
    page += "<h3>Closed bugs without JUnit tests</h3>\n<ul>\n"
    keys = sortAsNumber(allBugs.keys())
    count = 0
    for key in keys:
        if key in testedBugs.keys():
            pass
        elif key in openBugs.keys():
            pass
        else:
            page += """
            <li>Bug <a href=\"http://sourceforge.net/tracker/index.php?func=detail&aid=%s&group_id=20024&atid=120024\">#%s</a> is not JUnit tested!</li>
            """ % (key,key)
            count += 1
    page += """
    </ul>
    """

    print "Closed bugs that are not JUnit tested = %d\n" % (count)
            
    

    page = page + """
    </body>
    </html>
    """
    f = open(outputFile,'w')
    f.write(page)
    f.close()

if __name__ == '__main__':

    outputFile = 'bugAnalysis.html'
    srcdir = 'cdk/src/'

    analyzeBugs(outputFile, srcdir)

            


                                  
    
    
