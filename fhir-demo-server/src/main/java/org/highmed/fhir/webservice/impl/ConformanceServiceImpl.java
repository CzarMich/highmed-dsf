package org.highmed.fhir.webservice.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.highmed.fhir.help.ParameterConverter;
import org.highmed.fhir.search.SearchQueryParameter.SearchParameterDefinition;
import org.highmed.fhir.search.parameters.CodeSystemIdentifier;
import org.highmed.fhir.search.parameters.CodeSystemUrl;
import org.highmed.fhir.search.parameters.CodeSystemVersion;
import org.highmed.fhir.search.parameters.EndpointIdentifier;
import org.highmed.fhir.search.parameters.EndpointName;
import org.highmed.fhir.search.parameters.EndpointOrganization;
import org.highmed.fhir.search.parameters.HealthcareServiceActive;
import org.highmed.fhir.search.parameters.HealthcareServiceIdentifier;
import org.highmed.fhir.search.parameters.LocationIdentifier;
import org.highmed.fhir.search.parameters.OrganizationActive;
import org.highmed.fhir.search.parameters.OrganizationEndpoint;
import org.highmed.fhir.search.parameters.OrganizationIdentifier;
import org.highmed.fhir.search.parameters.OrganizationName;
import org.highmed.fhir.search.parameters.PatientActive;
import org.highmed.fhir.search.parameters.PatientIdentifier;
import org.highmed.fhir.search.parameters.PractitionerActive;
import org.highmed.fhir.search.parameters.PractitionerIdentifier;
import org.highmed.fhir.search.parameters.PractitionerRoleActive;
import org.highmed.fhir.search.parameters.PractitionerRoleIdentifier;
import org.highmed.fhir.search.parameters.ResearchStudyIdentifier;
import org.highmed.fhir.search.parameters.ResourceId;
import org.highmed.fhir.search.parameters.ResourceLastUpdated;
import org.highmed.fhir.search.parameters.StructureDefinitionIdentifier;
import org.highmed.fhir.search.parameters.StructureDefinitionUrl;
import org.highmed.fhir.search.parameters.StructureDefinitionVersion;
import org.highmed.fhir.search.parameters.SubscriptionChannelPayload;
import org.highmed.fhir.search.parameters.SubscriptionChannelType;
import org.highmed.fhir.search.parameters.SubscriptionCriteria;
import org.highmed.fhir.search.parameters.SubscriptionStatus;
import org.highmed.fhir.search.parameters.TaskIdentifier;
import org.highmed.fhir.search.parameters.TaskRequester;
import org.highmed.fhir.search.parameters.TaskStatus;
import org.highmed.fhir.search.parameters.ValueSetIdentifier;
import org.highmed.fhir.search.parameters.ValueSetUrl;
import org.highmed.fhir.search.parameters.ValueSetVersion;
import org.highmed.fhir.webservice.specification.ConformanceService;
import org.highmed.fhir.websocket.ServerEndpoint;
import org.hl7.fhir.r4.model.CapabilityStatement;
import org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementImplementationComponent;
import org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementKind;
import org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementRestComponent;
import org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementRestResourceComponent;
import org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementRestResourceOperationComponent;
import org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementRestResourceSearchParamComponent;
import org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementSoftwareComponent;
import org.hl7.fhir.r4.model.CapabilityStatement.ConditionalDeleteStatus;
import org.hl7.fhir.r4.model.CapabilityStatement.ConditionalReadStatus;
import org.hl7.fhir.r4.model.CapabilityStatement.ReferenceHandlingPolicy;
import org.hl7.fhir.r4.model.CapabilityStatement.ResourceVersionPolicy;
import org.hl7.fhir.r4.model.CapabilityStatement.RestfulCapabilityMode;
import org.hl7.fhir.r4.model.CapabilityStatement.TypeRestfulInteraction;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.CodeType;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Endpoint;
import org.hl7.fhir.r4.model.Enumerations.FHIRVersion;
import org.hl7.fhir.r4.model.Enumerations.PublicationStatus;
import org.hl7.fhir.r4.model.Enumerations.SearchParamType;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.HealthcareService;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Provenance;
import org.hl7.fhir.r4.model.ResearchStudy;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.StructureDefinition;
import org.hl7.fhir.r4.model.Subscription;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.UrlType;
import org.hl7.fhir.r4.model.ValueSet;
import org.hl7.fhir.r4.model.codesystems.RestfulSecurityService;
import org.springframework.beans.factory.InitializingBean;

import com.google.common.collect.Streams;

import ca.uhn.fhir.model.api.annotation.ResourceDef;
import ca.uhn.fhir.rest.api.Constants;

public class ConformanceServiceImpl implements ConformanceService, InitializingBean
{
	private final CapabilityStatement capabilityStatement;
	private final ParameterConverter parameterConverter;

	public ConformanceServiceImpl(String serverBase, int defaultPageCount, ParameterConverter parameterConverter)
	{
		capabilityStatement = createCapabilityStatement(serverBase, defaultPageCount);
		this.parameterConverter = parameterConverter;
	}

	@Override
	public String getPath()
	{
		throw new UnsupportedOperationException("implemented by jaxrs service layer");
	}

	@Override
	public void afterPropertiesSet() throws Exception
	{
		Objects.requireNonNull(parameterConverter, "parameterConverter");
	}

	@Override
	public Response getMetadata(String mode, UriInfo uri, HttpHeaders headers)
	{
		return Response.ok(capabilityStatement, parameterConverter.getMediaType(uri, headers)).build();
	}

	private CapabilityStatement createCapabilityStatement(String serverBase, int defaultPageCount)
	{
		CapabilityStatement statement = new CapabilityStatement();
		statement.setStatus(PublicationStatus.ACTIVE);
		statement.setDate(new Date());
		statement.setPublisher("Demo Publisher");
		statement.setKind(CapabilityStatementKind.INSTANCE);
		statement.setSoftware(new CapabilityStatementSoftwareComponent());
		statement.getSoftware().setName("Software Name");
		statement.getSoftware().setVersion("Software Version");
		statement.setImplementation(new CapabilityStatementImplementationComponent());
		statement.getImplementation().setDescription("Implementation Description");
		statement.getImplementation().setUrl(serverBase);
		statement.setFhirVersion(FHIRVersion._4_0_0);
		statement.setFormat(
				Arrays.asList(new CodeType(Constants.CT_FHIR_JSON_NEW), new CodeType(Constants.CT_FHIR_XML_NEW)));
		CapabilityStatementRestComponent rest = statement.addRest();
		rest.setMode(RestfulCapabilityMode.SERVER);
		rest.getSecurity()
				.setService(Collections.singletonList(new CodeableConcept().addCoding(new Coding(
						RestfulSecurityService.CERTIFICATES.getSystem(), RestfulSecurityService.CERTIFICATES.toCode(),
						RestfulSecurityService.CERTIFICATES.getDisplay()))));
		Extension websocketExtension = rest.addExtension();
		websocketExtension.setUrl("http://hl7.org/fhir/StructureDefinition/capabilitystatement-websocket");
		websocketExtension.setValue(new UrlType(serverBase.replace("http", "ws") + ServerEndpoint.PATH));

		var resources = Arrays.asList(CodeSystem.class, Endpoint.class, HealthcareService.class, Location.class,
				Organization.class, Patient.class, PractitionerRole.class, Practitioner.class, Provenance.class,
				ResearchStudy.class, StructureDefinition.class, Subscription.class, Task.class, ValueSet.class);

		var searchParameters = new HashMap<Class<? extends DomainResource>, List<CapabilityStatementRestResourceSearchParamComponent>>();

		var codeSystemIdentifier = createSearchParameter(CodeSystemIdentifier.class);
		var codeSystemUrl = createSearchParameter(CodeSystemUrl.class);
		var codeSystemVersion = createSearchParameter(CodeSystemVersion.class);
		searchParameters.put(CodeSystem.class, Arrays.asList(codeSystemIdentifier, codeSystemUrl, codeSystemVersion));

		var endpointIdentifier = createSearchParameter(EndpointIdentifier.class);
		var endpointName = createSearchParameter(EndpointName.class);
		var endpointOrganization = createSearchParameter(EndpointOrganization.class);
		searchParameters.put(Endpoint.class, Arrays.asList(endpointIdentifier, endpointName, endpointOrganization));

		var healthcareServiceActive = createSearchParameter(HealthcareServiceActive.class);
		var healthcareServiceIdentifier = createSearchParameter(HealthcareServiceIdentifier.class);
		searchParameters.put(HealthcareService.class,
				Arrays.asList(healthcareServiceActive, healthcareServiceIdentifier));

		var locationIdentifier = createSearchParameter(LocationIdentifier.class);
		searchParameters.put(Location.class, Arrays.asList(locationIdentifier));

		var organizationActive = createSearchParameter(OrganizationActive.class);
		var organizationEndpoint = createSearchParameter(OrganizationEndpoint.class);
		var organizationIdentifier = createSearchParameter(OrganizationIdentifier.class);
		var organizationNameOrAlias = createSearchParameter(OrganizationName.class);
		searchParameters.put(Organization.class, Arrays.asList(organizationActive, organizationEndpoint,
				organizationIdentifier, organizationNameOrAlias));

		var patientActive = createSearchParameter(PatientActive.class);
		var patientIdentifier = createSearchParameter(PatientIdentifier.class);
		searchParameters.put(Patient.class, Arrays.asList(patientActive, patientIdentifier));

		var practitionerActive = createSearchParameter(PractitionerActive.class);
		var practitionerIdentifier = createSearchParameter(PractitionerIdentifier.class);
		searchParameters.put(Practitioner.class, Arrays.asList(practitionerActive, practitionerIdentifier));

		var practitionerRoleActive = createSearchParameter(PractitionerRoleActive.class);
		var practitionerRoleIdentifier = createSearchParameter(PractitionerRoleIdentifier.class);
		searchParameters.put(PractitionerRole.class, Arrays.asList(practitionerRoleActive, practitionerRoleIdentifier));

		var researchStudyIdentifier = createSearchParameter(ResearchStudyIdentifier.class);
		searchParameters.put(ResearchStudy.class, Arrays.asList(researchStudyIdentifier));

		var structureDefinitionIdentifier = createSearchParameter(StructureDefinitionIdentifier.class);
		var structureDefinitionUrl = createSearchParameter(StructureDefinitionUrl.class);
		var structureDefinitionVersion = createSearchParameter(StructureDefinitionVersion.class);
		searchParameters.put(StructureDefinition.class,
				Arrays.asList(structureDefinitionIdentifier, structureDefinitionUrl, structureDefinitionVersion));

		var subscriptionCriteria = createSearchParameter(SubscriptionCriteria.class);
		var subscriptionStatus = createSearchParameter(SubscriptionStatus.class);
		var subscriptionChannelPayload = createSearchParameter(SubscriptionChannelPayload.class);
		var subscriptionChannelType = createSearchParameter(SubscriptionChannelType.class);
		searchParameters.put(Subscription.class, Arrays.asList(subscriptionCriteria, subscriptionStatus,
				subscriptionChannelPayload, subscriptionChannelType));

		var taskIdentifier = createSearchParameter(TaskIdentifier.class);
		var taskRequester = createSearchParameter(TaskRequester.class);
		var taskStatus = createSearchParameter(TaskStatus.class);
		searchParameters.put(Task.class, Arrays.asList(taskIdentifier, taskRequester, taskStatus));

		var valueSetIdentifier = createSearchParameter(ValueSetIdentifier.class);
		var valueSetUrl = createSearchParameter(ValueSetUrl.class);
		var valueSetVersion = createSearchParameter(ValueSetVersion.class);
		searchParameters.put(ValueSet.class, Arrays.asList(valueSetIdentifier, valueSetUrl, valueSetVersion));

		var operations = new HashMap<Class<? extends DomainResource>, List<CapabilityStatementRestResourceOperationComponent>>();

		var snapshotOperation = createOperation("snapshot",
				"http://hl7.org/fhir/OperationDefinition/StructureDefinition-snapshot",
				"Generates a StructureDefinition instance with a snapshot, based on a differential in a specified StructureDefinition");
		operations.put(StructureDefinition.class, Arrays.asList(snapshotOperation));

		var standardSortableSearchParameters = Arrays.asList(createSearchParameter(ResourceId.class),
				createSearchParameter(ResourceLastUpdated.class));
		var standardOperations = Arrays.asList(createValidateOperation());

		for (Class<? extends DomainResource> resource : resources)
		{
			CapabilityStatementRestResourceComponent r = rest.addResource();
			r.setVersioning(ResourceVersionPolicy.VERSIONED);
			r.setReadHistory(true);
			r.setUpdateCreate(false);
			r.setConditionalCreate(true);
			r.setConditionalRead(ConditionalReadStatus.FULLSUPPORT);
			r.setConditionalUpdate(true);
			r.setConditionalDelete(ConditionalDeleteStatus.SINGLE);
			r.addReferencePolicy(ReferenceHandlingPolicy.LITERAL); // TODO ReferenceHandlingPolicy.ENFORCED

			r.setType(resource.getAnnotation(ResourceDef.class).name());
			r.setProfile(resource.getAnnotation(ResourceDef.class).profile());
			r.addInteraction().setCode(TypeRestfulInteraction.CREATE);
			r.addInteraction().setCode(TypeRestfulInteraction.READ);
			r.addInteraction().setCode(TypeRestfulInteraction.VREAD);
			r.addInteraction().setCode(TypeRestfulInteraction.UPDATE);
			r.addInteraction().setCode(TypeRestfulInteraction.DELETE);
			r.addInteraction().setCode(TypeRestfulInteraction.SEARCHTYPE);

			standardSortableSearchParameters.stream().forEach(r::addSearchParam);

			var resourceSearchParameters = searchParameters.getOrDefault(resource, Collections.emptyList());
			resourceSearchParameters.stream()
					.sorted(Comparator.comparing(CapabilityStatementRestResourceSearchParamComponent::getName))
					.forEach(r::addSearchParam);

			if (resourceSearchParameters.stream().anyMatch(s -> SearchParamType.REFERENCE.equals(s.getType())))
				r.addSearchParam(createIncludeParameter(resource, resourceSearchParameters));

			r.setSearchInclude(resourceSearchParameters.stream()
					.filter(s -> SearchParamType.REFERENCE.equals(s.getType()))
					.map(s -> new StringType(resource.getAnnotation(ResourceDef.class).name() + ":" + s.getName()))
					.collect(Collectors.toList()));

			r.addSearchParam(createFormatParameter());
			r.addSearchParam(createPrettyParameter());
			r.addSearchParam(createCountParameter(defaultPageCount));
			r.addSearchParam(createPageParameter());
			r.addSearchParam(createSortParameter(standardSortableSearchParameters, resourceSearchParameters));

			operations.getOrDefault(resource, Collections.emptyList()).forEach(r::addOperation);
			standardOperations.forEach(r::addOperation);
		}
		return statement;
	}

	private CapabilityStatementRestResourceSearchParamComponent createIncludeParameter(
			Class<? extends DomainResource> resource,
			List<CapabilityStatementRestResourceSearchParamComponent> resourceSearchParameters)
	{
		return createSearchParameter("_include", "", SearchParamType.SPECIAL,
				"Additional resources to return, allowed values: "
						+ resourceSearchParameters.stream().filter(s -> SearchParamType.REFERENCE.equals(s.getType()))
								.map(s -> resource.getAnnotation(ResourceDef.class).name() + ":" + s.getName())
								.collect(Collectors.joining(", ", "[", "]"))
						+ " (use one _include parameter for every resource to include)");
	}

	private CapabilityStatementRestResourceOperationComponent createValidateOperation()
	{
		return createOperation("validate", "http://hl7.org/fhir/OperationDefinition/Resource-validate",
				"The validate operation checks whether the attached content would be acceptable either generally, as a create, an update or as a delete to an existing resource. The action the server takes depends on the mode parameter");
	}

	private CapabilityStatementRestResourceSearchParamComponent createSortParameter(
			List<CapabilityStatementRestResourceSearchParamComponent> standardSearchParameters,
			List<CapabilityStatementRestResourceSearchParamComponent> resourceSearchParameters)
	{
		return createSearchParameter("_sort", "", SearchParamType.SPECIAL,
				"Specify the returned order, allowed values: "
						+ Streams.concat(standardSearchParameters.stream(), resourceSearchParameters.stream())
								.map(s -> s.getName()).collect(Collectors.joining(", ", "[", "]"))
						+ " (one or multiple as comma separated string), prefix with '-' for reversed order");
	}

	private CapabilityStatementRestResourceSearchParamComponent createPageParameter()
	{
		return createSearchParameter("_page", "", SearchParamType.NUMBER,
				"Specify the page number, 1 if not specified");
	}

	private CapabilityStatementRestResourceSearchParamComponent createCountParameter(int defaultPageCount)
	{
		return createSearchParameter("_count", "", SearchParamType.NUMBER,
				"Specify the numer of returned resources per page, " + defaultPageCount + " if not specified");
	}

	private CapabilityStatementRestResourceSearchParamComponent createFormatParameter()
	{
		String formatValues = Streams
				.concat(Stream.of(ParameterConverter.JSON_FORMAT), ParameterConverter.JSON_FORMATS.stream(),
						Stream.of(ParameterConverter.XML_FORMAT), ParameterConverter.XML_FORMATS.stream())
				.collect(Collectors.joining(", ", "[", "]"));
		CapabilityStatementRestResourceSearchParamComponent createFormatParameter = createSearchParameter("_format", "",
				SearchParamType.STRING,
				"Specify the returned format of the payload response, allowed values: " + formatValues);
		return createFormatParameter;
	}

	private CapabilityStatementRestResourceSearchParamComponent createPrettyParameter()
	{
		CapabilityStatementRestResourceSearchParamComponent createFormatParameter = createSearchParameter("_pretty", "",
				SearchParamType.SPECIAL,
				"Ask for a pretty printed response for human convenience, allowed values: [true, false]");
		return createFormatParameter;
	}

	private CapabilityStatementRestResourceOperationComponent createOperation(String name, String definition,
			String documentation)
	{
		return new CapabilityStatementRestResourceOperationComponent().setName(name).setDefinition(definition)
				.setDocumentation(documentation);
	}

	private CapabilityStatementRestResourceSearchParamComponent createSearchParameter(Class<?> parameter)
	{
		SearchParameterDefinition d = parameter.getAnnotation(SearchParameterDefinition.class);
		return createSearchParameter(d.name(), d.definition(), d.type(), d.documentation());
	}

	private CapabilityStatementRestResourceSearchParamComponent createSearchParameter(String name, String definition,
			SearchParamType type, String documentation)
	{
		return new CapabilityStatementRestResourceSearchParamComponent().setName(name).setDefinition(definition)
				.setType(type).setDocumentation(documentation);
	}
}
