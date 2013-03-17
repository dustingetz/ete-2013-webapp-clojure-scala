/**
 * Defines a dropdown list based on a bootstrap button that changes color when selected.
 */
define([ 'libs/assert', 'libs/util', 'libs/templates'
    , 'text!widgets/bootstrap.list.html'
    ],
function(a, u, t, html)
{
	'use strict';
	var my = {};
	var template = t.stripComments(t.snippet($(html), 'dropdown'));
	// the name of the selected event.
	var SELECTED = 'selected';
	/**
	 * Create the widget.
	 * @param target where to put the html.
	 * @param label the widget's label when nothing has been selected.
	 */
	my.make = function(target, label)
	{
		var w = {};
		// this name very deliberately shadows the html name in the outer scope.
		var html = template.clone();
		target.replaceWith(html);
		var button = t.snippet(html, 'button');
		var caption = t.snippet(html, 'caption');
		var ul = t.snippet(html, 'ul');
		var li = t.snippet(html, 'li');
		a.assertValue(label, 'label required.');
		// the selected {id,name}
		var selection = null;
		// container for pub/sub on this widget.
		// events is hidden (as it's an implementation detail).
		var events = t.events({});
		// subscribe to changes in the selection.
		w.onSelectChange = function(f)
		{
			events.on(SELECTED,f);
		};
		/**
		 * Clears the button.
		 */
		function reset()
		{
			selection = null;
			caption.text(label);
			ul.empty();
			button.addClass('btn-inverse');
			button.removeClass('btn-primary');
			return w;
		}
		/**
		 * Populates the control with data.
		 * @param data the data required by the widget to render itself.
		 */
		w.update = function(data)
		{
			reset();
			// render new list items.
			data.forEach(function(d)
			{
				var id = a.assertValue(d.id,'No id in datum.');
				var name = a.assertValue(d.name,'No name in datum.');
				// clone is very important here!
				ul.append(li.clone().data('id',d.id).text(d.name));
			});
			// subscribe to clicks on the new list items.
			// respond to these clicks by digging data out of the event and publishing them to the listeners.
			ul.find('li').click(function(event)
			{
				var target = $(event.delegateTarget);
				var id = target.data('id');
				// suppress redundant notifications.
				if (!a.isValue(selection) || selection.id !== id)
				{
					var name = target.text();
					selection = {'id':id,'name':name};
					button.addClass('btn-primary');
					button.removeClass('btn-inverse');
					caption.text(name);
					events.trigger(SELECTED,selection);
				}
			});
			return this;
		};
		reset();
		return w;
	};
	return my;
});