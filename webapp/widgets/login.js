/**
 * Renders the login dialog.
 * @type {*}
 */
define(['libs/assert', 'libs/util', 'libs/templates'
	, 'text!widgets/login.html'
	],
function(a, u, t, html)
{
	'use strict';
	var my = {};
	var template = t.stripComments(t.snippet($(html), 'login'));
	var OK = 'ok';
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
		var username = t.snippet(html,'username');
		var password = t.snippet(html,'password');
		var remember = t.snippet(html,'remember');
		var biffed = t.snippet(html,'biffed');
		var ok = t.snippet(html,'ok');
		var cancel = t.snippet(html,'cancel');
		ok.click(function(event)
		{
			event.preventDefault();
			events.trigger(OK);
		});
		cancel.click(function()
		{
			w.hide();
		});
		/**
		 * The function given here will take the form values and either return normally, this signalling that the dialog should be dismissed, or throw an error,
		 * in which case the dialog will display the error.
		 * @param f
		 */
		w.onLogin = function(f)
		{
			events.on(OK, function()
			{
				var n = username.val();
				var p = password.val();
				var s = remember.is(':checked');
				f(n, p, s);
			});
		};
		w.show = function()
		{
			html.modal({'show':true,'backdrop':true});
			biffed.hide();
			username.focus();
		};
		w.hide = function()
		{
			html.modal('hide');
			// clearing the values is very important: these values persist in the dialog's DOM!
			username.val('');
			password.val('');
			remember.prop('checked', false);
		}
		/**
		 * Show the alert that the login is invalid.
		 */
		w.natter = function(e)
		{
			biffed.text(JSON.stringify(e));
			biffed.show();
			username.focus();
		};
		return w;
	};
	return my;
});
