package org.highmed.dsf.fhir.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.ContactDetail;
import org.hl7.fhir.r4.model.ElementDefinition;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.StructureDefinition;
import org.hl7.fhir.r4.model.StructureDefinition.StructureDefinitionContextComponent;
import org.hl7.fhir.r4.model.StructureDefinition.StructureDefinitionMappingComponent;
import org.hl7.fhir.r4.model.UsageContext;

public class SimpleSnapshotGenerator
{
	public StructureDefinition generateSnapshot(StructureDefinition base, StructureDefinition diff)
	{
		Objects.requireNonNull(base, "base");
		Objects.requireNonNull(diff, "diff");
		if (!base.hasUrl())
			throw new IllegalArgumentException("base.url null or empty");
		if (!diff.hasUrl())
			throw new IllegalArgumentException("diff.url null or empty");
		if (!diff.hasBaseDefinition())
			throw new IllegalArgumentException("diff.baseDefinition null or empty");
		if (!base.getUrl().equals(diff.getBaseDefinition()))
			throw new IllegalArgumentException("base.url '" + base.getUrl() + "' not equal to diff.baseDefinition '"
					+ diff.getBaseDefinition() + "'");
		if (!base.hasSnapshot())
			throw new IllegalArgumentException("base.snapshot null or empty");
		if (!diff.hasDifferential())
			throw new IllegalArgumentException("diff.differential null or empty");
		if (!base.hasFhirVersion())
			throw new IllegalArgumentException("base.fhirVersion null or empty");
		if (diff.hasFhirVersion() && !base.getFhirVersion().equals(diff.getFhirVersion()))
			throw new IllegalArgumentException("diff.fhirVersion '" + diff.getFhirVersion()
					+ "' not equal to base.Fhirversion '" + base.getFhirVersion() + "'");

		StructureDefinition snapshot = base.copy();

		snapshot.setUrl(diff.getUrl());
		snapshot.setIdentifier(diff.getIdentifier().stream().map(Identifier::copy).collect(Collectors.toList()));
		setIfInDiffElseKeepBase(diff::hasVersion, diff::getVersion, snapshot::setVersion);
		setIfInDiffElseKeepBase(diff::hasName, diff::getName, snapshot::setName);
		setIfInDiffElseKeepBase(diff::hasTitle, diff::getTitle, snapshot::setTitle);
		setIfInDiffElseKeepBase(diff::hasStatus, diff::getStatus, snapshot::setStatus);
		setIfInDiffElseKeepBase(diff::hasExperimental, diff::getExperimental, snapshot::setExperimental);
		setIfInDiffElseKeepBase(diff::hasDate, diff::getDate, snapshot::setDate);
		setIfInDiffElseKeepBase(diff::hasPublisher, diff::getPublisher, snapshot::setPublisher);
		snapshot.setContact(diff.getContact().stream().map(ContactDetail::copy).collect(Collectors.toList()));
		setIfInDiffElseKeepBase(diff::hasDescription, diff::getDescription, snapshot::setDescription);
		snapshot.setUseContext(diff.getUseContext().stream().map(UsageContext::copy).collect(Collectors.toList()));
		snapshot.setJurisdiction(
				diff.getJurisdiction().stream().map(CodeableConcept::copy).collect(Collectors.toList()));
		setIfInDiffElseKeepBase(diff::hasPurpose, diff::getPurpose, snapshot::setPurpose);
		setIfInDiffElseKeepBase(diff::hasCopyright, diff::getCopyright, snapshot::setCopyright);
		snapshot.setKeyword(diff.getKeyword().stream().map(Coding::copy).collect(Collectors.toList()));
		setIfInDiffElseKeepBase(diff::hasFhirVersion, diff::getFhirVersion, snapshot::setFhirVersion);
		setMappings(snapshot, diff);
		setIfInDiffElseKeepBase(diff::hasKind, diff::getKind, snapshot::setKind);
		setIfInDiffElseKeepBase(diff::hasAbstract, diff::getAbstract, snapshot::setAbstract);
		snapshot.setContext(
				diff.getContext().stream().map(StructureDefinitionContextComponent::copy).collect(Collectors.toList()));
		setIfInDiffElseKeepBase(diff::hasContextInvariant, diff::getContextInvariant, snapshot::setContextInvariant);
		setIfInDiffElseKeepBase(diff::hasType, diff::getType, snapshot::setType);
		setIfInDiffElseKeepBase(diff::hasBaseDefinition, diff::getBaseDefinition, snapshot::setBaseDefinition);
		setIfInDiffElseKeepBase(diff::hasDerivation, diff::getDerivation, snapshot::setDerivation);
		mergeElements(snapshot, diff);
		snapshot.setDifferential(diff.getDifferential().copy());

		return snapshot;
	}

	private void setMappings(StructureDefinition snapshot, StructureDefinition diff)
	{
		Map<String, StructureDefinitionMappingComponent> newMappings = new HashMap<>();
		diff.getMapping().stream().map(StructureDefinitionMappingComponent::copy).forEach(m ->
		{
			if (m.hasUri())
				newMappings.put(uriKey(m), m);
			if (m.hasName())
				newMappings.put(nameKey(m), m);
		});
		snapshot.getMapping().stream().map(StructureDefinitionMappingComponent::copy).forEach(m ->
		{
			if (m.hasUri() && !newMappings.containsKey(uriKey(m)))
				newMappings.put(uriKey(m), m);
			if (m.hasName() && !newMappings.containsKey(nameKey(m)))
				newMappings.put(nameKey(m), m);
		});
		snapshot.setMapping(newMappings.values().stream().distinct().collect(Collectors.toList()));
	}

	private String nameKey(StructureDefinitionMappingComponent m)
	{
		return "name:" + m.getName();
	}

	private String uriKey(StructureDefinitionMappingComponent m)
	{
		return "uri:" + m.getUri();
	}

	private void mergeElements(StructureDefinition snapshot, StructureDefinition diff)
	{
		List<ElementDefinition> elements = diff.getDifferential().getElement().stream()
				.map(e -> mergeElement(e, getElement(e, snapshot))).collect(Collectors.toList());

		snapshot.getSnapshot().setElement(elements);
	}

	private ElementDefinition mergeElement(ElementDefinition diff, ElementDefinition snapshot)
	{
		if (snapshot.equals(diff))
			return diff;

		
		// TODO
		// merge every existing element
		// add non existing elements
		return null;
	}

	private ElementDefinition getElement(ElementDefinition diff, StructureDefinition snapshot)
	{
		return snapshot.getSnapshot().getElement().stream().filter(d -> Objects.equals(diff.getId(), d.getId()))
				.findFirst().orElse(diff);
	}

	private <T> void setIfInDiffElseKeepBase(Supplier<Boolean> hasElement, Supplier<T> getter, Consumer<T> setter)
	{
		if (hasElement.get())
			setter.accept(getter.get());
	}
}
