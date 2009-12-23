#!/usr/bin/groovy

def nightly = "http://pele.farmbio.uu.se/nightly-1.2.x/"

ant = new AntBuilder()

ant.delete(dir:"doc/modules")
ant.mkdir(dir:"doc/modules")

def basedir = new File("src/META-INF")
files = basedir.listFiles().grep(~/.*cdkdepends$/)
files.add(new File(basedir,"annotation.cdkdepends"))
files.add(new File(basedir,"interfaces.cdkdepends"))
files.each { file ->
  println "Processing $file";
  m = (file =~ ~/\/([-|\w]*)\.cdkdepends/)
  module = m[0][1]

  def writer = new StringWriter()  
  def builder = new groovy.xml.MarkupBuilder(writer) 
  builder.html(){ 
    head(){ 
      title("CDK Module: " + module){} 
    } 
    body(){
      h1("CDK Module: " + module)
      if (!module.contains("test")) {
        h2("QA Reports")
        p(){
          junitURL = nightly+"test/result-"+module
          stats = "";
          try {
            junitURL.toURL().eachLine {
              if (it =~ ~/Tests\srun/) {
                stats = it
              }
            }
            a(href:junitURL,"JUnit")
            span(": " + stats)
          } catch (FileNotFoundException exc) {
          } catch (SocketException exc) {}
        }
      }
      p(){
        a(href:nightly+"javadoc/$module/", "DocCheck Results")
      }
      p(){
        span("PDM: ")
        a(href:nightly+"pmd-unused/"+module+".html", "unused")
        a(href:nightly+"pmd-migrating/"+module+".html", "migration")
        a(href:nightly+"pmd/"+module+".html", "all")
      }
      h2("Depends")
      p(){
        if (file.exists()) {
          b("CDK")
          file.text.eachLine{
            dependency = (it =~ ~/cdk-([-|\w]*)\.jar/)[0][1]
            a(href:dependency+".html", dependency)
          }
        }
      }
      libdepends = new File(basedir, module + ".libdepends")
      p(){
        if (libdepends.exists()) {
          b("Libraries")
          libdepends.text.eachLine{
            span(it)
          }
        }
      }
      h2("Classes")
      classes = new File("build/" + module + ".javafiles").text
      classes.eachLine {
        classURL = it.replaceAll(/.java/,"")
        clazz = classURL.replaceAll(/\//,".")
        a(href:nightly+"api/"+classURL+".html", clazz)
        br()
      }
    }
  } 
  new File("doc/modules/" + module + ".html").write(writer.toString())
}

