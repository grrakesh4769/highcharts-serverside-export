package gr.highcharts.export;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextAction;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class HighchartsSVGGenerator
{
	private volatile static HighchartsSVGGenerator svgGenerator;

	private static String[] scriptFileNames = { "env.rhino.1.2.35.js", "jquery-1.11.3.js", "highcharts.src.js", "data.src.js", "exporting.src.js", "svg-renderer-highcharts.js", "add-BBox.js", "formatWrapper.js" };
	private static File jsResourcesDirectory = new File("D:\\HSE_resources\\highcharts_4_2_4");
	private static ContextFactory CONTEXT_FACTORY = org.mozilla.javascript.tools.shell.Main.shellContextFactory;//Could use static import

	public static File getJsResourcesDirectory()
	{
		return jsResourcesDirectory;
	}

	public static void setJsResourcesDirectory(File jsResourcesDirectory)
	{
		HighchartsSVGGenerator.jsResourcesDirectory = jsResourcesDirectory;
	}

	private static JSONObject DEFAULT_GLOBALS = new JSONObject();

	private Scriptable scriptable;
	private List<Script> scripts;
	private Context cx;

	public static HighchartsSVGGenerator getInstance()
	{
		HighchartsSVGGenerator svgGen = svgGenerator;
		if (svgGen == null)
		{
			synchronized (HighchartsSVGGenerator.class)
			{
				svgGen = svgGenerator;
				if (svgGen == null)
				{
					svgGenerator = svgGen = new HighchartsSVGGenerator();
				}
			}
		}
		return svgGen;
	}

	private HighchartsSVGGenerator()
	{
		CONTEXT_FACTORY.call(new ContextAction()
		{
			@Override
			public Object run(Context context)
			{
				cx = context;
				scriptable = cx.initStandardObjects();
				scripts = new ArrayList<Script>();
				cx.setGeneratingDebug(true);
				cx.setLanguageVersion(Context.VERSION_ES6);
				cx.setOptimizationLevel(-1);

				for (String scriptFileName : scriptFileNames)
				{
					addScriptToList(scriptFileName);
				}

				for (Script script : scripts)
				{
					script.exec(cx, scriptable);
				}
				return null;
			}
		});
	}

	public String generateChartSvg(JSONObject chartOptions, JSONObject globalOptions)
	{
		String svgString = null;
		try
		{
			Object svg = CONTEXT_FACTORY.call(new ContextAction()
			{
				@Override
				public Object run(Context context)
				{
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

	public String generateChartSvg(JSONObject chartOptions)
	{
		return generateChartSvg(chartOptions, DEFAULT_GLOBALS);
	}

	protected void addScriptToList(String jsFileName)
	{
		File jsFile = new File(jsResourcesDirectory, jsFileName);
		addScriptToList(jsFile);
	}

	protected void addScriptToList(File jsFile)
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
}
