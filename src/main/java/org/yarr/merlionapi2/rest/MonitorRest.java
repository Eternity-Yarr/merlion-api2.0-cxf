package org.yarr.merlionapi2.rest;

import https.api_merlion_com.dl.mlservice2.ArrayOfShipmentMethodsResult;
import https.api_merlion_com.dl.mlservice2.ShipmentMethodsResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yarr.merlionapi2.MLPortProvider;
import org.yarr.merlionapi2.persistence.Database;
import org.yarr.merlionapi2.scheduler.TaskQueue;
import org.yarr.merlionapi2.service.BindService;
import org.yarr.merlionapi2.service.CatalogService;
import org.yarr.merlionapi2.service.MonitorService;
import org.yarr.merlionapi2.service.TrackService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

@Path("/_monitor")
@Produces(MediaType.APPLICATION_JSON)
@Component
public class MonitorRest
{
    private final CatalogService catalogService;
    private final TrackService trackService;
    private final BindService bindService;
    private final Database db;
    private final MLPortProvider portProvider;
    @Autowired
    public MonitorRest(CatalogService catalogService,
                       TrackService trackService,
                       BindService bindService,
                       Database database,
                       MLPortProvider portProvider) {
        this.catalogService = catalogService;
        this.trackService = trackService;
        this.bindService = bindService;
        this.db = database;
        this.portProvider = portProvider;
    }

    @GET
    @Path("/")
    public Map<String, Object> status() {
        boolean dbAccessible = false;
        List<String> error = new ArrayList<>();
        try(Connection con = db.c();
            Statement stm = con.createStatement();
            ResultSet rs = stm.executeQuery("SELECT 1")) {
            if(rs.next() && rs.getInt(1) == 1)
                dbAccessible = true;
        } catch (SQLException e) {
            error.add(e.getClass().getName() + ": " + e.getMessage());
        }

        String port = null;
        try {
            port = portProvider.get().helloWorld("test");
        } catch (Exception e) {
            error.add(e.getClass().getName() + ": " + e.getMessage());
        }

        Map<String, Object> stats = new TreeMap<>();
        stats.put("1. Tasks in queue", TaskQueue.i().size());
        stats.put("4. Catalogs count", catalogService.get().nodes().size());
        stats.put("6. Currently tracking categories", trackService.all().nodes());
        stats.put("5. Bindings", bindService.all().bonds().size());
        stats.put("2. Database accessible", dbAccessible);
        stats.put("3. Merlion API accesible", port != null);
        if(!error.isEmpty())
            stats.put("Errors", error);
        List<String> events = new ArrayList<>(MonitorService.info());
        Collections.reverse(events);
        stats.put("7. Logging events", events);
        return stats;
    }
}
