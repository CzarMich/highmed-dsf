package org.highmed.dsf.fhir.dao.command;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.WebApplicationException;

import org.highmed.dsf.fhir.authentication.User;
import org.highmed.dsf.fhir.dao.ResourceDao;
import org.highmed.dsf.fhir.dao.exception.ResourceDeletedException;
import org.highmed.dsf.fhir.dao.exception.ResourceNotFoundException;
import org.highmed.dsf.fhir.help.ExceptionHandler;
import org.highmed.dsf.fhir.help.ParameterConverter;
import org.highmed.dsf.fhir.help.ResponseGenerator;
import org.highmed.dsf.fhir.service.ReferenceExtractor;
import org.highmed.dsf.fhir.service.ReferenceResolver;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Resource;

public class ResolveReferencesCommand<R extends Resource, D extends ResourceDao<R>>
		extends AbstractCommandWithResource<R, D> implements Command
{
	private final ResolveReferencesHelper<R> resolveReferencesHelper;

	public ResolveReferencesCommand(int index, User user, Bundle bundle, BundleEntryComponent entry, String serverBase,
			AuthorizationHelper authorizationHelper, R resource, D dao, ExceptionHandler exceptionHandler,
			ParameterConverter parameterConverter, ResponseGenerator responseGenerator,
			ReferenceExtractor referenceExtractor, ReferenceResolver referenceResolver)
	{
		super(4, index, user, bundle, entry, serverBase, authorizationHelper, resource, dao, exceptionHandler,
				parameterConverter);

		resolveReferencesHelper = new ResolveReferencesHelper<R>(index, user, serverBase, referenceExtractor,
				referenceResolver, responseGenerator);
	}

	@Override
	public void preExecute(Map<String, IdType> idTranslationTable)
	{
	}

	@Override
	public void execute(Map<String, IdType> idTranslationTable, Connection connection)
			throws SQLException, WebApplicationException
	{
		R latest = latestOrErrorIfDeletedOrNotFound(idTranslationTable, connection);

		boolean resourceNeedsUpdated = resolveReferencesHelper.resolveReferences(idTranslationTable, connection,
				latest);
		if (resourceNeedsUpdated)
		{
			try
			{
				dao.updateSameRowWithTransaction(connection, latest);
			}
			catch (ResourceNotFoundException e)
			{
				throw exceptionHandler.internalServerError(e);
			}
		}
	}

	private R latestOrErrorIfDeletedOrNotFound(Map<String, IdType> idTranslationTable, Connection connection)
			throws SQLException
	{
		try
		{
			String id = idTranslationTable.getOrDefault(entry.getFullUrl(), resource.getIdElement()).getIdPart();
			return dao.readWithTransaction(connection, parameterConverter.toUuid(resource.getResourceType().name(), id))
					.orElseThrow(() -> exceptionHandler.internalServerError(new ResourceNotFoundException(id)));
		}
		catch (ResourceDeletedException e)
		{
			throw exceptionHandler.internalServerError(e);
		}
	}

	@Override
	public Optional<BundleEntryComponent> postExecute(Connection connection)
	{
		return Optional.empty();
	}
}
