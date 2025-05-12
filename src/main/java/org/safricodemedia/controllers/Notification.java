package org.safricodemedia.controllers;

import java.time.Duration;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import org.safricodemedia.models.Appareil;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class Notification {

    @Inject
    VideoResource videoResource;
    //
    public static LinkedList<Appareil> appareils = new LinkedList<>();

    //@Scheduled(every="30s")
    void initApp() throws InterruptedException {
        //
        //TimeUnit.SECONDS.sleep(10);
        //
        LocalTime now = LocalTime.now();
        System.err.println("Ca tourne et tout."+Notification.appareils.size());
        
        Notification.appareils.removeIf(map -> {
            Object timerObj = map.duree;
            if (timerObj instanceof String) {
                try {
                    LocalTime timer = LocalTime.parse((String) timerObj);
                    Duration duration = Duration.between(timer, now);
                    System.err.println("Data: "+duration.toMinutes());
                    return duration.toMinutes() > 2;
                } catch (Exception e) {
                    System.err.println("Format de timer invalide : " + timerObj);
                    return false;
                }
            }
            return false;
        });
        //
        //
    }
}
