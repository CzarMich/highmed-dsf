package org.highmed.fhir.webservice.secure;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.highmed.fhir.webservice.specification.BasicService;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Parameters;

public class AbstractServiceSecure<R extends DomainResource, S extends BasicService<R>> implements BasicService<R>
{
	protected final S delegate;

	public AbstractServiceSecure(S delegate)
	{
		this.delegate = delegate;
	}

	public Response create(R resource, UriInfo uri)
	{
		return delegate.create(resource, uri);
	}

	public Response read(String id, String format, UriInfo uri)
	{
		return delegate.read(id, format, uri);
	}

	public Response vread(String id, long version, String format, UriInfo uri)
	{
		return delegate.vread(id, version, format, uri);
	}

	public Response update(String id, R resource, UriInfo uri)
	{
		return delegate.update(id, resource, uri);
	}

	public Response delete(String id, UriInfo uri)
	{
		return delegate.delete(id, uri);
	}

	public Response search(UriInfo uri)
	{
		return delegate.search(uri);
	}

	public Response validateNew(String validate, Parameters parameters, UriInfo uri)
	{
		return delegate.validateNew(validate, parameters, uri);
	}

	public Response validateExisting(String validate, Parameters parameters, UriInfo uri)
	{
		return delegate.validateExisting(validate, parameters, uri);
	}
}
