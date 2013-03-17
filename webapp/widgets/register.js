/**
 * Renders the registration dialog.
 * @type {*}
 */
define(['libs/assert', 'libs/util', 'libs/templates'
	, 'text!widgets/register.html'
],
	function(a, u, t, html)
	{
		'use strict';
		var my = {};
		var template = t.stripComments(t.snippet($(html), 'register'));
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
			var firstName = t.snippet(html, "first-name");
			var lastName = t.snippet(html, "last-name");
			var email = t.snippet(html, "e-mail");
			var username = t.snippet(html, "username");
			var password1 = t.snippet(html, "password-1");
			var password2 = t.snippet(html, "password-2");
			var ok = t.snippet(html, "ok");
			var cancel = t.snippet(html, "cancel");
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
			w.onRegister = function(f)
			{
				events.on(OK, function()
				{
					var fn = firstName.val();
					var ln = lastName.val();
					var e = email.val();
					var n = username.val();
					var p1 = password1.val();
					var p2 =  password2.val();
					f(fn, ln, e, n, p1, p2);
				});
			};
			w.natter = function(e)
			{
				alert('registration failed. you lose. you get nothing. good day, sir.\n'+u.showJSON(e));// + u.showJSON(e));
			};
			w.show = function()
			{
				html.modal({'show':true,'backdrop':true});
				firstName.focus();
			};
			w.hide = function()
			{
				html.modal('hide');
				// clearing the values is very important: these values persist in the dialog's DOM!
				firstName.val('');
				lastName.val('');
				email.val('');
				username.val('');
				password1.val('');
				password2.val('');
			}
			return w;
		};
		return my;
	});
