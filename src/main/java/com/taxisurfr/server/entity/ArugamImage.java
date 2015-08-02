package com.taxisurfr.server.entity;

import com.google.appengine.api.datastore.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;
import com.taxisurfr.shared.model.ArugamImageInfo;

@Entity
public class ArugamImage extends ArugamEntity<ArugamImageInfo>
{

    private static final long serialVersionUID = 1L;

    @Id Long id;

    private Blob image;

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
        info.setId(id);
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