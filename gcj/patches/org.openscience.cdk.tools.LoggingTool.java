41,54c41
<         try {
<             logger = org.apache.log4j.Category.getInstance( classname );
< 
<             // configure Log4J
<             URL url = getClass().getClassLoader().getResource("org/openscience/cdk/config/log4j.properties");
<             org.apache.log4j.PropertyConfigurator.configure(url);
<         } catch (NoClassDefFoundError e) {
<             tostdout = true;
<         } catch (NullPointerException e) {
<             tostdout = true;
<             debug("Properties file not found!");
<         } catch (Exception e) {
<             tostdout = true;
<         }
---
>         tostdout = true;
87d73
<                 ((org.apache.log4j.Category)logger).debug(s);
98d83
<                 ((org.apache.log4j.Category)logger).error(s);
109d93
<                 ((org.apache.log4j.Category)logger).fatal(s);
120d103
<                 ((org.apache.log4j.Category)logger).info(s);
131d113
<                 ((org.apache.log4j.Category)logger).warn(s);
