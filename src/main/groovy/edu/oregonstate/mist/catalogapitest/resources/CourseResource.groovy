package edu.oregonstate.mist.catalogapitest.resources

import edu.oregonstate.mist.catalogapitest.core.ErrorPOJO
import edu.oregonstate.mist.catalogapitest.core.Course
import edu.oregonstate.mist.catalogapitest.db.CourseDAO
import io.dropwizard.jersey.params.IntParam
import com.google.common.base.Optional
import javax.validation.Valid
import javax.ws.rs.Consumes
import javax.ws.rs.DELETE
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriInfo
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException

/**
 * Course resource class.
 */
@Path("/course")
@Produces(MediaType.APPLICATION_JSON)
class CourseResource {

    private final CourseDAO courseDAO

    @Context
    UriInfo uriInfo

    /**
     * Constructs the object after receiving and storing courseDAO instance.
     *
     * @param courseDAO
     */
    public CourseResource(CourseDAO courseDAO) {
        this.courseDAO = courseDAO
    }

    // /course ---------------------------------------------------------------------------------------------------------

    /**
     * Responds to POST requests by creating and returning a course.
     *
     * @param newCourse
     * @return response containing the result or error message
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postUser(@Valid Course newCourse) {

        Response returnResponse
        URI createdURI

        try {
            courseDAO.postCourse(newCourse.getCrn(), newCourse.getCourseName(), newCourse.getInstructor(),
                    newCourse.getDay(), newCourse.getTime(), newCourse.getLocation())

            createdURI = URI.create(uriInfo.getPath() + "/" + courseDAO.getLatestCidNumber())
            returnResponse = Response.created(createdURI).build()

        } catch (UnableToExecuteStatementException e) {

            String constraintError = e.getMessage()
            ErrorPOJO returnError

            if (constraintError.contains("COURSES_UK_CRN")) {

                // CRN number is not unique
                returnError = new ErrorPOJO(errorMessage: "CRN is not unique", errorCode: Response.Status.CONFLICT.getStatusCode())
            } else {

                // Some other error, should be logged
                System.out.println(e.localizedMessage)
                returnError = new ErrorPOJO(errorMessage: "Unknown Error", errorCode: Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
            }

            returnResponse = Response.status(returnError.getErrorCode()).entity(returnError).build()
        }

        returnResponse
    }

    /**
     * Responds to GET requests and retrieves all course objects
     *
     * @return list of all courses, otherwise empty
     */
    @GET
    @Path('/all')
    @Produces(MediaType.APPLICATION_JSON)
    public List<Course> getByCrn() {
        courseDAO.allCourses
    }

    // CRN specific requests -------------------------------------------------------------------------------------------

    /**
     * Responds to GET requests and retrieves course with a specific crn.
     *
     * @param crn
     * @return response containing the result or error message
     */
    @GET
    @Path('{crn}')
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByCrn(@PathParam('crn') IntParam crn) {

        Course courses = courseDAO.getByCrn(crn.get())

        Response returnResponse

        if (courses == null) {
            ErrorPOJO returnError = new ErrorPOJO(errorMessage: "Resource Not Found.", errorCode: Response.Status.NOT_FOUND.getStatusCode())
            returnResponse = Response.status(Response.Status.NOT_FOUND).entity(returnError).build()
        } else {
            returnResponse = Response.ok(courses).build()
        }

        returnResponse
    }

    /**
     * Responds to PUT requests depending on the state of the course object,
     * either through updating existing object with PUT request or
     * creating with POST request if not already existing.
     *
     * @param crn
     * @param newCourse
     * @return response containing the result or error message
     */
    @PUT
    @Path("{crn}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putByCrn(@PathParam("crn") Integer crn , Course newCourse) {

        Response returnResponse

        Course checkForCourseCrn = courseDAO.getByCrn(crn)

        // If course does not already exist - POST it!
        if (checkForCourseCrn == null) {
            courseDAO.postByCrn(crn, newCourse.getCourseName(), newCourse.getInstructor(), newCourse.getDay(),
                    newCourse.getTime(), newCourse.getLocation())
            returnResponse = Response.created().build()

        } else {

            // Otherwise PUT it!
            Optional<String> newCourseName = Optional.of( newCourse.getCourseName() )
            Optional<String> newInstructor = Optional.of( newCourse.getInstructor() )
            Optional<String> newDay = Optional.of( newCourse.getDay() )
            Optional<String> newTime = Optional.of( newCourse.getTime() )
            Optional<String> newLocation = Optional.of( newCourse.getLocation() )

            courseDAO.putByCrn(crn, newCourseName.or(checkForCourseCrn.getCourseName()),
                    newInstructor.or(checkForCourseCrn.getInstructor()), newDay.or(checkForCourseCrn.getDay()),
                    newTime.or(checkForCourseCrn.getTime()), newLocation.or(checkForCourseCrn.getLocation())
            )

            returnResponse = Response.ok().build()
        }

        returnResponse
    }

    /**
     * Responds to DELETE requests and removes a course object.
     *
     * @param crn
     * @return response containing the result or error message
     */
    @Path("{crn}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteByCrn(@PathParam("crn") Integer crn) {
        courseDAO.deleteByCrn(crn)
        Response.ok().build()
    }


    // Name specific requests ------------------------------------------------------------------------------------------

    /**
     * Responds to GET requests and retrieves all courses of a specific name.
     *
     * @param courseName
     * @return list of courses of specific name, otherwise empty
     */
    @GET
    @Path('/name/{courseName}')
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByCourseName(@PathParam('courseName') String courseName) {

        Course courses = courseDAO.getByName(courseName)

        Response returnResponse

        if (courses == null) {
            ErrorPOJO returnError = new ErrorPOJO(errorMessage: "Resource Not Found.", errorCode: Response.Status.NOT_FOUND.getStatusCode())
            returnResponse = Response.status(Response.Status.NOT_FOUND).entity(returnError).build()
        } else {
            returnResponse = Response.ok(courses).build()
        }

        returnResponse
    }

    // Location specific requests --------------------------------------------------------------------------------------

    /**
     * Responds to GET requests and retrieves all courses of a specific location.
     *
     * @param location
     * @return list of courses of specific location, otherwise empty
     */
    @GET
    @Path('/location/{location}')
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCourseByLocation(@PathParam('location') String location) {
        final List<Course> courses = courseDAO.getByLocation(location)

        if (courses.isEmpty()) {
            throw new WebApplicationException(404)
        }

        courses
    }
}
