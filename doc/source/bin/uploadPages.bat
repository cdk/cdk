set JCP_HOME=z:\develop\cdk
move %JCP_HOME%\doc\htdocs\index.html %JCP_HOME%\doc\htdocs\index_template.html
z:\bin\pscp -r %JCP_HOME%\doc\htdocs\* steinbeck@cdk.sourceforge.net:/home/groups/c/cd/cdk/htdocs
