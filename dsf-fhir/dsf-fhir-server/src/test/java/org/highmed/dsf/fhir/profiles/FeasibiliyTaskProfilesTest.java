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
import org.hl7.fhir.r4.model.StructureDefinition;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.fhir.context.FhirContext;

public class FeasibiliyTaskProfilesTest
{
	private static final Logger logger = LoggerFactory.getLogger(FeasibiliyTaskProfilesTest.class);

	@Test(expected = RuntimeException.class)
	public void testGeneratSnapshotForTaskRequestSimpleFeasibilityWithoutBaseSnapshot() throws Exception
	{
		FhirContext context = FhirContext.forR4();
		StructureDefinitionReader reader = new StructureDefinitionReader(context);

		StructureDefinition highmedTaskBase = reader
				.readXml(Paths.get("src/test/resources/profiles/highmed-task-base-0.1.0.xml"));
		StructureDefinition highmedTaskRequestSimpleFeasibility = reader
				.readXml(Paths.get("src/test/resources/profiles/highmed-task-request-simple-feasibility-0.1.0.xml"));

		SnapshotGenerator generator = new SnapshotGeneratorImpl(context,
				new DefaultProfileValidationSupportWithCustomResources(Collections.emptyList(), Collections.emptyList(),
						Collections.emptyList()));

		SnapshotWithValidationMessages highmedTaskBaseSnapshot = generator.generateSnapshot(highmedTaskBase);

		assertNotNull(highmedTaskBaseSnapshot);
		assertNotNull(highmedTaskBaseSnapshot.getSnapshot());
		assertNotNull(highmedTaskBaseSnapshot.getMessages());
		assertTrue(highmedTaskBaseSnapshot.getMessages().isEmpty());

		generator.generateSnapshot(highmedTaskRequestSimpleFeasibility);
	}

	@Test
	public void testGeneratSnapshotForTaskRequesSimpleFeasibilityWithBaseSnapshot() throws Exception
	{
		FhirContext context = FhirContext.forR4();
		StructureDefinitionReader reader = new StructureDefinitionReader(context);

		StructureDefinition highmedTaskBase = reader
				.readXml(Paths.get("src/test/resources/profiles/highmed-task-base-0.1.0.xml"));
		StructureDefinition highmedTaskRequestSimpleFeasibility = reader
				.readXml(Paths.get("src/test/resources/profiles/highmed-task-request-simple-feasibility-0.1.0.xml"));

		SnapshotGenerator generator = new SnapshotGeneratorImpl(context,
				new DefaultProfileValidationSupportWithCustomResources(Collections.emptyList(), Collections.emptyList(),
						Collections.emptyList()));

		SnapshotWithValidationMessages highmedTaskBaseSnapshot = generator.generateSnapshot(highmedTaskBase);

		assertNotNull(highmedTaskBaseSnapshot);
		assertNotNull(highmedTaskBaseSnapshot.getSnapshot());
		assertNotNull(highmedTaskBaseSnapshot.getMessages());
		assertTrue(highmedTaskBaseSnapshot.getMessages().isEmpty());

		logger.info("Snapshot generated for StructureDefinition with url {}\n{}",
				highmedTaskBaseSnapshot.getSnapshot().getUrl(), context.newXmlParser().setPrettyPrint(true)
						.encodeResourceToString(highmedTaskBaseSnapshot.getSnapshot()));

		generator = new SnapshotGeneratorImpl(context,
				new DefaultProfileValidationSupportWithCustomResources(
						Collections.singleton(highmedTaskBaseSnapshot.getSnapshot()), Collections.emptyList(),
						Collections.emptyList()));

		SnapshotWithValidationMessages highmedTaskRequestSimpleFeasibilitySnapshot = generator
				.generateSnapshot(highmedTaskRequestSimpleFeasibility);

		assertNotNull(highmedTaskRequestSimpleFeasibilitySnapshot);
		assertNotNull(highmedTaskRequestSimpleFeasibilitySnapshot.getSnapshot());
		assertNotNull(highmedTaskRequestSimpleFeasibilitySnapshot.getMessages());
		assertTrue(highmedTaskRequestSimpleFeasibilitySnapshot.getMessages().isEmpty());
	}
}
