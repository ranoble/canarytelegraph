package uk.co.tangent.resources;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.PATCH;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import uk.co.tangent.entities.Lane;
import uk.co.tangent.entities.Test;
import uk.co.tangent.injection.ServiceRegistry;
import uk.co.tangent.services.LaneService;
import uk.co.tangent.services.TestService;

@Path("/lane")
@Produces(MediaType.APPLICATION_JSON)
public class LaneResource {

    LaneService service;
    TestService tests;

    public LaneResource(ServiceRegistry services) {
        this.service = services.getLaneService();
        this.tests = services.getTestService();
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
