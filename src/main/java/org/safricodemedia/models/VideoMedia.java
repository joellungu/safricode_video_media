package org.safricodemedia.models;

import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

//@Entity
public class VideoMedia {

    public String titre;
    public byte[] video;
}
