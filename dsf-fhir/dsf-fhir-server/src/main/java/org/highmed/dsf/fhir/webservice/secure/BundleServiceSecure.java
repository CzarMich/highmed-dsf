package org.highmed.dsf.fhir.webservice.secure;

import org.highmed.dsf.fhir.authorization.BundleAuthorizationRule;
import org.highmed.dsf.fhir.dao.BundleDao;
import org.highmed.dsf.fhir.help.ExceptionHandler;
import org.highmed.dsf.fhir.help.ParameterConverter;
import org.highmed.dsf.fhir.help.ResponseGenerator;
import org.highmed.dsf.fhir.service.ReferenceResolver;
import org.highmed.dsf.fhir.webservice.specification.BundleService;
import org.hl7.fhir.r4.model.Bundle;

public class BundleServiceSecure extends AbstractResourceServiceSecure<BundleDao, Bundle, BundleService>
		implements BundleService
{
	public BundleServiceSecure(BundleService delegate, String serverBase, ResponseGenerator responseGenerator,
			ReferenceResolver referenceResolver, BundleDao bundleDao, ExceptionHandler exceptionHandler,
			ParameterConverter parameterConverter, BundleAuthorizationRule authorizationRule)
	{
		super(delegate, serverBase, responseGenerator, referenceResolver, Bundle.class, bundleDao, exceptionHandler,
				parameterConverter, authorizationRule);
	}
}
