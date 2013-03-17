/**
 * Skills picker.
 */
define([ 'libs/assert', 'libs/util', 'libs/templates'
	, 'text!widgets/skills.html'
	, 'widgets/skills-picker'
],
function(a, u, t, html, spicker)
{
	'use strict';
	var my = {};
	var template = t.stripComments(t.snippet($(html), 'skills'));
	// event names
	var UPDATE = 'UPDATE';
	/**
	 * Create the widget.
	 * @param target where to put the html.
	 * @param label the widget's label when nothing has been selected.
	 * @param cols the number of columns to use for the checkboxes.
	 */
	my.make = function(target, label, cols)
	{
		var w = {};
		// this name very deliberately shadows the html name in the outer scope.
		var html = template.clone();
		var title = t.snippet(html, 'title');
		var skillsPicker = spicker.make(t.snippet(html, 'picker', cols));
		var update = t.snippet(html, 'update');
		var events = new t.events({});
		a.assertValue(label, 'label required.');
		title.text(label);
		target.replaceWith(html);
		w.onUpdate = function(f)
		{
			events.on(UPDATE, f);
		};
		update.click(function()
		{
			// wrap in object so we have just one parameter to the receiver.
			events.trigger(UPDATE, {'ids': skillsPicker.selectedItems()});
		});
		w.addItems = function(items)
		{
			skillsPicker.addItems(items);
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