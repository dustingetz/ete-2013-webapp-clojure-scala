/*
 * Utilities.
 */

define(['libs/assert'],
	function(a)
	{
		// tagging class
		function Util () { }
		var my = new Util();

		/**
		 * Associative map.
		 * @constructor
		 */
		function Map()
		{
			var keys = [];
			var values = [];
			this.put = function(key, value)
			{
				keys.push(key);
				values.push(value);
			}
			this.get = function(key)
			{
				return values[keys.indexOf(key)];
			}
			this.foreach = function(f)
			{
				var m = this;
				keys.forEach(function(k)
				{
					f(k, m.get(k))
				});
			}
		}
		my.Map = Map;
		/**
		 * Represent HTML as a string.
		 * @param h a jquery element or collection.
		 */
		function showHTML(h)
		{
			try
			{
				return $('<div/>').append(h).html();
			}
			catch (e)
			{
				debugger;
				return "Can't show HTML: " + h + "\n" + e;
			}
		}
		my.showHTML = showHTML;
		/**
		 * Represent an object a JSON string.
		 * @param j an object.
		 */
		function showJSON(j)
		{
			try
			{
				return JSON.stringify(j);
			}
			catch (e)
			{
				debugger;
				return "Can't show JSON: " + j + "\n" + e;
			}
		}
		my.showJSON = showJSON;
		// off ya go!
		return my;
	}
);