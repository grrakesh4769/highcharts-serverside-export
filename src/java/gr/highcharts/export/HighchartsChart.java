package gr.highcharts.export;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.StringReader;

import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.json.JSONException;
import org.json.JSONObject;

public class HighchartsChart
{
	private static volatile File outputDirectory = new File("D:\\HSE_ExportDirectory");

	public static File getOutputDirectory()
	{
		return outputDirectory;
	}

	public static void setOutputDirectory(File outputDirectory)
	{
		HighchartsChart.outputDirectory = outputDirectory;
	}

	private String svgString = null;
	private JSONObject chartOptions = null;

	public HighchartsChart(String optionsJsonString) throws JSONException
	{
		this(new JSONObject(optionsJsonString));
	}

	public HighchartsChart(JSONObject optionsJson)
	{
		chartOptions = optionsJson;
		updateSVG();
	}

	public String updateSVG()
	{
		svgString = HighchartsSVGGenerator.getInstance().generateChartSvg(chartOptions);
		return svgString;
	}

	public boolean storeAsImage(File outputFile, String imageType)
	{
		Transcoder transcoder = null;
		if (imageType.equalsIgnoreCase("jpg") || imageType.equalsIgnoreCase("jpeg"))
		{
			transcoder = new JPEGTranscoder();
		}
		else if (imageType.equalsIgnoreCase("png"))
		{
			transcoder = new PNGTranscoder();
		}
		else
		{
			throw new IllegalArgumentException("Unsupported Image Type");
		}

		StringReader svgStringReader = new StringReader(svgString);
		TranscoderInput transcoderInput = new TranscoderInput(svgStringReader);

		try
		{
			FileOutputStream fos = new FileOutputStream(outputFile);
			TranscoderOutput transcoderOutput = new TranscoderOutput(fos);
			transcoder.transcode(transcoderInput, transcoderOutput);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			return false;
		}
		catch (TranscoderException e)
		{
			e.printStackTrace();
			return false;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean storeAsImage(String outputFileName, String imageType)
	{
		return storeAsImage(new File(outputDirectory, outputFileName), imageType);
	}
}
