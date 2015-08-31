package com.taxisurfr.shared.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class StatInfoTest
{

    @Test
    public void should_serialize_and_deserialize() throws IOException
    {
        StatInfo statInfo = new StatInfo();
        statInfo.setIp("xxxx");

        ObjectMapper mapper = new ObjectMapper();
        String serial = mapper.writeValueAsString(statInfo);

        statInfo = mapper.readValue(serial, StatInfo.class);

        Assert.assertTrue(statInfo.getIp().equals("xxxx"));
    }
}
