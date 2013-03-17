/**
 * Renders the login dialog.
 * @type {*}
 */
define(['libs/assert', 'libs/util', 'libs/templates'
	, 'text!widgets/create-project.html'
	, 'widgets/skills-picker'
],
	function(a, u, t, html, skillsPickerF)
	{
		'use strict';
		var my = {};
		var template = t.stripComments(t.snippet($(html), 'head'));
		var SUBMIT = 'submit';
		/**
		 * Create the widget.
		 * @param target the target html element where this instance will be rendered.
		 */
		my.make = function(target)
		{
			var w = {};
			// this name very deliberately shadows the html name in the outer scope.
			var html = template.clone();
			html.hide();
			target.replaceWith(html);
			var events = t.events({});
			var projectName = t.snippet(html, 'name');
			var skillsPicker = skillsPickerF.make (t.snippet(html, 'skills-picker'), 2);
			var submit = t.snippet(html, 'submit');
			submit.text("Create");
			var cancel = t.snippet(html, 'cancel');
			submit.click(function(event)
			{
				event.preventDefault();
				events.trigger(SUBMIT);
			});
			cancel.click(function()
			{
				w.hide();
			});
			w.onSubmit = function(f)
			{
				events.on(SUBMIT, function()
				{
					f(projectName.val(), skillsPicker.selectedItems());
				});
			};
			w.show = function(sks)
			{
				skillsPicker.addItems(sks);
				html.modal({'show':true, 'backdrop':true});
				projectName.focus();
			};
			w.hide = function()
			{
				html.modal('hide');
				// clearing the values is very important: these values persist in the dialog's DOM!
				projectName.val('');
			}
			/**
			 * Show the alert that the login is invalid.
			 */
			w.natter = function (e,u,p)
			{
				alert('There were complications:\n'+JSON.stringify(e)+'\n'+JSON.stringify(u)+'\n'+JSON.stringify(p));
				projectName.focus();
			};
			return w;
		};
		return my;
	});
