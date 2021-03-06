package org.highmed.dsf.fhir.spring.config;

import org.highmed.dsf.fhir.service.ReferenceExtractorImpl;
import org.highmed.dsf.fhir.service.ReferenceResolver;
import org.highmed.dsf.fhir.service.ReferenceResolverImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReferenceConfig
{
	@Value("${org.highmed.dsf.fhir.serverBase}")
	private String serverBase;

	@Autowired
	private HelperConfig helperConfig;

	@Autowired
	private DaoConfig daoConfig;

	@Autowired
	private ClientConfig clientConfig;

	@Bean
	public ReferenceExtractorImpl referenceExtractor()
	{
		return new ReferenceExtractorImpl();
	}

	@Bean
	public ReferenceResolver referenceResolver()
	{
		return new ReferenceResolverImpl(serverBase, daoConfig.daoProvider(), helperConfig.responseGenerator(),
				helperConfig.exceptionHandler(), clientConfig.clientProvider(), helperConfig.parameterConverter());
	}
}
