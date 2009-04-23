#!/usr/bin/groovy

ant = new AntBuilder()

ant.delete(dir:"doc/modules")
ant.mkdir(dir:"doc/modules")

def basedir = new File("src/META-INF")
files = basedir.listFiles().grep(~/.*cdkdepends$/)
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
    } 
  } 
  new File("doc/modules/" + module + ".html").write(writer.toString())
}

