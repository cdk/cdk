import com.sun.tools.doclets.Taglet;
import com.sun.javadoc.*;
import java.util.Map;

public class CDKModuleTaglet implements Taglet {
    
    private static final String NAME = "cdk.module";
    
    public String getName() {
        return NAME;
    }
    
    public boolean inField() {
        return true;
    }

    public boolean inConstructor() {
        return true;
    }
    
    public boolean inMethod() {
        return true;
    }
    
    public boolean inOverview() {
        return true;
    }

    public boolean inPackage() {
        return true;
    }

    public boolean inType() {
        return true;
    }
    
    public boolean isInlineTag() {
        return false;
    }
    
    public static void register(Map tagletMap) {
       CDKModuleTaglet tag = new CDKModuleTaglet();
       Taglet t = (Taglet) tagletMap.get(tag.getName());
       if (t != null) {
           tagletMap.remove(tag.getName());
       }
       tagletMap.put(tag.getName(), tag);
    }

    public String toString(Tag tag) {
        return "<DT><B>Belongs to CDK module: </B><DD>"
               + "<a href=\"http://cdk.sf.net/module-"
               + tag.text() + ".html\">" + tag.text() + "</a></DD>\n";
    }
    
    public String toString(Tag[] tags) {
        if (tags.length == 0) {
            return null;
        } else {
            return toString(tags[0]);
        }
    }

}
