package org.highmed.dsf.fhir.search.parameters;

import static org.highmed.dsf.fhir.search.parameters.OrganizationType.PARAMETER_NAME;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import org.highmed.dsf.fhir.function.BiFunctionWithSqlException;
import org.highmed.dsf.fhir.search.SearchQueryParameter.SearchParameterDefinition;
import org.highmed.dsf.fhir.search.parameters.basic.AbstractTokenParameter;
import org.highmed.dsf.fhir.search.parameters.basic.TokenValueAndSearchType;
import org.hl7.fhir.r4.model.Enumerations.SearchParamType;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Resource;

@SearchParameterDefinition(name = PARAMETER_NAME, definition = "http://hl7.org/fhir/SearchParameter/Organization-type", type = SearchParamType.TOKEN, documentation = "A code for the type of organization")
public class OrganizationType extends AbstractTokenParameter<Organization>
{
	public static final String PARAMETER_NAME = "type";
	public static final String RESOURCE_COLUMN = "organization";

	public OrganizationType()
	{
		super(PARAMETER_NAME);
	}

	@Override
	public String getFilterQuery()
	{
		switch (valueAndType.type)
		{
			case CODE:
			case CODE_AND_SYSTEM:
			case SYSTEM:
				return "(SELECT jsonb_agg(coding) FROM jsonb_array_elements(" + RESOURCE_COLUMN
						+ "->'type') AS type, jsonb_array_elements(type->'coding') AS coding) @> ?::jsonb";
			case CODE_AND_NO_SYSTEM_PROPERTY:
				return "(SELECT COUNT(*) FROM jsonb_array_elements(" + RESOURCE_COLUMN
						+ "->'type') AS type, jsonb_array_elements(type->'coding') AS coding "
						+ "WHERE coding->>'code' = ? AND NOT (coding ?? 'system')) > 0";
			default:
				return "";
		}
	}

	@Override
	public int getSqlParameterCount()
	{
		return 1;
	}

	@Override
	public void modifyStatement(int parameterIndex, int subqueryParameterIndex, PreparedStatement statement,
			BiFunctionWithSqlException<String, Object[], Array> arrayCreator) throws SQLException
	{
		switch (valueAndType.type)
		{
			case CODE:
				statement.setString(parameterIndex, "[{\"code\": \"" + valueAndType.codeValue + "\"}]");
				return;
			case CODE_AND_SYSTEM:
				statement.setString(parameterIndex, "[{\"code\": \"" + valueAndType.codeValue + "\", \"system\": \""
						+ valueAndType.systemValue + "\"}]");
				return;
			case CODE_AND_NO_SYSTEM_PROPERTY:
				statement.setString(parameterIndex, valueAndType.codeValue);
				return;
			case SYSTEM:
				statement.setString(parameterIndex, "[{\"system\": \"" + valueAndType.systemValue + "\"}]");
				return;
		}
	}

	protected boolean codingMatches(List<Identifier> identifiers)
	{
		return identifiers.stream().anyMatch(i -> codingMatches(valueAndType, i));
	}

	public static boolean codingMatches(TokenValueAndSearchType valueAndType, Identifier identifier)
	{
		switch (valueAndType.type)
		{
			case CODE:
				return Objects.equals(valueAndType.codeValue, identifier.getValue());
			case CODE_AND_SYSTEM:
				return Objects.equals(valueAndType.codeValue, identifier.getValue())
						&& Objects.equals(valueAndType.systemValue, identifier.getSystem());
			case CODE_AND_NO_SYSTEM_PROPERTY:
				return Objects.equals(valueAndType.codeValue, identifier.getValue())
						&& (identifier.getSystem() == null || identifier.getSystem().isBlank());
			case SYSTEM:
				return Objects.equals(valueAndType.systemValue, identifier.getSystem());
			default:
				return false;
		}
	}

	@Override
	protected String getSortSql(String sortDirectionWithSpacePrefix)
	{
		return "(SELECT string_agg((coding->>'system')::text || (coding->>'code')::text, ' ') FROM jsonb_array_elements("
				+ RESOURCE_COLUMN + "->'type'-->'coding') coding)" + sortDirectionWithSpacePrefix;
	}

	@Override
	public boolean matches(Resource resource)
	{
		if (!isDefined())
			throw notDefined();

		if (!(resource instanceof Organization))
			return false;

		Organization o = (Organization) resource;

		return codingMatches(o.getIdentifier());
	}
}
