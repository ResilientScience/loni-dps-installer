package pipelineserverinstaller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.jdom.Element;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 *
 * @author Petros Petrosyan
 */
public class UserdataUpdater {

    private Document document;
    private Element root;
    private File userdataFile;

    private String plUser;
    private String plPort;


    public UserdataUpdater() {

       plPort = Configuration.getConfig(Configuration.CONFIG_PIPELINE_PORT);
       plUser = Configuration.getConfig(Configuration.CONFIG_PIPELINE_USER);

       String pipeHome = Constants.pipelineHome(plUser, plPort);

       userdataFile = new File( pipeHome + "userdata.xml");

       if (userdataFile.exists() && userdataFile.isFile()) {
            try {
                SAXBuilder builder = new SAXBuilder();
                document = builder.build(userdataFile);
                root = document.getRootElement();
                XMLOutputter xout = new XMLOutputter();
            } catch (Exception e) {
                System.err.println("Unable to create the Userdata object (by loading)");
                e.printStackTrace();
                root = new Element("userdata");
                document = new Document(root);
            }
        } else {
            // let's create the pipeline home directory
            try {
                Process p = Runtime.getRuntime().exec("sudo -u " + plUser + " mkdir -p " + pipeHome);
                try {
                    p.waitFor();
                } catch (Exception ex) {
                }
                if ( p != null )
                    NativeCalls.releaseProcess(p);


            } catch (IOException e) {
                e.printStackTrace();
            }
            
            try {
                root = new Element("userdata");
                document = new Document(root);
            } catch (Exception e) {
                System.err.println("Unable to create the Userdata object (from scratch)");
                e.printStackTrace();
            }
        }
        
    }
    
    public void setTempDir(String tempdir){
        Element miscElement = root.getChild("misc");

        if (miscElement == null) {
            miscElement = new Element("misc");
            root.addContent(miscElement);
        }
        
        Element tempdirElement = miscElement.getChild("tempdir");

        if (tempdirElement == null) {
            tempdirElement = new Element("tempdir");
            miscElement.addContent(tempdirElement);
        }

        tempdirElement.setText(tempdir);

    }

    @SuppressWarnings("unchecked")
    public void setPackages(List<Package> packages) {
        String serverAddress = "localhost";
        
        Element packagesElement = root.getChild("packages");

        if (packagesElement == null )
        {
            packagesElement = new Element("packages");
            root.addContent(packagesElement);
        }

        List<Element> serverElements = packagesElement.getChildren("server");

        Element serverElement = null;

        for ( Element s : serverElements ) {
            if ( serverAddress.equals(s.getAttribute("address").getValue()) ) {
                serverElement = s;
            }
        }

        if (serverElement != null)
        {
            serverElement.removeChildren("package");
        } else {
            serverElement = new Element("server");
            serverElement.setAttribute("address", serverAddress);
            packagesElement.addContent(serverElement);
        }

        for ( Package p : packages ) {
            serverElement.addContent(p.toXML());
        }

    }

    @SuppressWarnings("unchecked")
    public List<Package> getPackages ( ) {
        String serverAddress = "localhost";
        
        Element packagesElement = root.getChild("packages");
        if (packagesElement != null) {
            List<Element> serverElements = packagesElement.getChildren("server");
            for (Element server:serverElements) {
                if ( server.getAttributeValue("address").equals(serverAddress) ) {
                    List<Element> packageElements = server.getChildren("package");
                    List<Package> packages = new LinkedList<Package>();

                    for ( Element pe : packageElements )
                        packages.add(Package.fromXML(pe));

                    return packages;
                }
            }
        }

        return null;
    }


    public synchronized void flushToDisk() {
        BufferedOutputStream outstream = null;
        try {
            outstream = new BufferedOutputStream(new FileOutputStream(userdataFile));
        } catch (IOException ex) {
            ex.printStackTrace();
            System.err.println("Unable to create an outputstream for userdata.xml");
            return;
        }

        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat().setIndent("     "));
        try {
            outputter.output(document, outstream);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.err.println("Unable to output to userdata.xml");
        }

        try {
            outstream.flush();
            outstream.close();

            // Make sure that userdataFile has the right permissions ( not for Windows )
            Process p = Runtime.getRuntime().exec("chown " + plUser + " " + userdataFile);
            try {
                p.waitFor();
            } catch (Exception ex) {

            }
            if ( p != null )
                NativeCalls.releaseProcess(p);


        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Unable to close userdata.xml after writing");
        }
    }



}
