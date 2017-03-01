package uk.co.tangent.resources;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.PATCH;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import uk.co.tangent.entities.Lane;
import uk.co.tangent.entities.Test;
import uk.co.tangent.services.LaneService;
import uk.co.tangent.services.TestService;

@Path("/lane")
@Produces(MediaType.APPLICATION_JSON)
@Api("/lane")
public class LaneResource {

    private final LaneService service;
    private final TestService tests;

    @Inject
    public LaneResource(LaneService laneService, TestService testService) {
        this.service = laneService;
        this.tests = testService;
    }

    @GET
    @UnitOfWork
    @ApiOperation(value = "List Test Lanes", response = Lane.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Lane.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Invalid JSON request"),
            @ApiResponse(code = 500, message = "Internal server error") })
    public List<Lane> list() {
        return service.getLanes();
    }

    @GET
    @Path("/{id}")
    @UnitOfWork
    @ApiOperation(value = "Fetch a Test Lane", notes = "Returns a specifc Test Lane..", response = Lane.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "A list of individuals matching the criteria.", response = Lane.class),
            @ApiResponse(code = 400, message = "Invalid JSON request"),
            @ApiResponse(code = 404, message = "No matching lanes"),
            @ApiResponse(code = 500, message = "Internal server error") })
    public Response getLane(@PathParam("id") Long id) {
        Lane lane = service.getLane(id);
        if (lane == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(service.getLane(id)).build();
    }

    @POST
    @UnitOfWork
    @ApiOperation(value = "Insert a new Test Lane", notes = "Insert a lane", response = Lane.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Inserted.", response = Lane.class),
            @ApiResponse(code = 400, message = "Invalid JSON request"),
            @ApiResponse(code = 500, message = "Internal server error") })
    public Response insert(Lane lane) {
        lane.setId(null);
        return Response.status(Status.CREATED).entity(service.save(lane))
                .build();
    }

    @PUT
    @Path("/{id}")
    @UnitOfWork
    @ApiOperation(value = "Update a Test Lane", response = Lane.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Updated.", response = Lane.class),
            @ApiResponse(code = 400, message = "Invalid JSON request"),
            @ApiResponse(code = 500, message = "Internal server error") })
    public Response update(@PathParam("id") Long id, Lane lane) {
        if (service.getLane(id) == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        lane.setId(id);
        return Response.ok(service.save(lane)).build();
    }

    @PATCH
    @Path("/{id}")
    @UnitOfWork
    @ApiOperation(value = "Enable or disable a lane.", notes = "", response = Lane.class)
    public Lane control(
            @PathParam("id") Long id,
            @ApiParam(value = "\"start\" to activate a Lane, any other string will stop it.", example = "/test/1", required = true) String command) {

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
    @ApiOperation(value = "Add a test to the lane.", notes = "This will add the test to the lane, and will add them to the lande randomiser.", response = Lane.class)
    public Lane addTest(
            @PathParam("id") Long id,
            @ApiParam(value = "The *relative* path of the test to add.", example = "/test/1", required = true) String testPath) {
        Lane lane = service.getLane(id);
        Test test = tests.fromPath(testPath);
        lane.getTests().add(test);
        return service.save(lane);
    }

    @DELETE
    @Path("/{id}/tests")
    @UnitOfWork
    @ApiOperation(value = "Delete a test from the lane.", notes = "This will remove the test from the lane.", response = Lane.class)
    public Lane delTest(
            @PathParam("id") Long id,
            @ApiParam(value = "The *relative* path of the test to remove.", example = "/test/1", required = true) String testPath) {
        Test test = tests.fromPath(testPath);
        Lane lane = service.getLane(id);
        lane.getTests().remove(test);
        return service.save(lane);
    }
}
