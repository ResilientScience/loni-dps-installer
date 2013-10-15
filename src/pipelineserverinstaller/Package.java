package pipelineserverinstaller;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.jdom.Element;


/**
 *
 * @author Petros Petrosyan
 */
public class Package {
    private String name;
    private String version;
    private String location;
    private Map<String,String> variables = new LinkedHashMap<String, String>();
    private List<String> sources = new LinkedList<String>();

    public Package() {
    }

    public Package(String name, String version, String location) {
        if (name==null || version==null || location==null)
            throw new NullPointerException();
        this.name = name;
        this.version = version;
        this.location = location;
    }

    public Package(String name, String version, String location,
            Map<String,String> variables, List<String> sources) {
        if (name==null || version==null || location==null ||
                variables==null || sources==null)
            throw new NullPointerException();
        this.name = name;
        this.version = version;
        this.location = location;

        for(String key : variables.keySet())
            this.variables.put(key, variables.get(key));
        this.sources.addAll(sources);

    }
    public String getLocation() {
        if ( location == null )
            return "";

        return location;
    }

    public String getName() {
        if ( name == null )
            return "";

        return name;
    }

    public String getVersion() {
        if ( version == null )
            return "";

        return version;
    }

    public List<String> getSources() {
        return sources;
    }

    public Map<String, String> getVariables() {
        return variables;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void addVariable(String name, String value) {
        variables.put(name, value);
    }

    public void addVariables(Map<String,String> vars) {
        variables.putAll(vars);
    }

    public void removeVariable(String name) {
        variables.remove(name);
    }

    public String getVariableValue(String name) {
        return variables.get(name);
    }

    public void addSource(String sourceLocation) {
        sources.add(sourceLocation);
    }

    public Element toXML() {
         Element packageElement = new Element("package");

         if ( name != null && name.length() > 0 )
            packageElement.setAttribute("name", name);

         if ( version != null && version.length() > 0 )
            packageElement.setAttribute("version", version);

         if ( location != null && location.length() > 0 ) { 
            if ( location.endsWith(File.separator) )
                packageElement.setAttribute("location", location.substring(0,location.length() -1));
            else
                packageElement.setAttribute("location", location);
         }

         if ( !variables.isEmpty() ) {
             Element variablesElement = new Element("variables");

             for ( String name : variables.keySet() ) {
                 String value = variables.get(name);

                 Element variableElement = new Element("variable");

                 variableElement.setAttribute("name", name);
                 variableElement.setAttribute("value", value);

                 variablesElement.addContent(variableElement);
             }
             packageElement.addContent(variablesElement);
         }

         if ( !sources.isEmpty() ) {
             Element sourcesElement = new Element("sources");

             for ( String sourceLoc : sources ) {
                 Element sourceElement = new Element("source");
                 sourceElement.setAttribute("location", sourceLoc);
                 sourcesElement.addContent(sourceElement);
             }
             packageElement.addContent(sourcesElement);
         }

         return packageElement;
    }

    @SuppressWarnings("unchecked")
    public static Package fromXML(Element e) {
        Package p = new Package();

        p.setName(e.getAttributeValue("name"));
        p.setVersion(e.getAttributeValue("version"));
        p.setLocation(e.getAttributeValue("location"));

        Element varsElement = e.getChild("variables");

        if ( varsElement != null ) {
            List<Element> variableElements = varsElement.getChildren();

            for ( Element varElement : variableElements ) {
                String name = varElement.getAttributeValue("name");
                String value = varElement.getAttributeValue("value");
                p.addVariable(name, value);
            }
        }

        Element sourcesElement = e.getChild("sources");
        if ( sourcesElement != null ) {
            List<Element> sourceElements = sourcesElement.getChildren();

            for ( Element sourceElement : sourceElements ) {
                String location = sourceElement.getAttributeValue("location");
                p.addSource(location);
            }
        }

        return p;
    }
}
