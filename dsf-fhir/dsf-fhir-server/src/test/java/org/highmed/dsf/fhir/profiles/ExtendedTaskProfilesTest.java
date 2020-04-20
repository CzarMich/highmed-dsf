package org.highmed.dsf.fhir.profiles;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Paths;
import java.util.Collections;

import org.highmed.dsf.fhir.service.DefaultProfileValidationSupportWithCustomResources;
import org.highmed.dsf.fhir.service.SnapshotGenerator;
import org.highmed.dsf.fhir.service.SnapshotGenerator.SnapshotWithValidationMessages;
import org.highmed.dsf.fhir.service.SnapshotGeneratorImpl;
import org.highmed.dsf.fhir.service.StructureDefinitionReader;
import org.highmed.dsf.fhir.service.exception.SnapshotBaseNotFoundException;
import org.hl7.fhir.r4.model.StructureDefinition;
import org.hl7.fhir.r4.model.StructureDefinition.StructureDefinitionSnapshotComponent;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.fhir.context.FhirContext;

public class ExtendedTaskProfilesTest
{
	private static final Logger logger = LoggerFactory.getLogger(ExtendedTaskProfilesTest.class);

	@Test(expected = SnapshotBaseNotFoundException.class)
	public void testGeneratSnapshotForExtendedTaskWithoutBaseTaskSnapshot() throws Exception
	{
		FhirContext context = FhirContext.forR4();
		StructureDefinitionReader reader = new StructureDefinitionReader(context);

		StructureDefinition extendedTask = reader.readXml(Paths.get("src/test/resources/profiles/ExtendedTask.xml"));

		SnapshotGenerator generator = new SnapshotGeneratorImpl(context,
				new DefaultProfileValidationSupportWithCustomResources(Collections.emptyList(), Collections.emptyList(),
						Collections.emptyList()));

		generator.generateSnapshot(extendedTask);
	}

	@Test
	public void testGeneratSnapshotForExtendedTaskWithBaseTaskSnapshot() throws Exception
	{
		FhirContext context = FhirContext.forR4();
		StructureDefinitionReader reader = new StructureDefinitionReader(context);

		StructureDefinition baseTask = reader.readXml(Paths.get("src/test/resources/profiles/BaseTask.xml"));
		StructureDefinition extendedTask = reader.readXml(Paths.get("src/test/resources/profiles/ExtendedTask.xml"));

		SnapshotGenerator generator = new SnapshotGeneratorImpl(context,
				new DefaultProfileValidationSupportWithCustomResources(Collections.emptyList(), Collections.emptyList(),
						Collections.emptyList()));

		SnapshotWithValidationMessages baseTaskSnapshotResult = generator.generateSnapshot(baseTask);

		assertNotNull(baseTaskSnapshotResult);
		assertNotNull(baseTaskSnapshotResult.getSnapshot());
		assertNotNull(baseTaskSnapshotResult.getMessages());

		StructureDefinition baseTaskSnapshot = baseTaskSnapshotResult.getSnapshot();
		assertTrue(baseTaskSnapshot.hasDifferential());
		assertTrue(baseTaskSnapshot.hasSnapshot());

		logger.info("Snapshot generated for StructureDefinition with url {}\n{}",
				baseTaskSnapshotResult.getSnapshot().getUrl(),
				context.newXmlParser().encodeResourceToString(baseTaskSnapshotResult.getSnapshot()));
		assertTrue(baseTaskSnapshotResult.getMessages().isEmpty());

		generator = new SnapshotGeneratorImpl(context, new DefaultProfileValidationSupportWithCustomResources(
				Collections.singleton(baseTaskSnapshot), Collections.emptyList(), Collections.emptyList()));

		SnapshotWithValidationMessages extendedTaskSnaphotResult = generator.generateSnapshot(extendedTask);

		assertNotNull(extendedTaskSnaphotResult);
		assertNotNull(extendedTaskSnaphotResult.getSnapshot());
		assertNotNull(extendedTaskSnaphotResult.getMessages());
		assertTrue(extendedTaskSnaphotResult.getSnapshot().hasDifferential());
		assertTrue(extendedTaskSnaphotResult.getSnapshot().hasSnapshot());

		StructureDefinition extendedTaskSnapshot = extendedTaskSnaphotResult.getSnapshot();
		extendedTaskSnapshot.setSnapshot(
				new StructureDefinitionSnapshotComponent().setElement(extendedTaskSnapshot.getSnapshot().getElement()));

		logger.info("Snapshot generated for StructureDefinition with url {}\n{}",
				extendedTaskSnaphotResult.getSnapshot().getUrl(),
				context.newXmlParser().encodeResourceToString(extendedTaskSnapshot));

		assertTrue(extendedTaskSnaphotResult.getMessages().isEmpty());
	}
}
