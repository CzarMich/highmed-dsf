package org.highmed.dsf.fhir.authorization;

import java.sql.Connection;
import java.util.Optional;

import org.highmed.dsf.fhir.authentication.OrganizationProvider;
import org.highmed.dsf.fhir.authentication.User;
import org.highmed.dsf.fhir.dao.GroupDao;
import org.highmed.dsf.fhir.dao.provider.DaoProvider;
import org.highmed.dsf.fhir.service.ReferenceResolver;
import org.hl7.fhir.r4.model.Group;

public class GroupAuthorizationRule extends AbstractAuthorizationRule<Group, GroupDao>
{
	public GroupAuthorizationRule(DaoProvider daoProvider, String serverBase, ReferenceResolver referenceResolver,
			OrganizationProvider organizationProvider)
	{
		super(Group.class, daoProvider, serverBase, referenceResolver, organizationProvider);
	}

	@Override
	public Optional<String> reasonCreateAllowed(Connection connection, User user, Group newResource)
	{
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public Optional<String> reasonReadAllowed(Connection connection, User user, Group existingResource)
	{
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public Optional<String> reasonUpdateAllowed(Connection connection, User user, Group oldResource, Group newResource)
	{
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public Optional<String> reasonDeleteAllowed(Connection connection, User user, Group oldResource)
	{
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public Optional<String> reasonSearchAllowed(Connection connection, User user)
	{
		// TODO Auto-generated method stub
		return Optional.empty();
	}
}
