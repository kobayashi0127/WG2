package com.commander4j.db;

public class JDBUpdateRequest
{

	public boolean schema_updateRequired = false;
	public int schema_currentVersion = -1;
	public int schema_requiredVersion = -1;

	public boolean program_updateRequired = false;
	public Double program_currentVersion = new Double("-1");
	public Double program_requiredVersion = new Double("-1");

}
