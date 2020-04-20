package org.highmed.dsf.fhir.service;

import java.util.ArrayList;
import java.util.List;

import org.highmed.dsf.fhir.service.exception.SnapshotBaseNotFoundException;
import org.hl7.fhir.r4.conformance.ProfileUtilities;
import org.hl7.fhir.r4.context.IWorkerContext;
import org.hl7.fhir.r4.hapi.ctx.HapiWorkerContext;
import org.hl7.fhir.r4.hapi.ctx.IValidationSupport;
import org.hl7.fhir.r4.model.StructureDefinition;
import org.hl7.fhir.utilities.validation.ValidationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.fhir.context.FhirContext;

public class SnapshotGeneratorImpl implements SnapshotGenerator
{
	private static final Logger logger = LoggerFactory.getLogger(SnapshotGeneratorImpl.class);

	private final IWorkerContext worker;

	public SnapshotGeneratorImpl(FhirContext fhirContext, IValidationSupport validationSupport)
	{
		worker = createWorker(fhirContext, validationSupport);
	}

	protected HapiWorkerContext createWorker(FhirContext context, IValidationSupport validationSupport)
	{
		return new HapiWorkerContext(context, validationSupport);
	}

	@Override
	public SnapshotWithValidationMessages generateSnapshot(StructureDefinition differential)
			throws SnapshotBaseNotFoundException
	{
		return generateSnapshot("", differential);
	}

	@Override
	public SnapshotWithValidationMessages generateSnapshot(String baseAbsoluteUrlPrefix,
			StructureDefinition differential) throws SnapshotBaseNotFoundException
	{
		logger.debug("Generating snapshot for StructureDefinition with id {}, url {}, version {}",
				differential.getIdElement().getIdPart(), differential.getUrl(), differential.getVersion());

		StructureDefinition base = worker.fetchResource(StructureDefinition.class, differential.getBaseDefinition());
		if (base == null)
			throw new SnapshotBaseNotFoundException(differential.getBaseDefinition());

		/* ProfileUtilities is not thread safe */
		List<ValidationMessage> messages = new ArrayList<>();
		ProfileUtilities profileUtils = new ProfileUtilities(worker, messages, null);
		profileUtils.generateSnapshot(base, differential, baseAbsoluteUrlPrefix, baseAbsoluteUrlPrefix, null);

		if (messages.isEmpty())
			logger.debug("Snapshot generated for StructureDefinition with id {}, url {}, version {}",
					differential.getIdElement().getIdPart(), differential.getUrl(), differential.getVersion());
		else
		{
			logger.warn("Snapshot not generated for StructureDefinition with id {}, url {}, version {}",
					differential.getIdElement().getIdPart(), differential.getUrl(), differential.getVersion());
			messages.forEach(m -> logger.warn("Issue while generating snapshot: {} - {} - {}", m.getDisplay(),
					m.getLine(), m.getMessage()));
		}

		return new SnapshotWithValidationMessages(differential, messages);
	}
}
