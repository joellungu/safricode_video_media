package org.safricodemedia.controllers;
//package org.acme;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.*;

import java.io.*;
import java.nio.file.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jboss.resteasy.reactive.MultipartForm;
import org.jboss.resteasy.reactive.PartType;
import org.safricodemedia.WebSocketService;
import org.safricodemedia.models.Appareil;
import org.safricodemedia.models.VideoInfo;
import org.safricodemedia.models.VideoMedia;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/videos")
@ApplicationScoped
public class VideoResource {

    @Inject
    VideoService videoService;

    private static final String VIDEO_FOLDER = "videos";
    private static final java.nio.file.Path VIDEO_DIR = Paths.get("videos");
    private static final ObjectMapper mapper = new ObjectMapper();
    

    @Inject
    WebSocketService webSocketService;

    @POST
    @Path("/upload")
    @Transactional
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadVideo(@MultipartForm MultipartBody body) {
        try{
            //String[] nom = body.filename.split("\\");
            System.out.println("Filename: "+body.filename);
            Files.createDirectories(java.nio.file.Path.of(VIDEO_FOLDER));
            java.nio.file.Path target = java.nio.file.Path.of(VIDEO_FOLDER, body.filename);
            Files.write(target, body.fileData);
            String fullUrl = "/videos/" + body.filename;
        } catch(Exception ex){
            System.out.println("Erreur: "+ex);
        }
        //
        // VideoMedia videoMedia = new VideoMedia();
        // videoMedia.titre = body.filename;
        // videoMedia.video = body.fileData;
        // videoMedia.persist();
        // Notifie tous les clients WebSocket
        //webSocketService.broadcast(new VideoInfo(fullUrl));

        return Response.ok().build();
    }

    @GET
    @Path("/lecture")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response lecture(@QueryParam("titre") String titre) throws IOException {

        //
        try (Stream<java.nio.file.Path> files = Files.list(java.nio.file.Path.of(VIDEO_FOLDER))) {
            Optional<java.nio.file.Path> resultat = files
                .filter(Files::isRegularFile)
                .filter(path -> path.getFileName().toString().equals(titre))
                .findFirst();

            if (resultat.isPresent()) {
                java.nio.file.Path fichier = resultat.get();
                System.out.println("Fichier trouvé : " + fichier.toAbsolutePath());
                // Tu peux maintenant lire le fichier ou l'utiliser
                return Response.ok(fichier.toFile())
                // .header("Content-Disposition", "attachment; filename=\"" + titre + "\"")
                // .header("Content-Length", Files.size(fichier.getFileName()))
                // .header("Accept-Ranges", "bytes") // Permet la reprise des téléchargements
                .build();
            } else {
                System.out.println("Fichier non trouvé.");
                return Response.status(404).build();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Response.status(404).build();
        }
        // Notifie tous les clients WebSocket
        //webSocketService.broadcast(video);
        //
        //VideoMedia videoMedia = VideoMedia.find("titre", titre).firstResult();
        //
        
    }
     

    @GET
    @Path("/list")
    public Response listVideos() {
        try {
            if (!Files.exists(VIDEO_DIR)) {
                return Response.ok(Collections.emptyList()).build();
            }

            List<String> videos = new LinkedList<>();

            
            // Crée le répertoire s'il n'existe pas
            Files.createDirectories(java.nio.file.Path.of(VIDEO_FOLDER));

            // Lister les fichiers dans le dossier
                        
            List<java.nio.file.Path> files = Files.list(java.nio.file.Path.of(VIDEO_FOLDER)).toList();
            
                for(java.nio.file.Path path : files){
                    
                    videos.add(path.getFileName().toString());
                    System.out.println(path.getFileName());
                
                }
                
            // .filter(Files::isRegularFile)
            // .map(path -> path.getFileName().toString())
            // .collect(Collectors.toList());
            //
            String message = mapper.writeValueAsString(videos);

            return Response.ok(message).build();
        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity(Map.of("error", "Impossible de lire les vidéos"))
                           .build();
        }
    }

    @GET
    @Path("/appareils")
    public Response listAppareils() {
        //appareils
        try {
            String message = mapper.writeValueAsString(Notification.appareils);
            return Response.ok(message).build();
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //
        return Response.ok().build();
        
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response listAppareils(@QueryParam("id") String id,
    @QueryParam("titre") String titre,
    @QueryParam("duree") String duree) {
        //
        Appareil appareil = new Appareil();
        appareil.id = id;
        appareil.titre = titre;
        appareil.duree = duree;
        
        //List<HashMap> apps = new LinkedList<>();
        addOrUpdate(Notification.appareils, appareil);
        //
        return Response.ok().build();
        
    }

    @GET
    @Path("/{filename}")
    public Response stream(@PathParam("filename") String filename,
                           @HeaderParam("Range") String rangeHeader) throws IOException {

        File file = java.nio.file.Path.of(VIDEO_FOLDER, filename).toFile();
        //
        System.out.println("File: "+VIDEO_FOLDER+"/"+filename);
        //
        System.out.println("File: "+VIDEO_FOLDER+"/"+filename);
        //
        if (!file.exists()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return buildPartialContentResponse(file, rangeHeader);
    }

    @DELETE
    @Path("/{titre}")
    @Transactional
    public Response supprimerr(@PathParam("titre") String titre) throws IOException {

        //VideoMedia.delete("titre", titre);
        //Path filePath = Paths.get("chemin/vers/fichier.txt");

        try {
            java.nio.file.Path target = java.nio.file.Path.of(VIDEO_FOLDER, titre);
            Files.delete(target);
            //Files.delete(filePath);
            System.out.println("Fichier supprimé avec succès.");
        } catch (IOException e) {
            System.err.println("Erreur de suppression du fichier : " + e.getMessage());
        }

        return Response.ok().build();
    }

    public void addOrUpdate(LinkedList<Appareil> list, Appareil newItem) {
        String newId = newItem.id;
        boolean found = false;

        for (Appareil item : list) {
            if (Objects.equals(item.id, newId)) {
                // Mise à jour des propriétés spécifiques
                item.titre = newItem.titre;
                item.duree = newItem.duree;
                found = true;
                break;
            }
        }
        //id,titre,duree,

        if (!found) {
            list.add(newItem);
        }
    }

    private Response buildPartialContentResponse(File file, String rangeHeader) throws IOException {
        long fileLength = file.length();
        long start = 0;
        long end = fileLength - 1;

        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            String[] parts = rangeHeader.substring("bytes=".length()).split("-");
            start = Long.parseLong(parts[0]);
            if (parts.length > 1 && !parts[1].isEmpty()) {
                end = Long.parseLong(parts[1]);
            }
        }

        if (start > end) {
            return Response.status(Response.Status.REQUESTED_RANGE_NOT_SATISFIABLE)
                    .header("Content-Range", "bytes */" + fileLength)
                    .build();
        }

        long contentLength = end - start + 1;
        InputStream in = new FileInputStream(file);
        in.skip(start);

        return Response.status(rangeHeader != null ? 206 : 200)
                .header("Accept-Ranges", "bytes")
                .header("Content-Range", "bytes " + start + "-" + end + "/" + fileLength)
                .header("Content-Length", contentLength)
                .header("Content-Type", Files.probeContentType(file.toPath()))
                .entity(new LimitedInputStream(in, contentLength))
                .build();
    }

    private static class LimitedInputStream extends InputStream {
        private final InputStream in;
        private long left;

        public LimitedInputStream(InputStream in, long limit) {
            this.in = in;
            this.left = limit;
        }

        public int read() throws IOException {
            if (left <= 0) return -1;
            int b = in.read();
            if (b != -1) left--;
            return b;
        }

        public int read(byte[] b, int off, int len) throws IOException {
            if (left <= 0) return -1;
            len = (int) Math.min(len, left);
            int read = in.read(b, off, len);
            if (read != -1) left -= read;
            return read;
        }

        public void close() throws IOException {
            in.close();
        }
    }

    public static class MultipartBody {
        @FormParam("file")
        @PartType(MediaType.APPLICATION_OCTET_STREAM)
        public byte[] fileData;

        @FormParam("filename")
        @PartType(MediaType.TEXT_PLAIN)
        public String filename;
    }
}

