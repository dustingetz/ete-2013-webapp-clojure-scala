/**
 * Projects panel.
 */
define([ 'libs/assert', 'libs/util', 'libs/templates'
	, 'text!widgets/projects.html'
],
	function(a, u, t, html)
	{
		'use strict';
		var my = {};
		var template = t.stripComments(t.snippet($(html), 'template'));
		// modes (which are also events)
		var OWNED='OWNED';
		var JOINED='JOINED';
		var ELIGIBLE='ELIGIBLE';
		// other events
		var CREATE = 'CREATE';
		var PROJECT_JOIN = 'PROJECT-JOIN';
		var PROJECT_DELETE = 'PROJECT-DELETE';
		var PROJECT_LEAVE = 'PROJECT-LEAVE';
		/**
		 * Create the widget.
		 * @param target where to put the html.
		 */
		my.make = function(target)
		{
			var w = {};
			// this name very deliberately shadows the html name in the outer scope.
			var html = template.clone();
			var count = t.snippet(html, 'count');
			var projects = t.snippet(html, 'projects');
			var project = t.snippet(html, 'project');
			var owned = t.snippet(html, 'owned');
			var joined = t.snippet(html, 'joined');
			var eligible = t.snippet(html, 'eligible');
			var create = t.snippet(html, 'create');
			var events = new t.events({});
			var mode = OWNED;
			count.text('0');
			projects.empty();
			target.replaceWith(html);
			/**
			 * Call this to force a reload of the panel (e.g. after a project has been affected and the displayed list would change).
			 */
			w.reload = function() { events.trigger(mode); };
			w.onOwned = function(f) { events.on(OWNED, f); };
			owned.click(function(event)
			{
				mode = OWNED;
				event.preventDefault();
				events.trigger(OWNED);
			});
			w.onJoined = function(f) { events.on(JOINED, f); };
			joined.click(function(event)
			{
				mode = JOINED;
				event.preventDefault();
				events.trigger(JOINED);
			});
			w.onEligible = function(f) { events.on(ELIGIBLE, f); };
			eligible.click(function(event)
			{
				mode = ELIGIBLE;
				event.preventDefault();
				events.trigger(ELIGIBLE);
			});
			w.onCreate = function(f)
			{
				events.on(CREATE, f);
			};
			create.click(function(event)
			{
				event.preventDefault();
				events.trigger(CREATE);
			});
			// project button handlers
			w.onProjectJoin = function(f) { events.on(PROJECT_JOIN, f); };
			w.onProjectLeave = function(f) { events.on(PROJECT_LEAVE, f); };
			w.onProjectDelete = function(f) { events.on(PROJECT_DELETE, f); };

			w.addItems = function(items)
			{
				projects.empty();
				count.text(''+items.length);
				var nth = 0;
				_.each(items, function(item)
				{
					var p = project.clone();
					var a = t.snippet(p, 'action');
					var skills = t.snippet(p, 'skills');
					var skill = t.snippet(skills, 'skill');
					var memberList = t.snippet(p, 'members');
					var member = t.snippet(memberList, 'member');
					var first = false; // for formatting lists, below.
					nth += 1;
					t.snippet(p, 'nth').text('' + nth);
					t.snippet(p, 'owner').text(item.owner.name);
					t.snippet(p, 'name').text(item.name+' ['+item.id+']');
					if (mode === ELIGIBLE)
					{
						a.addClass('btn-success').text('Join');
						a.click(function()
						{
							events.trigger(PROJECT_JOIN, item.id);
						});
					}
					else if (mode === OWNED)
					{
						a.addClass('btn-danger').text('Delete');
						a.click(function()
						{
							events.trigger(PROJECT_DELETE, item.id);
						});
					}
					else if (mode === JOINED)
					{
						a.addClass('btn-warning').text('Leave');
						a.click(function()
						{
							events.trigger(PROJECT_LEAVE, item.id);
						});
					}
					skills.empty();
					first = true;
					_.each(item.skills, function(q)
					{
						var y = '';
						if (first) { first = false } else { y = ', ' }
						skills.append(skill.clone().text(y+q.name));
					});
					memberList.empty();
					first = true;
					_.each(item.members, function(q)
					{
						var y = '';
						if (first) { first = false } else { y = ', ' }
						memberList.append(member.clone().text(y+q.name));
					});
					projects.append(p);
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