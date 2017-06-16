function renderSVGFromObject (jsonGeneralOptions,jsonOptions) {
	n = Highcharts.createElement('div', null, null, null, true);

	Highcharts.setOptions (eval (jsonGeneralOptions));
	
	var chartOptions = jsonOptions;
	new FormatWrapper ().visitObject(chartOptions);

	var chart = new Highcharts.Chart(n,chartOptions);
	svg = chart.getSVG ();
	chart.destroy ();
	Highcharts.discardElement (n);
	return svg;
}
function renderSVGFromJson (jsonGeneralOptions, jsonChartOptions) {
	return renderSVGFromObject (jsonGeneralOptions, eval (jsonChartOptions));
}
