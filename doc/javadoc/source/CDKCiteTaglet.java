import com.sun.tools.doclets.Taglet;
import com.sun.javadoc.*;
import java.util.Map;

public class CDKCiteTaglet implements Taglet {
    
    private static final String NAME = "cdk.cite";
    
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
        return true;
    }
    
    public static void register(Map tagletMap) {
       CDKCiteTaglet tag = new CDKCiteTaglet();
       Taglet t = (Taglet) tagletMap.get(tag.getName());
       if (t != null) {
           tagletMap.remove(tag.getName());
       }
       tagletMap.put(tag.getName(), tag);
    }

    public String toString(Tag tag) {
        return "[<a href=\"http://cdk.sf.net/biblio.html#"
               + tag.text() + "\">" + tag.text() + "</a>]";
    }
    
    public String toString(Tag[] tags) {
        String result = null;
        if (tags.length > 0) {
            result = "[";
            for (int i=0; i<tags.length; i++) {
                result += "<a href=\"http://cdk.sf.net/biblio.html#"
                + tags[i].text() + "\">" + tags[i].text() + "</a>";
                if ((i+1)<tags.length) result += ", ";
            }
            result += "]";
        }
        return result;
    }

}
