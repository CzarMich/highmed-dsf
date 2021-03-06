package org.highmed.dsf.fhir.variables;

import java.io.IOException;
import java.util.Objects;

import org.camunda.bpm.engine.impl.variable.serializer.PrimitiveValueSerializer;
import org.camunda.bpm.engine.impl.variable.serializer.ValueFields;
import org.camunda.bpm.engine.variable.impl.value.UntypedValueImpl;
import org.highmed.dsf.fhir.variables.FeasibilityQueryResultsValues.FeasibilityQueryResultsValue;
import org.springframework.beans.factory.InitializingBean;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FeasibilityQueryResultsSerializer extends PrimitiveValueSerializer<FeasibilityQueryResultsValue>
		implements InitializingBean
{
	private final ObjectMapper objectMapper;

	public FeasibilityQueryResultsSerializer(ObjectMapper objectMapper)
	{
		super(FeasibilityQueryResultsValues.VALUE_TYPE);

		this.objectMapper = objectMapper;
	}

	@Override
	public void afterPropertiesSet() throws Exception
	{
		Objects.requireNonNull(objectMapper, "objectMapper");
	}

	@Override
	public void writeValue(FeasibilityQueryResultsValue value, ValueFields valueFields)
	{
		FeasibilityQueryResults targets = value.getValue();
		try
		{
			if (targets != null)
				valueFields.setByteArrayValue(objectMapper.writeValueAsBytes(targets));
		}
		catch (JsonProcessingException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public FeasibilityQueryResultsValue convertToTypedValue(UntypedValueImpl untypedValue)
	{
		return FeasibilityQueryResultsValues.create((FeasibilityQueryResults) untypedValue.getValue());
	}

	@Override
	public FeasibilityQueryResultsValue readValue(ValueFields valueFields)
	{
		byte[] bytes = valueFields.getByteArrayValue();

		try
		{
			FeasibilityQueryResults targets = (bytes == null || bytes.length <= 0) ? null
					: objectMapper.readValue(bytes, FeasibilityQueryResults.class);
			return FeasibilityQueryResultsValues.create(targets);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}
