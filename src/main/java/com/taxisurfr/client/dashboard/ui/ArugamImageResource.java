package com.taxisurfr.client.dashboard.ui;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeUri;
import com.taxisurfr.shared.model.RouteInfo;

public class ArugamImageResource implements ImageResource
{
    final RouteInfo routeInfo;

    public ArugamImageResource(RouteInfo routeInfo)
    {
        this.routeInfo = routeInfo;
    }

    @Override
    public String getName()
    {
        return "vehicle";
    }

    @Override
    public int getHeight()
    {
        return 100;
    }

    @Override
    public int getLeft()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public SafeUri getSafeUri()
    {
        return new SafeUri()
        {

            @Override
            public String asString()
            {
                return "/imageservice?image=" + routeInfo.getImage();
            }
        };
    }

    @Override
    public int getTop()
    {
        return 0;
    }

    @Override
    public String getURL()
    {
        return getSafeUri().toString();
    }

    @Override
    public int getWidth()
    {
        return 100;
    }

    @Override
    public boolean isAnimated()
    {
        return false;
    }

}
