package gr.highcharts.export;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextAction;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class Main
{
	public static File outputDirectory = new File("");
	public static File jsResourcesDirectory = new File("");
	private static JSONObject DEFAULT_GLOBALS = new JSONObject();
	private static String[] scriptFileNames = { "env.rhino.1.2.35.js", "jquery-1.11.3.js", "highcharts.src.js", "data.src.js", "exporting.src.js", "svg-renderer-highcharts.js", "add-BBox.js", "formatWrapper.js" };

	private static ContextFactory CONTEXT_FACTORY = org.mozilla.javascript.tools.shell.Main.shellContextFactory;//Could use static import

	public static void main(String args[])
	{
		String sampleJsonString = "";
		try
		{
			JSONObject sampleJson = new JSONObject(sampleJsonString);
			storeSvgAsImage(generateChartSvg(sampleJson), "png", "A.png");
			storeSvgAsImage(generateChartSvg(sampleJson), "jpg", "A.jpg");
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}

	public static String generateChartSvg(JSONObject chartOptions, JSONObject globalOptions)
	{
		String svgString = null;

		try
		{
			Object svg = CONTEXT_FACTORY.call(new ContextAction()
			{
				@Override
				public Object run(Context cx)
				{
					Scriptable scriptable = cx.initStandardObjects();
					List<Script> scripts = new ArrayList<Script>();
					cx.setGeneratingDebug(true);
					cx.setLanguageVersion(Context.VERSION_ES6);
					cx.setOptimizationLevel(-1);

					for (String scriptFileName : scriptFileNames)
					{
						addScriptToList(cx, scripts, scriptFileName);
					}

					for (Script script : scripts)
					{
						script.exec(cx, scriptable);
					}
					return ScriptableObject.callMethod(null, scriptable, "renderSVGFromJson", new Object[] { '(' + globalOptions.toString() + ')', '(' + chartOptions.toString() + ')' });
				}
			});
			svgString = svg.toString();
			svgString = svgString.replace("clippath", "clipPath");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return svgString;
	}

	protected static void addScriptToList(Context cx, List<Script> scripts, String jsFileName)
	{
		File jsFile = new File(jsResourcesDirectory, jsFileName);
		addScriptToList(cx, scripts, jsFile);
	}

	protected static void addScriptToList(Context cx, List<Script> scripts, File jsFile)
	{
		InputStream in = null;
		InputStreamReader reader = null;
		try
		{
			in = new FileInputStream(jsFile);
			reader = new InputStreamReader(in);
			scripts.add(cx.compileReader(reader, jsFile.getName(), 1, null));
		}
		catch (IOException e)
		{
			throw new RuntimeException("Cannot load js file : " + jsFile.getAbsolutePath(), e);
		}
		finally
		{
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(reader);
		}
	}

	public static String generateChartSvg(JSONObject chartOptions)
	{
		return generateChartSvg(chartOptions, DEFAULT_GLOBALS);
	}

	public static boolean storeSvgAsImage(String svgString, String imageType, File outputFile)
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
		return true;
	}

	public static boolean storeSvgAsImage(String svgString, String imageType, String outputFileName)
	{
		return storeSvgAsImage(svgString, imageType, new File(outputDirectory, outputFileName));
	}
}
