package uk.co.tangent.resources;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.PATCH;
import uk.co.tangent.entities.Lane;
import uk.co.tangent.entities.Test;
import uk.co.tangent.services.LaneService;
import uk.co.tangent.services.TestService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/lane")
@Produces(MediaType.APPLICATION_JSON)
public class LaneResource {

    private final LaneService service;
    private final TestService tests;

    @Inject
    public LaneResource(LaneService laneService, TestService testService) {
        this.service = laneService;
        this.tests = testService;
    }

    @GET
    @Path("/{id}")
    @UnitOfWork
    public Lane getLane(@PathParam("id") Long id) {
        return service.getLane(id);
    }

    @POST
    @UnitOfWork
    public Lane insert(Lane lane) {
        lane.setId(null);
        return service.save(lane);
    }

    @PUT
    @Path("/{id}")
    @UnitOfWork
    public Lane update(@PathParam("id") Long id, Lane lane) {
        lane.setId(id);
        return service.save(lane);
    }

    @GET
    @UnitOfWork
    public List<Lane> list() {
        return service.getLanes();
    }

    @PATCH
    @Path("/{id}")
    @UnitOfWork
    public Lane control(@PathParam("id") Long id, String command) {

        Lane lane = service.getLane(id);
        if ("start".equals(command)) {
            lane.setActive(Boolean.TRUE);
        } else {
            lane.setActive(Boolean.FALSE);
        }
        return service.save(lane);
    }

    @POST
    @Path("/{id}/tests")
    @UnitOfWork
    public Lane addTest(@PathParam("id") Long id, String testPath) {
        Lane lane = service.getLane(id);
        Test test = tests.fromPath(testPath);
        lane.getTests().add(test);
        return service.save(lane);
    }

    @DELETE
    @Path("/{id}/tests")
    @UnitOfWork
    public Lane delTest(@PathParam("id") Long id, String testPath) {
        Test test = tests.fromPath(testPath);
        Lane lane = service.getLane(id);
        lane.getTests().remove(test);
        return service.save(lane);
    }
}
