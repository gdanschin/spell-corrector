package ru.hh.spellcorrector;

import com.google.inject.Inject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class SpellCorrectorResource {

  @Inject
  SpellCorrector corrector;

  @GET
  @Path("/")
  @Produces(MediaType.TEXT_XML + ";charset=utf-8")
  public Response correct(@QueryParam("q") String query) {
    try {
      return Response.ok(corrector.correct(query)).build();
    } catch (RuntimeException e) {
      throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
    }
  }

}
