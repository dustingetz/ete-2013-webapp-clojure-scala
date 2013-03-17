/**
 * Checkboxes for skills picking.
 */
define([ 'libs/assert', 'libs/util', 'libs/templates'
	, 'text!widgets/skills-picker.html'
],
function(a, u, t, html)
{
	'use strict';
	var my = {};
	var template = t.stripComments(t.snippet($(html), 'groups'));
	// event names
	var UPDATE = 'UPDATE';
	/**
	 * Create the widget.
	 * @param target where to put the html.
	 * @param cols the number of columns to use for the checkboxes.
	 */
	my.make = function(target, cols)
	{
		var w = {};
		// this name very deliberately shadows the html name in the outer scope.
		var html = template.clone();
		var controlGroup = t.snippet(html, 'controls');
		var checkbox = t.snippet(html, 'item');
		var events = new t.events({});
		cols = cols ? cols : 3; // default value.
		a.assert(0 < cols, "Invalid number of columns for skills display: "+cols+" (must be a positive number).");
		target.replaceWith(html);
		w.onUpdate = function(f)
		{
			events.on(UPDATE, f);
		};
		w.selectedItems = function()
		{
			var ids = [];
			// this must find all the checkboxes regardless of how they're laid out.
			var checkboxes = html.find('input[type="checkbox"]');
			checkboxes.each(function(index, checkbox)
			{
				if (checkbox.checked)
				{
					// per crockford: always give the radix to protect against leading zeros.
					ids.push(parseInt(checkbox.value, 10));
				}
			});
			return ids;
		};
		w.addItems = function(items)
		{
			var controlGroups = [];
			controlGroup.empty();
			html.empty();
			for (var q = 0; q < cols; ++ q)
			{
				var g = controlGroup.clone();
				controlGroups.push(g);
				html.append(g);
			}
			var nth = 0;
			_.each(items, function(item)
			{
				var i = checkbox.clone();
				var l = t.snippet(i, 'label');
				l.text(item.name);
				var c = t.snippet(i, 'checkbox');
				c.attr('value', item.id);
				c.prop('checked', item.enabled);
				// alternate the checkboxes between the two control groups.
				controlGroups[nth % controlGroups.length].append(i);
				++ nth;
			});
		};
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