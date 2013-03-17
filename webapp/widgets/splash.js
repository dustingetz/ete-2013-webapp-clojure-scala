/**
 * Displays the home default text.
 */
define([ 'libs/assert', 'libs/util', 'libs/templates'
	, 'text!widgets/splash.html'
],
	function(a, u, t, html)
	{
		'use strict';
		var my = {};
		var template = t.stripComments(t.snippet($(html), 'splash'));
		/**
		 * Create the widget.
		 * @param target where to put the html.
		 */
		my.make = function(target)
		{
			var w = {};
			// this name very deliberately shadows the html name in the outer scope.
			var html = template.clone();
			target.replaceWith(html);
			w.show = function()
			{
				html.show();
			};
			w.hide = function()
			{
				html.hide();
			};
			return w;
		};
		return my;
	});