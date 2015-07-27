package com.taxisurfr.server.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;
import com.taxisurfr.shared.model.ArugamImageInfo;

@Entity
public class ArugamImage extends ArugamEntity<ArugamImageInfo>
{
    public Key getKey()
    {
        return key;
    }

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Key key;

    private Blob image;

    @Override
    public void setKey(Key key)
    {
        this.key = key;
    }

    public Blob getImage()
    {
        return image;
    }

    public void setImage(Blob image)
    {
        this.image = image;
    }

    @Override
    public ArugamImageInfo getInfo()
    {
        ArugamImageInfo info = new ArugamImageInfo();
        info.setId(key.getId());
        info.setContent(image.getBytes());
        return info;
    }

    public static ArugamImage getArugamImage(ArugamImageInfo info)
    {
        ArugamImage arugamImage = new ArugamImage();
        arugamImage.setImage(new Blob(info.getContent()));
        return arugamImage;
    }
}