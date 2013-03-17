/**
 * A collection of utilities for testing values and asserting.
 * Most of the asserts return the test value so you can inline a test with an assignment.
 */
define([],
	function()
	{
		'use strict';
		var my = {};
		/**
		 * Alerts then throws a message.
		 * @param m the message.
		 */
		my.fail = function(m)
		{
			alert("ERROR\n" + m);
			throw m;
		};
		// Returns true iff x is defined and not null.
		my.isValue = function(x) { return ! (_.isUndefined(x) || _.isNull(x)); };
		// Returns true iff x is undefined or null.
		my.isNotValue = function(x) { return _.isUndefined(x) || _.isNull(x); };
		// Fails with message iff b is false.
		my.assert = function(b, m) { if (! b) my.fail(m); return b; };
		// Fails with message if x is undefined else returns x.
		my.assertDef = function(x, m) { if (_.isNull(x)) my.fail(m); return x; };
		// Fails with message if x is defined else returns x.
		my.assertUndef = function(x, m) { if (! _.isUndefined(x)) my.fail(m); return x; };
		// Fails with message if x is not null else returns x.
		my.assertNull = function(x, m) { if (_.isNull(x)) my.fail(m); return x; };
		// Fails with message if x is null else returns x.
		my.assertNotNull = function(x, m) { if (_.isNull(x)) my.fail(m); return x; };
		// Fails with message if x is a value else returns x.
		my.assertValue = function(x, m) { if (my.isNotValue(x)) my.fail(m); return x; };
		// Fails with message if x is not a value else returns x.
		my.assertNotValue = function(x, m) { if (my.isValue(x)) my.fail(m); return x; };
		// off ya go!
		return my;
	}
);