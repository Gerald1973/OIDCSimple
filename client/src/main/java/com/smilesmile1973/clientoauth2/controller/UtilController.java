package com.smilesmile1973.clientoauth2.controller;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/util")
public class UtilController {

    private static final Logger LOG = LoggerFactory.getLogger(UtilController.class);

    @GetMapping("server-time")
    public String getServerTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        String result = simpleDateFormat.format(new Date());
        return result;
    }

    @GetMapping("timezones")
    public String[] getTimeZones() {
        Set<String> zoneIds = new TreeSet<>(ZoneId.getAvailableZoneIds());
        LOG.info("Total timezones: {}",zoneIds.size());
        return zoneIds.toArray(new String[0]);
    }
}
