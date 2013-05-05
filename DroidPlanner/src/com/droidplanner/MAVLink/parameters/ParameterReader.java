package com.droidplanner.MAVLink.parameters;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.droidplanner.helpers.FileManager;

public class ParameterReader implements com.droidplanner.dialogs.OpenFileDialog.FileReader {
	private List<Parameter> parameters;

	public ParameterReader() {
		this.parameters = new ArrayList<Parameter>();
	}

	public boolean openFile(String itemList) {
		if (!FileManager.isExternalStorageAvaliable()) {
			return false;
		}
		try {
			FileInputStream in = new FileInputStream(itemList);
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));

			if (!isParameterFile(reader)) {
				in.close();
				return false;
			}
			parseWaypointLines(reader);

			in.close();

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	private void parseWaypointLines(BufferedReader reader) throws IOException {
		String line;
		parameters.clear();
		while ((line = reader.readLine()) != null) {
			try {
				parseLine(line);
			} catch (Exception e) {
			}
		}
	}

	private void parseLine(String line) throws Exception {
		String[] RowData = splitLine(line);

		String name = RowData[0];
		Double value = Double.valueOf(RowData[1]);

		Parameter.checkParameterName(name);

		parameters.add(new Parameter(name, value));
	}

	private String[] splitLine(String line) throws Exception {
		String[] RowData = line.split(",");
		if (RowData.length != 2) {
			throw new Exception("Invalid Length");
		}
		return RowData;
	}



	private static boolean isParameterFile(BufferedReader reader)
			throws IOException {
		return reader.readLine().contains("#NOTE");
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

	@Override
	public String getPath() {
		return FileManager.getParametersPath();
	}

	@Override
	public String[] getFileList() {
		return FileManager.getParametersFileList();
	}
}
