/**
 * Renders the top navigation bit.
 * @type {*}
 */
define(
	[ 'libs/assert', 'libs/util', 'libs/templates'
	, 'text!widgets/topnav.html'
	],
function(a, u, t, html)
{
	'use strict';
	var my = {};
	var template = t.stripComments(t.snippet($(html), 'topnav'));
	// event names
	var MAIN = 'MAIN';
	var REGISTER = 'REGISTER';
	var LOGIN = 'LOGIN';
	var LOGOUT = 'LOGOUT';
	var MANAGE_ACCOUNT = 'MANAGE-ACCOUNT';
	var SKILLS = 'SKILLS';
	var PROJECTS = 'PROJECTS';
	/**
	 * Create the widget.
	 * @param target the target html element where this instance will be rendered.
	 */
	my.make = function(target)
	{
		// make a new widget.
		var w = {};
		// this name very deliberately shadows the html name in the outer scope.
		// it's very important to clone here because it's possible to modify the template's html and have it either not render or globally change
		// every single instance of the template! fun!
		var html = template.clone();
		/*
		 * Snippets of our template which we'll use for events and rendering.
		 */
		var panel = t.snippet(html,'link-panel')
		var main = t.snippet(html,'main')
		var usermenu = t.snippet(html, 'usermenu');
		var username = t.snippet(html, 'username');
		var nonusermenu = t.snippet(html, 'nonusermenu');
		var login = t.snippet(html, 'login');
		var logout = t.snippet(html, 'logout');
		var manageAccount = t.snippet(html, 'manage-account');
		var register = t.snippet(html, 'register');
 		var skills = t.snippet(html, 'skills');
		var projects = t.snippet(html, 'projects');
		/*
		 * Our private events hub.
		 */
		var events = new t.Events();
		target.replaceWith(html);
		panel.hide();
		/*
		 * Internal event listeners.
		 * We're mapping private click events to triggers on our private events object.
		 * Elsewhere, we'll install handlers for those triggers and use them to notify containers regarding the user's selections.
		 */
		events.onClick(main, true, MAIN, null);
		events.onClick(register, false, REGISTER, null);
		events.onClick(login, false, LOGIN, null);
		events.onClick(logout, false, LOGOUT, null);
		events.onClick(manageAccount, false, MANAGE_ACCOUNT, null);
		events.onClick(skills, true, SKILLS, null);
		events.onClick(projects, true, PROJECTS, null);
		/*
		 * Subscriptions.
		 */
		w.onMain = events.publish(MAIN);
		w.onLogin = events.publish(LOGIN);
		w.onRegistration = events.publish(REGISTER);
		w.onLogout = events.publish(LOGOUT);
		w.onManageAccount = events.publish(MANAGE_ACCOUNT);
		w.onSkills = events.publish(SKILLS);
		w.onProjects = events.publish(PROJECTS);
		/**
		 * Announce the user to the control and it will switch right hand menus.
		 * @param name The name to display,
		 */
		w.update = function(name)
		{
			username.text(name);
			usermenu.show();
			nonusermenu.hide();
			panel.show();
		};
		/**
		 * Call this when the user logs off to revert to the nonuser menu.
		 */
		w.clear = function ()
		{
			usermenu.hide();
			nonusermenu.show();
			panel.hide();
		};
		// put it in its default state which forces the container to check for login cookies and such.
		w.clear();
		return w;
	};
	return my;
});
