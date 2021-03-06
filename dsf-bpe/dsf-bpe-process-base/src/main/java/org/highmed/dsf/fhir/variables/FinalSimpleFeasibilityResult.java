package org.highmed.dsf.fhir.variables;

public class FinalSimpleFeasibilityResult
{
	private final String cohortId;
	private final long participatingMedics;
	private final long cohortSize;

	public FinalSimpleFeasibilityResult(String cohortId, long participatingMedics, long cohortSize)
	{
		this.cohortId = cohortId;
		this.participatingMedics = participatingMedics;
		this.cohortSize = cohortSize;
	}

	public String getCohortId()
	{
		return cohortId;
	}

	public long getParticipatingMedics()
	{
		return participatingMedics;
	}

	public long getCohortSize()
	{
		return cohortSize;
	}
}