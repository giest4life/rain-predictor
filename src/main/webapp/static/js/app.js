"use strict";

(function() {

	const BASE_URI = window.location.pathname

	const CITIES_SEARCH_URI = BASE_URI + "api/cities";
	const RAIN_PREDICTION_URI = BASE_URI + "api/predict";
	const DEFAULT_INPUT_VALUE = "New York, New York";
	const DEFAULT_TYPEAHEAD_OBJECT = {
		canonicalName : DEFAULT_INPUT_VALUE
	};

	$(".typeahead").typeahead({
		hint : true,
		highlight : true,
		minLength : 1,

	}, {
		source : function(query, syncResults, asyncResults) {
			if (query.trim().length === 0) {
				return;
			}
			$.get(CITIES_SEARCH_URI, {
				query : query
			}, asyncResults);

		},
		async : true,
		limit : 10,
		display : "canonicalName",
		templates : {
			notFound : "<div>No results found</div>"
		}
	}).typeahead("val", DEFAULT_INPUT_VALUE);

	$(".typeahead").bind("typeahead:select", function(ev, suggestion) {

		getRainPrediction(suggestion.canonicalName).then(showHourlyData);
	}).trigger("typeahead:select", DEFAULT_TYPEAHEAD_OBJECT);

	$(".typeahead").bind("typeahead:autocompleted", function(ev, suggestion) {
		getRainPrediction(suggestion.canonicalName).then(showHourlyData);
		$(this).typeahead('close');
	});

	function showHourlyData(data) {

		const hourlyData = data.hourly.data.slice(1);
		hourlyData.unshift(data.currently);

		const table = $("#probTable")

		hourlyData.map(function(current, index) {

			const row = $("<tr></tr>");
			row.appendTo(table);

			const TD_HTML = "<td></td>";

			const timeCell = $(TD_HTML);
			if (index === 0) {
				timeCell.text("Now");
			} else {
				timeCell.text(new Date(current.time * 1000).toLocaleString());
			}
			timeCell.appendTo(row);

			const summaryCell = $(TD_HTML);
			summaryCell.text(current.summary);
			summaryCell.appendTo(row);

			const probCell = $(TD_HTML);
			probCell.text(current.precipProbability);
			probCell.appendTo(row);

		});

	}

	function getRainPrediction(location) {

		const loadingDiv = $(".loading");
		loadingDiv.css("display", "");

		return $.get(RAIN_PREDICTION_URI, {
			city : location
		}, function(data) {
			loadingDiv.css("display", "none");
			
			const summaryCells = $("#summaryTable tbody tr:first td");
			summaryCells.eq(0).text(data.hourly.summary);
			summaryCells.eq(1).text(data.currently.summary);

		});

	}

})();