package org.safricodemedia.controllers;

import java.nio.file.Files;
import java.util.List;

import org.safricodemedia.models.VideoMedia;
import org.safricodemedia.models.VideoMediaSummary;

import io.quarkus.panache.common.Sort;
import jakarta.transaction.Transactional;
import jakarta.websocket.server.PathParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
//import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.nio.file.Files;
import java.nio.*;


@Path("/video")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VideoMediaController {


    @GET
    @Path("/files/{filename}")
    public Response getFile(@PathParam("filename") String filename)  {
        java.nio.file.Path path = java.nio.file.Path.of("videos/" + filename);
        if (!Files.exists(path)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(path.toFile()).build();
    }

    @POST
    @Transactional
    public Response create(VideoMedia videoMedia) {
        //videoMedia.persist();
        return Response.status(Response.Status.CREATED).entity(videoMedia).build();
    }

    // READ ALL
    // @GET
    // public List<VideoMediaSummary> getAll() {
    //     List<VideoMedia> videoMedias = VideoMedia.listAll(Sort.by("titre"));
    //     List<VideoMediaSummary> summaries = ((List<VideoMedia>) videoMedias).stream()
    // .map(v -> new VideoMediaSummary(v.id, v.titre))
    // .toList();
            
    //     return summaries;
    // }

    // READ ONE
    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        // VideoMedia videoMedia = VideoMedia.findById(id);
        // if (videoMedia == null) {
        //     return Response.status(Response.Status.NOT_FOUND).build();
        // }
        return Response.ok().build();
    }

    // UPDATE
    @PUT
    @Path("/{id}")
    @Transactional
    public Response update(@PathParam("id") Long id, VideoMedia updatedVideo) {
        // VideoMedia videoMedia = VideoMedia.findById(id);
        // if (videoMedia == null) {
        //     return Response.status(Response.Status.NOT_FOUND).build();
        // }
        
        // videoMedia.titre = updatedVideo.titre;
        // videoMedia.video = updatedVideo.video;
        
        return Response.ok().build();
    }

    // DELETE
    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        // boolean deleted = VideoMedia.deleteById(id);
        // if (!deleted) {
        //     return Response.status(Response.Status.NOT_FOUND).build();
        // }
        return Response.noContent().build();
    }

    // Endpoint spécial pour le téléchargement de la vidéo
    @GET
    @Path("/{id}/video")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getVideo(@PathParam("id") Long id) {
        // VideoMedia videoMedia = VideoMedia.findById(id);
        // if (videoMedia == null || videoMedia.video == null) {
        //     return Response.status(Response.Status.NOT_FOUND).build();
        // }
        return Response.ok()
               .header("Content-Disposition", "attachment; filename=\".mp4\"")
               .build();
    }
}
