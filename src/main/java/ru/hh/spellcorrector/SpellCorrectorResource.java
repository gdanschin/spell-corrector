package ru.hh.spellcorrector;

import com.google.inject.Inject;
import ru.hh.spellcorrector.dto.NigmerDto;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Path("/")
public class SpellCorrectorResource {

  @Inject
  SpellCorrector corrector;

  @GET
  @Path("/")
  public NigmerDto correct(@QueryParam("q") String query) {
    try {
      return corrector.correct(query);
    } catch (RuntimeException e) {
      throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
    }
  }

}
