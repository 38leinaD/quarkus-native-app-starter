package org.acme;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.stream.JsonCollectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.runtime.Quarkus;

@ApplicationScoped
@Path("/api")
public class LsResource {

    java.nio.file.Path cwd = Paths.get(System.getProperty("user.dir"));
    
    @ConfigProperty(name = "ls.browser.control")
    boolean browserControl;
    
    @POST
    @Path("exit")
    @Consumes("text/plain")
    public void exit() {
        if (browserControl) Quarkus.asyncExit();
    }
    
    @POST
    @Path("ls")
    @Produces("application/json")
    public JsonArray cd(String dir) throws IOException {
        cwd = cwd.resolve(dir);
        return ls();
    }
    
    private JsonArray ls() throws IOException {
        return currentLs().stream()
                .sorted()
                .map(this::fileToJson)
                .collect(JsonCollectors.toJsonArray());
    }
    
    private List<File> currentLs() throws IOException {
        List<File> listing = Files.list(cwd).map(java.nio.file.Path::toFile).collect(Collectors.toList());
        
        LinkedList<File> completeListing = new LinkedList<File>();
        completeListing.add(new File("."));
        File parent = new File("..");
        if (parent.exists()) completeListing.add(parent);
        completeListing.addAll(listing);
        
        return completeListing;
    }
    
    private JsonObject fileToJson(File f) {
        return Json.createObjectBuilder()
            .add("isDirectory", f.isDirectory())
            .add("canRead", f.canRead())
            .add("canWrite", f.canWrite())
            .add("canExecute", f.canExecute())
            .add("size", f.length())
            .add("lastModified", f.lastModified())
            .add("name", f.getName())
            .build();
    }
}