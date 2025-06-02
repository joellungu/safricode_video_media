package org.safricodemedia;
//package org.acme;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import jakarta.ws.rs.QueryParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Predicate;

import org.safricodemedia.models.VideoInfo;

@ServerEndpoint("/ws/video/{username}/{idUser}/{video}")
@ApplicationScoped
public class WebSocketService {

    private static final Set<HashMap<String, Object>> sessions = new CopyOnWriteArraySet<>();
    private static final ObjectMapper mapper = new ObjectMapper();
    String videoInfo = "";

    @SuppressWarnings("unchecked")
    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username
    , @PathParam("idUser") String idUser
    , @PathParam("video") String video) {
        //Session
        System.out.println("Nom connecté : " + username);
        //
        HashMap mHashMap = new HashMap<>();
        mHashMap.put("username", username);
        mHashMap.put("idUser", idUser);
        mHashMap.put("video", video);
        mHashMap.put("session", session);
        //
        sessions.add(mHashMap);
        System.out.println("Client connecté : " + username);
        System.out.println("Client connecté : " + idUser);
        System.out.println("Client connecté : " + video);
        System.out.println("Client connecté : " + session.getId());
    }

    @OnClose
    public void onClose(Session session) {
        //remove(session)
        //
        sessions.forEach((e)->{
            if(e.get("session").equals(session)){
                sessions.remove(e); 
            }
        });
        System.out.println("Client déconnecté : " + session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        //sessions.remove(session);
        sessions.forEach((e)->{
            if(e.get("session").equals(session)){
                sessions.remove(e); 
            }
        });
        throwable.printStackTrace();
    }

    @OnMessage
    public void onMessage(String message, Session session) throws JsonProcessingException {
        // Si tu veux que le client envoie des commandes
        if(message.equals("encour")){
            session.getAsyncRemote().sendText(videoInfo);
        }
        //
        if(message.equals("appareil")){
            List<HashMap> l = new LinkedList<>();
            //
            sessions.forEach((e) -> {
                //
                HashMap dada = new HashMap<>();
                dada.put("username", e.get("username"));
                dada.put("idUser", e.get("idUser"));
                dada.put("video", e.get("video"));
                l.add(dada);
            });
            //
            String reponses = mapper.writeValueAsString(l);
            session.getAsyncRemote().sendText(reponses);
        }
        if(message.contains("video")){
            //List<HashMap> l = new LinkedList<>();
            //
            String[] v = message.split(":");
            sessions.forEach((e) -> {
                if(((Session)e.get("session")).equals(session)){
                    e.put("video", v[v.length - 1]);
                }
                
            });
        }
        System.out.println("Message reçu: " + message);
    }

    public void broadcast(String video) {
        videoInfo = video;
        // try {
        //     //message = mapper.writeValueAsString(video);
        // } catch (Exception e) {
        //     e.printStackTrace();
        //     return;
        // }

        

        for (HashMap session : sessions) {
            System.out.println("Session: "+((Session)session.get("session")).getId());
            System.out.println("Session: "+((Session)session.get("session")).isOpen());
            if (((Session)session.get("session")).isOpen()) {
                ((Session)session.get("session")).getAsyncRemote().sendText(video);
            }
        }
    }

    public void getAll(Session session) {
        String message;
        try {
            message = mapper.writeValueAsString(session);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        session.getAsyncRemote().sendText(message);
    }
}
