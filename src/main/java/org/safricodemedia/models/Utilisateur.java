package org.safricodemedia.models;

import java.util.HashMap;
import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;

@Entity
public class Utilisateur extends PanacheEntity {
    //
    public String idUser;
    public String nom;

    @ElementCollection
    public List<String> videos;

    public Utilisateur() {} // nécessaire pour la désérialisation JSON
}
