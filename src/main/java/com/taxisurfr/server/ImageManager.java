package com.taxisurfr.server;

import java.util.List;
import java.util.logging.Logger;


import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.Transform;
import com.googlecode.objectify.ObjectifyService;
import com.taxisurfr.server.entity.Agent;
import com.taxisurfr.server.entity.ArugamImage;
import com.taxisurfr.server.entity.Finance;
import com.taxisurfr.shared.model.ArugamImageInfo;

/**
 * The server-side implementation of the RPC service.
 */
public class ImageManager extends Manager
{
    private static final Logger logger = Logger.getLogger(ImageManager.class.getName());

    public ImageManager()
    {

        ObjectifyService.register(ArugamImage.class);
    }
    public Long addImage(byte[] image) throws IllegalArgumentException
    {

        ImagesService imagesService = ImagesServiceFactory.getImagesService();

        Image oldImage = ImagesServiceFactory.makeImage(image);
        Transform resize = ImagesServiceFactory.makeResize(200, 300);

        Image newImage = imagesService.applyTransform(resize, oldImage);
        image = newImage.getImageData();
        Long id = null;
            ArugamImageInfo info = new ArugamImageInfo();
            info.setContent(image);
            ArugamImage arugamImage = ArugamImage.getArugamImage(info);
            ObjectifyService.ofy().save().entity(arugamImage).now();
        return  arugamImage.id;
    }

    public byte[] getImage(Long imageId)
    {
        logger.info("getImage "+imageId);
        ArugamImage arugamImage = ObjectifyService.ofy().load().type(ArugamImage.class).id(imageId).now();
        if (arugamImage != null) {
            logger.info("image found");
            return arugamImage.getImage().getBytes();
        }
        return null;
    }

}
