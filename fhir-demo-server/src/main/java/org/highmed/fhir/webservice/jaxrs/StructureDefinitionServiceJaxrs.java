package org.highmed.fhir.webservice.jaxrs;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.highmed.fhir.webservice.specification.StructureDefinitionService;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.StructureDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(StructureDefinitionServiceJaxrs.PATH)
public class StructureDefinitionServiceJaxrs extends
		AbstractServiceJaxrs<StructureDefinition, StructureDefinitionService> implements StructureDefinitionService
{
	public static final String PATH = "StructureDefinition";

	private static final Logger logger = LoggerFactory.getLogger(StructureDefinitionServiceJaxrs.class);

	public StructureDefinitionServiceJaxrs(StructureDefinitionService delegate)
	{
		super(delegate);
	}

	@GET
	@Path("/{id}/{snapshot : [$]snapshot(/)?}")
	public Response getSnapshotExisting(@PathParam("snapshot") String snapshotPath, @PathParam("id") String id,
			@QueryParam("_format") String format, @Context UriInfo uri)
	{
		logger.trace("GET {}", uri.getRequestUri().toString());

		return delegate.getSnapshotExisting(snapshotPath, id, format, uri);
	}

	@POST
	@Path("/{id}/{snapshot : [$]snapshot(/)?}")
	public Response postSnapshotExisting(@PathParam("snapshot") String snapshotPath, String id,
			@QueryParam("_format") String format, @Context UriInfo uri)
	{
		logger.trace("POST {}", uri.getRequestUri().toString());

		return delegate.postSnapshotExisting(snapshotPath, id, format, uri);
	}

	@POST
	@Path("/{snapshot : [$]snapshot(/)?}")
	public Response snapshotNew(@PathParam("snapshot") String snapshotPath, @QueryParam("_format") String format,
			Parameters parameters, @Context UriInfo uri)
	{
		logger.trace("POST {}", uri.getRequestUri().toString());

		return delegate.snapshotNew(snapshotPath, format, parameters, uri);
	}
}
