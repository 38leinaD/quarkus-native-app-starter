package org.acme;

import java.awt.Desktop;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class LsMain implements QuarkusApplication {

    @ConfigProperty(name = "quarkus.http.port")
    Integer assignedPort;
    
    @ConfigProperty(name = "ls.output")
    String outputFormat;
    
    // If set to false, application will not spawn browser and also will not close application when browser closes.
    // Setting this to false is mainly useful during development.
    @ConfigProperty(name = "ls.browser.control")
    boolean browserControl;
    
    // Instead of using Desktop.getDesktop().browse() provide the path to your Chrome executable directly
    @ConfigProperty(name = "ls.chrome.exe")
    String chromeExecutable;
    
    public static void main(String[] args) {
        Quarkus.run(LsMain.class, args);
    }

    @Override
    public int run(String... args) throws Exception {
        if (outputFormat.equals("web")) {
            URI webappUri = new URI("http://localhost:" + assignedPort + "/index.html");
            
            if (browserControl) {
                if (chromeExecutable != null && !chromeExecutable.equals("native")) {
                    String cmd = chromeExecutable + " --incognito --app=" + webappUri.toString();
                    Runtime.getRuntime().exec(cmd).waitFor();
                }
                else {
                    Desktop.getDesktop().browse(webappUri);
                }
            }
            else {
                System.out.println("Please open " + webappUri);
            }
            Quarkus.waitForExit();
        }
        else {
            String out = Files.list(Paths.get(""))
                    .map(Path::toFile)
                    .sorted()
                    .map(f -> String.format("%s%s%s%s %.2f kb %s %s", f.isDirectory() ? "d" : "-",
                            f.canRead() ? "r" : "-",
                            f.canWrite() ? "w" : "-",
                            f.canExecute() ? "x" : "-",
                            f.length() / 1024.0,
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.ofInstant(Instant.ofEpochMilli(f.lastModified()), ZoneId.systemDefault())),
                            f.getName()))
                    .collect(Collectors.joining(" \n"));
    
            System.out.println(out);
        }

        return 0;
    }
}