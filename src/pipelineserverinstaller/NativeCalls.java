package pipelineserverinstaller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;

/**
 *
 * @author Petros Petrosyan
 */
public class NativeCalls {

    /**
     * Loads the specified URL into the user's default web browser
     * @param url The URL to load. Make sure to specify a protocol.
     */
    public static void openBrowser(final String url) {
        if (Constants.isOSX)
        {
            try {
                Class<?> fileManagerClass = Class.forName("com.apple.eio.FileManager");
                Method openURLMethod = fileManagerClass.getMethod("openURL", String.class);
                openURLMethod.invoke(null, url);
                return;
            } catch (Exception ex) { ex.printStackTrace(); }
        }

        Runtime runtime = Runtime.getRuntime();
        String command = null;

        if (Constants.isWindows)
        {
            command = "cmd.exe /C start " + url;
            try { runtime.exec(command); }
            catch (Exception e) { e.printStackTrace(); }
            return;
        }

        // we must be dealing with Linux or Solaris

        Thread browserThread = new Thread(new Runnable() {
            public void run() {
                Runtime runtime = Runtime.getRuntime();
                // first try using the portland freedesktop utils (http://portland.freedesktop.org)
                String command = "xdg-open '" +url+"'";
                try {
                    final Process process = runtime.exec(command);
                    while (true)
                    {
                        try {
                            int exitValue = process.exitValue();
                            if (exitValue == 0) { 
                                // sometimes even when there are errors, exitValue is not 0, check if there are bytes in error stream
                                // if so, then call failed.
                                int avail = process.getErrorStream().available();
                                
                                if ( avail == 0 ) { 
                                    return;
                                }
                            }

                            break;
                        }
                        catch (Exception e) {}
                    }
                }
                catch (Exception e) { }

                // if we make it here, it means that xdg-open didn't work
                // now let's try gnome-open (lots of gnome users out there)
                command = "gnome-open " + url;
                try {
                    final Process process = runtime.exec(command);
                    while (true)
                    {
                        try {
                            int exitValue = process.exitValue();
                            if (exitValue == 0) { 
                                // sometimes even when there are errors, exitValue is not 0, check if there are bytes in error stream
                                // if so, then call failed.
                                int avail = process.getErrorStream().available();
                                
                                if ( avail == 0 ) { 
                                    return;
                                }
                            }

                            break;
                        }
                        catch (Exception e) {}
                    }
                }
                catch (Exception e) {}

                // probably don't have gnome. try kde
                command = "kfmclient " + url;
                try {
                    final Process process = runtime.exec(command);
                    while (true)
                    {
                        try {
                            int exitValue = process.exitValue();
                            if (exitValue == 0) { 
                                // sometimes even when there are errors, exitValue is not 0, check if there are bytes in error stream
                                // if so, then call failed.
                                int avail = process.getErrorStream().available();
                                
                                if ( avail == 0 ) { 
                                    return;
                                }
                            }
                            break;
                        }
                        catch (Exception e) {}
                    }
                }
                catch (Exception e) {}

                // not even kde, then we're just gonna use firefox
                command = "firefox " + url;
                try {
                    final Process process = runtime.exec(command);
                    while (true)
                    {
                        try {
                            int exitValue = process.exitValue();
                            if (exitValue == 0) { 
                                // sometimes even when there are errors, exitValue is not 0, check if there are bytes in error stream
                                // if so, then call failed.
                                int avail = process.getErrorStream().available();
                                
                                if ( avail == 0 ) { 
                                    return;
                                }
                            }
                            
                            break;
                        }
                        catch (Exception e) {}
                    }
                }
                catch (Exception e) {}

                // still nothing? you're on your own
            }
        });

        browserThread.start();
    }

    public static void releaseProcess(Process p) {
        Exception ex = null;

        if ( p != null) {
            try { p.getInputStream().close(); } catch ( Exception iex) { iex.printStackTrace(); }
            try { p.getOutputStream().close(); } catch ( Exception oex) {oex.printStackTrace(); }
            try { p.getErrorStream().close(); } catch ( Exception eex) { eex.printStackTrace(); }
            try { p.destroy(); } catch ( Exception dex) { dex.printStackTrace(); }
        }

        if  ( ex != null)
            ex.printStackTrace();
    }

    public static void copyFile(File sourceFile, File destFile) {
        try { 
            if (!sourceFile.exists()) {
                    return;
            }
            if (!destFile.exists()) {
                    destFile.createNewFile();
            }
            FileChannel source = null;
            FileChannel destination = null;
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            if (destination != null && source != null) {
                    destination.transferFrom(source, 0, source.size());
            }
            if (source != null) {
                    source.close();
            }
            if (destination != null) {
                    destination.close();
            }
        } catch ( IOException ex) {
            ex.printStackTrace();
        }

}


    public static String checksum( String fileLocation , int algorithm )
    {

        String alg = null;

        switch ( algorithm ) {
            case Constants.CHECKSUM_ALG_MD_2: alg = "MD-2"; break;
            case Constants.CHECKSUM_ALG_MD_5: alg = "MD-5";  break;
            case Constants.CHECKSUM_ALG_SHA_1: alg = "SHA-1"; break;
            case Constants.CHECKSUM_ALG_SHA_256: alg = "SHA-256"; break;
            case Constants.CHECKSUM_ALG_SHA_384: alg = "SHA-384"; break;
            case Constants.CHECKSUM_ALG_SHA_512: alg = "SHA-512"; break;

            default: throw new IllegalArgumentException("Invalid checksum algorithm specified ( " + algorithm + " )");
        }

        try {
            MessageDigest md = MessageDigest.getInstance(alg);

            FileInputStream fis = new FileInputStream(fileLocation);

            byte[] dataBytes = new byte[16384];

            int nread = 0;
            while ((nread = fis.read(dataBytes)) != -1) {
              md.update(dataBytes, 0, nread);
            }

            byte[] mdbytes = md.digest();

            StringBuilder hexString = new StringBuilder();
            for (int i=0;i<mdbytes.length;i++) {
              hexString.append(Integer.toHexString(0xFF & mdbytes[i]));
            }

            return hexString.toString();

        } catch ( Exception ex ) {
            ex.printStackTrace();
        }

        return null;
    }
}
