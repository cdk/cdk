#!/usr/bin/groovy

ant = new AntBuilder()

ant.delete(dir:"doc/modules")
ant.mkdir(dir:"doc/modules")

def basedir = new File("src/META-INF")
files = basedir.listFiles().grep(~/.*cdkdepends$/)
files.add(new File(basedir,"annotation.cdkdepends"))
files.add(new File(basedir,"interfaces.cdkdepends"))
files.each {
  file = it
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
    }
  } 
  new File("doc/modules/" + module + ".html").write(writer.toString())
}

