/**
 * Defines a dropdown list based on kendo.
 * NB: This widget needs no HTML file as kendo controls the rendering.
 */
define(
	[ 'libs/assert'
	, 'libs/util'
	, 'libs/templates'
	],
function(a, u, t)
{
	'use strict';
	var my = {};
	var SELECTED = 'selected'; // the name of the selected event.
	/**
	 * Create the widget.
	 * The purpose of the data functions is to allow containers to adapt their native data to this widget's 'schema'.
	 * @param target the target html where this instance will be rendered.
	 * @param id the target element in the target html where this instance will be rendered.
	 * @param label the widget's label when nothing has been selected.
	 */
	my.make = function(target, id, label)
	{
		var w = {};
		var root = t.snippet(target, id);
		var list = root.kendoComboBox(
			{ dataTextField : "name"
			, dataValueField : "id"
			, select : function (e)
			{
				alert(u.showHTML(e.item));
				var name = e.item.text();
				var id = list.data('kendoComboBox').value();
				selection = {'id':id,'name':name};
				events.trigger(SELECTED, selection);
			}
		});
		a.assertValue(label, "label required.");
		// the selected {id,name}
		var selection = null;
		// container for pub/sub on this widget.
		// events is hidden (as it's an implementation detail).
		var events = _.extend({}, Backbone.Events);
		// mnemonic function name here.
		w.onSelectChange = function(fn)
		{
			events.on(SELECTED, function ()
			{
				fn(selection);
			});
		};
		/**
		 * Clears the button.
		 */
		function reset()
		{
			selection = null;
			return w;
		}
		/**
		 * Populates the control with data.
		 * @param data the data required by the widget to render itself.
		 */
		w.update = function(data)
		{
			reset();
			root.data("kendoComboBox").setDataSource(data);
			return this;
		};
		return w;
	};
	return my;
});