package org.highmed.dsf.fhir.webservice.secure;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.highmed.dsf.fhir.authorization.StructureDefinitionAuthorizationRule;
import org.highmed.dsf.fhir.dao.StructureDefinitionDao;
import org.highmed.dsf.fhir.help.ExceptionHandler;
import org.highmed.dsf.fhir.help.ParameterConverter;
import org.highmed.dsf.fhir.help.ResponseGenerator;
import org.highmed.dsf.fhir.service.ReferenceResolver;
import org.highmed.dsf.fhir.webservice.specification.StructureDefinitionService;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.StructureDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StructureDefinitionServiceSecure
		extends AbstractResourceServiceSecure<StructureDefinitionDao, StructureDefinition, StructureDefinitionService>
		implements StructureDefinitionService
{
	private static final Logger logger = LoggerFactory.getLogger(StructureDefinitionServiceSecure.class);

	public StructureDefinitionServiceSecure(StructureDefinitionService delegate, String serverBase,
			ResponseGenerator responseGenerator, ReferenceResolver referenceResolver,
			StructureDefinitionDao structureDefinitionDao, ExceptionHandler exceptionHandler,
			ParameterConverter parameterConverter, StructureDefinitionAuthorizationRule authorizationRule)
	{
		super(delegate, serverBase, responseGenerator, referenceResolver, StructureDefinition.class,
				structureDefinitionDao, exceptionHandler, parameterConverter, authorizationRule);
	}

	@Override
	public Response postSnapshotNew(String snapshotPath, Parameters parameters, UriInfo uri, HttpHeaders headers)
	{
		logger.debug("Current user '{}', role '{}'", getCurrentUser().getName(), getCurrentUser().getRole());

		return delegate.postSnapshotNew(snapshotPath, parameters, uri, headers);
	}

	@Override
	public Response getSnapshotNew(String snapshotPath, UriInfo uri, HttpHeaders headers)
	{
		logger.debug("Current user '{}', role '{}'", getCurrentUser().getName(), getCurrentUser().getRole());

		return delegate.getSnapshotNew(snapshotPath, uri, headers);
	}

	@Override
	public Response postSnapshotExisting(String snapshotPath, String id, UriInfo uri, HttpHeaders headers)
	{
		logger.debug("Current user '{}', role '{}'", getCurrentUser().getName(), getCurrentUser().getRole());

		return delegate.postSnapshotExisting(snapshotPath, id, uri, headers);
	}

	@Override
	public Response getSnapshotExisting(String snapshotPath, String id, UriInfo uri, HttpHeaders headers)
	{
		logger.debug("Current user '{}', role '{}'", getCurrentUser().getName(), getCurrentUser().getRole());

		return delegate.getSnapshotExisting(snapshotPath, id, uri, headers);
	}
}
