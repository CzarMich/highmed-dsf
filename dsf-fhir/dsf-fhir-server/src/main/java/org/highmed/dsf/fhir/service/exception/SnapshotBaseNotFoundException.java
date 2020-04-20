package org.highmed.dsf.fhir.service.exception;

public class SnapshotBaseNotFoundException extends Exception
{
	private static final long serialVersionUID = 1L;

	private final String url;

	public SnapshotBaseNotFoundException(String url)
	{
		this.url = url;
	}

	public String getUrl()
	{
		return url;
	}
}
