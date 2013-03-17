/**
 * A collection of utilities for working with templates.
 * Mostly, this involves functions for dealing with data attributes.
 * Depends: backbone.
 */
define(['libs/assert', 'libs/util'],
	function(a, u)
	{
		'use strict';
		var my = {};
		/**
		 * Recursively finds and removes comment nodes from HTML in situ.
		 * @param h a jquery object.
		 */
		function stripComments(h)
		{
			// for some reason, a recursive each/remove destroyed the source html.
			// invoking filter first (which does not recur) and then recurring seems to do the trick.
			h.contents().filter(function() { return this.nodeType == 8; }).remove();
			h.contents().each(function(i,n) { stripComments($(n)); });
			return h; // fluently
		}
		my.stripComments = stripComments;
		/**
		 * Searches for data attributes by name and value, i.e. data-[name]="[value]".
		 * Also removes the attribute from the found node(s).
		 */
		function findData(html, name, value)
		{
			a.assertValue(html, "no html for findData.");
			a.assert('' === value || value, "no value for findData.");
			var attr = "data-" + name;
			return $(html.find("[" + attr + "='" + value + "']")).removeAttr(attr);
		}
		my.findData = findData;
		/**
		 * Searches for tagged attributes in the page dom.  Fails if a single, unique element is not found.
		 * @param html the html to search.
		 * @param value The value of the data-snippet attribute.
		 */
		function snippet(html, value)
		{
			var q = findData(html, "snippet", value);
			try
			{
				a.assert(1 === q.size(), "Expected one target at '" + value + "' but found " + q.size() + " in current DOM.");
			}
			catch (e)
			{
				alert(u.showHTML(html));
				throw e;
			}
			return q;
		}
		my.snippet = snippet;
		/**
		 * This encapsulates a backbone events object with functions for listening to its events and publishing.
		 * @constructor
		 */
		function Events()
		{
			/**
			 * This is available publicly so we don't have to account for the more complex interactions that can involve this object.
			 * @type {*}
			 */
			var events = _.extend({}, Backbone.Events);
			/**
			 * Maps events on a component to triggers on the events object.
			 * @param component The component to which to bind the event handler.
			 * @param eventName
			 * @param triggerName the name of the event bound on the events object.
			 * @param dataf function producing an array of data to be supplied to trigger. may be null.
			 * @return {*}
			 */
			this.onEvent = function(component,eventName,triggerName, dataf)
			{
				return component.on(eventName,function()
				{
					events.trigger(triggerName, dataf && dataf());
				});
			};
			/**
			 * Maps click events on a component to triggers on the events object.
			 * This is just a shorthand for onEvent that handles clicks, specifically.
			 * @param component The component to which to bind the event handler.
			 * @param dodef if false call preventDefault on the click event.
			 * @param triggerName the name of the event bound on the events object.
			 * @param dataf function producing an array of data to be supplied to trigger. may be null.
			 * @return {*}
			 */
			this.onClick = function(component,dodef,triggerName,dataf)
			{
				return component.click(function(event)
				{
					if (! dodef) event.preventDefault();
					events.trigger(triggerName, dataf && dataf());
				});
			};
			/**
			 * Provides a function to hook a listener up to a trigger on the events object.
			 * Components will use this to provide an API for containers to hook into their events.
			 * @param eventName The name of the event (defined in the widget)
			 * @return {Function}
			 */
			this.publish = function(eventName)
			{
				return function(f) { events.on(eventName,f) };
			};
		}
		my.Events = Events;
		/**
		 * Extends an object with backbone events.
		 */
		function events (o)
		{
			return _.extend(o, Backbone.Events);
		}
		my.events = events;
		// off ya go!
		return my;
	}
);