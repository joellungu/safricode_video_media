package org.safricodemedia.controllers;

import java.util.List;

import org.safricodemedia.models.Utilisateur;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("utilisateur")
public class UtilisateurController {
    //
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response saveUtilisateur(Utilisateur utilisateur) {
        //
        Utilisateur utilisateur2 = Utilisateur.find("idUser", utilisateur.idUser).firstResult();
        //
        if(utilisateur2 != null){
            //
            utilisateur2.videos = utilisateur.videos;
            utilisateur2.nom = utilisateur.nom;
            //
            return Response.ok().build();
        } else {
            //
            Utilisateur utilisateur3 = new Utilisateur();
            utilisateur3.idUser = utilisateur.idUser;
            utilisateur3.nom = utilisateur.nom;
            utilisateur3.videos = utilisateur.videos;
            utilisateur3.persist();
            //
            return Response.ok().build();
        }
    }

    @PUT
    @Path("videos")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response updateListe(List<String> videos, @QueryParam("idUser") String idUser) {
        //
        Utilisateur utilisateur = Utilisateur.find("idUser", idUser).firstResult();
        //
        if(utilisateur != null){
            //
            utilisateur.videos.addAll(videos);
            //
        }
        return Response.ok().build();
    }

    
    @DELETE
    @Path("supprimer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response supprimerVideo(String video, @QueryParam("idUser")  String idUser) {
        //
        System.out.println("IdUser: "+idUser);
        //
        Utilisateur utilisateur = Utilisateur.find("idUser", idUser).firstResult();
        //
        utilisateur.videos.remove(video);
        //
        return Response.ok().build();
        
    }

    @GET
    //@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response tousUtilisateur() {
        //
        List<Utilisateur> utilisateurs = Utilisateur.listAll();
        //
        return Response.ok().entity(utilisateurs).build();
    }

    @GET
    @Path("videos")
    //@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response videoUtilisateur(@QueryParam("idUser") String idUser) {
        //
        Utilisateur utilisateur = Utilisateur.find("idUser", idUser).firstResult();
        //
        return Response.ok().entity(utilisateur.videos).build();
    }
}
