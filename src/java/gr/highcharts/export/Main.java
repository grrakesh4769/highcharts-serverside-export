package gr.highcharts.export;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

public class Main
{
	public static void main(String args[])
	{
		HighchartsChart.setOutputDirectory(new File("D:\\HSE_ExportDirectory"));
		HighchartsSVGGenerator.setJsResourcesDirectory(new File("D:\\HSE_resources\\highcharts_4_2_4"));

		String sampleJsonString = "{\"yAxis\":{\"title\":{\"text\":\"License count\",\"align\":\"middle\"}},\"title\":{\"text\":\"\"},\"legend\":{\"maxHeight\":40},\"data\":{\"columns\":[[\"COLUMN_DISPLAY_NAME\",\"Mobile Device Management\",\"Yammer Enterprise\",\"Azure Rights Management(RMS)\",\"Office Professional Plus\",\"Skype for Business Online\",\"Office Online\",\"SharePoint Online\",\"Sway\",\"Exchange Online Plan 2\"],[\"Count\",18,13,14,15,14,16,17,13,16]]},\"credits\":{\"enabled\":false},\"chart\":{\"type\":\"column\"},\"exporting\":{\"enabled\":false},\"xAxis\":{\"type\":\"category\",\"labels\":{\"useHTML\":true,\"style\":{\"whiteSpace\":\"nowrap\",\"textOverflow\":\"ellipsis\"}}},\"IS_REPORT_RUNNING\":false,\"tooltip\":{\"useHTML\":true,\"headerFormat\":\"<span style=\\\"display:inline-block;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;max-width:35em;font-size: 10px\\\">{point.key}</span><br>\"}}";
		try
		{
			JSONObject sampleJson = new JSONObject(sampleJsonString);
			HighchartsChart c1 = new HighchartsChart(sampleJson);
			c1.storeAsImage("testB.png", "png");
			c1.storeAsImage("testB.jpeg", "jpg");
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}
}
