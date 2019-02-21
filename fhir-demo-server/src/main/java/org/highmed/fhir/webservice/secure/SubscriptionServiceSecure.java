package org.highmed.fhir.webservice.secure;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.highmed.fhir.webservice.specification.SubscriptionService;
import org.hl7.fhir.r4.model.Subscription;

public class SubscriptionServiceSecure extends AbstractServiceSecure<Subscription, SubscriptionService>
		implements SubscriptionService
{
	public SubscriptionServiceSecure(SubscriptionService delegate)
	{
		super(delegate);
	}
	
	@Override
	public Response create(Subscription resource, UriInfo uri)
	{
		//check subscription.channel.payload null or one of the supported mimetypes
		//check subscription.channel.type = websocket
		//check subscription.criteria is implemented as search query
		//check if subscription.channel.type = websocket, Task unique on subscription.criteria
		
		// TODO Auto-generated method stub
		return super.create(resource, uri);
	}
	
	@Override
	public Response update(String id, Subscription resource, UriInfo uri)
	{
		//see create
		
		// TODO Auto-generated method stub
		return super.update(id, resource, uri);
	}
}
